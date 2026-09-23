package com.skyframework.islandcoreclient.hud;

import com.skyframework.islandcoreclient.state.ClientAllyLocationView;
import com.skyframework.islandcoreclient.state.ClientAllyLocationsCache;

import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.common.NeoForge;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * NEOFORGE PORT: Fabric's HudRenderCallback has no 1:1 NeoForge equivalent — the closest is a GUI
 * layer registered via {@link RegisterGuiLayersEvent}, rendered on {@link RenderGuiLayerEvent.Post}
 * (kept separate from any vanilla layer via {@code registerAboveAll}, same "draws on top of
 * everything else" position HudRenderCallback had). Everything below this class javadoc — the
 * angular-projection math itself — is untouched from the Fabric source; only the render entry point
 * and the Mojmap API calls it makes (GuiGraphics/PlayerInfo/PlayerSkin/Camera) changed.
 *
 * <p>Draws a small icon (the target's own player head via {@link PlayerFaceRenderer} when the
 * client already has their {@link PlayerInfo} cached, a plain colored square otherwise) for every
 * entry in {@link ClientAllyLocationsCache}, positioned by ANGLE relative to the camera rather
 * than a true 3D-to-screen projection matrix: the angular offset (target yaw/pitch minus camera
 * yaw/pitch) is compared against the current FOV to decide in-view vs. off-screen, then mapped
 * linearly to a screen position. In view, that gives the icon's actual on-screen spot; off
 * screen, the same unclamped linear position is clamped to the nearest point on the screen's
 * inset border along the ray from the screen center — the standard "off-screen direction arrow"
 * technique. Entries closer than {@link #HIDE_DISTANCE_BLOCKS} are skipped entirely, per spec.
 */
public final class AllyHudRenderer {
	private static final ResourceLocation LAYER_ID = ResourceLocation.fromNamespaceAndPath("islandcoreclient", "ally_hud");

	private static final double HIDE_DISTANCE_BLOCKS = 15.0;
	// Halved from 16 to 8, then halved again to 4 — still read as too large/intrusive on the HUD
	// at 8px per in-game feedback.
	private static final int ICON_SIZE = 4;
	private static final int EDGE_MARGIN = 20;
	private static final int LABEL_GAP = 2;

	private AllyHudRenderer() {
	}

	public static void registerGuiLayer(RegisterGuiLayersEvent event) {
		event.registerAboveAll(LAYER_ID, (context, tickCounter) -> { });
	}

	public static void register() {
		NeoForge.EVENT_BUS.addListener((RenderGuiLayerEvent.Post event) -> {
			if (event.getName().equals(LAYER_ID)) {
				onHudRender(event.getGuiGraphics(), event.getPartialTick());
			}
		});
	}

	private static void onHudRender(GuiGraphics context, DeltaTracker tickCounter) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.options.hideGui) {
			return;
		}

		List<ClientAllyLocationView> entries = ClientAllyLocationsCache.getEntries();
		if (entries.isEmpty()) {
			return;
		}

		Camera camera = client.gameRenderer.getMainCamera();
		Vec3 camPos = camera.getPosition();
		float camYaw = camera.getYRot();
		float camPitch = camera.getXRot();

		int screenWidth = client.getWindow().getGuiScaledWidth();
		int screenHeight = client.getWindow().getGuiScaledHeight();

		double fov = client.options.fov().get();
		double halfVFov = fov / 2.0;
		double aspect = (double) screenWidth / (double) screenHeight;
		double halfHFov = Math.toDegrees(Math.atan(Math.tan(Math.toRadians(halfVFov)) * aspect));

		for (ClientAllyLocationView entry : entries) {
			double dx = entry.x() - camPos.x;
			double dy = entry.y() - camPos.y;
			double dz = entry.z() - camPos.z;
			double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
			if (distance < HIDE_DISTANCE_BLOCKS) {
				continue;
			}

			double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
			// Standard Minecraft yaw convention: 0 = south (+Z), increasing clockwise viewed from
			// above; pitch: 0 = horizontal, positive = looking down.
			double targetYaw = Math.toDegrees(Math.atan2(-dx, dz));
			double targetPitch = Math.toDegrees(-Math.atan2(dy, horizontalDistance));

			double relativeYaw = wrapDegrees(targetYaw - camYaw);
			double relativePitch = targetPitch - camPitch;

			boolean inView = Math.abs(relativeYaw) < halfHFov && Math.abs(relativePitch) < halfVFov;

			double rawX = screenWidth / 2.0 + (relativeYaw / halfHFov) * (screenWidth / 2.0);
			double rawY = screenHeight / 2.0 + (relativePitch / halfVFov) * (screenHeight / 2.0);

			int drawX;
			int drawY;
			if (inView) {
				drawX = (int) Math.round(rawX);
				drawY = (int) Math.round(rawY);
			} else {
				int[] clamped = clampToRect(screenWidth / 2.0, screenHeight / 2.0, rawX, rawY,
						EDGE_MARGIN, EDGE_MARGIN, screenWidth - EDGE_MARGIN, screenHeight - EDGE_MARGIN);
				drawX = clamped[0];
				drawY = clamped[1];
			}

			drawMarker(context, client, entry, drawX, drawY);
		}
	}

	private static void drawMarker(GuiGraphics context, Minecraft client, ClientAllyLocationView entry, int centerX, int centerY) {
		int x = centerX - ICON_SIZE / 2;
		int y = centerY - ICON_SIZE / 2;

		PlayerInfo playerInfo = client.getConnection() != null ? client.getConnection().getPlayerInfo(entry.uuid()) : null;
		if (playerInfo != null) {
			PlayerFaceRenderer.draw(context, playerInfo.getSkin(), x, y, ICON_SIZE);
		} else {
			// Fallback generic icon: this client has no cached tab-list entry for that uuid (e.g.
			// never rendered them nearby this session) — a plain colored marker rather than
			// skipping the ally entirely, per spec.
			context.fill(x, y, x + ICON_SIZE, y + ICON_SIZE, 0xFF000000);
			context.fill(x + 1, y + 1, x + ICON_SIZE - 1, y + ICON_SIZE - 1, 0xFF55FF55);
		}

		Component nameText = Component.literal(entry.name());
		int textWidth = client.font.width(nameText);
		context.drawString(client.font, nameText, centerX - textWidth / 2, y + ICON_SIZE + LABEL_GAP, 0xFFFFFF);
	}

	private static double wrapDegrees(double degrees) {
		double result = degrees % 360.0;
		if (result >= 180.0) {
			result -= 360.0;
		} else if (result < -180.0) {
			result += 360.0;
		}
		return result;
	}

	// Scales the vector from (centerX, centerY) to (px, py) so it lands exactly on the border of
	// the inset rectangle [left, top, right, bottom] — the point where the ray from the center
	// through the raw (possibly far off-screen) position first crosses that border.
	private static int[] clampToRect(double centerX, double centerY, double px, double py, double left, double top, double right, double bottom) {
		double dx = px - centerX;
		double dy = py - centerY;
		if (dx == 0.0 && dy == 0.0) {
			return new int[] {(int) centerX, (int) centerY};
		}

		double halfWidth = (right - left) / 2.0;
		double halfHeight = (bottom - top) / 2.0;
		double scaleX = dx != 0.0 ? halfWidth / Math.abs(dx) : Double.MAX_VALUE;
		double scaleY = dy != 0.0 ? halfHeight / Math.abs(dy) : Double.MAX_VALUE;
		double scale = Math.min(scaleX, scaleY);

		return new int[] {(int) Math.round(centerX + dx * scale), (int) Math.round(centerY + dy * scale)};
	}
}
