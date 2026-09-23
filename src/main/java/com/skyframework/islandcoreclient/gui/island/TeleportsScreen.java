package com.skyframework.islandcoreclient.gui.island;

import com.skyframework.islandcoreclient.gui.common.BaseMenuScreen;
import com.skyframework.islandcoreclient.gui.common.PagedFlagGrid;
import com.skyframework.islandcoreclient.network.ClientErrorToasts;
import com.skyframework.islandcoreclient.network.PendingActionTracker;
import com.skyframework.islandcoreclient.network.teleport.TeleportRequestC2S;
import com.skyframework.islandcoreclient.network.teleport.TeleportStatusRequestC2S;
import com.skyframework.islandcoreclient.state.ClientDimensionTeleportView;
import com.skyframework.islandcoreclient.state.ClientIslandCache;
import com.skyframework.islandcoreclient.state.ClientTeleportState;
import com.skyframework.islandcoreclient.state.ClientTeleportType;

import net.neoforged.neoforge.network.PacketDistributor;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * HOME/SPAWN/RTP stay fixed rows at the top, unchanged from before this sprint. Below them, a
 * paginated grid of buttons — one per {@code DIMENSION_REGISTRY} dimension, {@link ClientIslandCache
 * #getDimensionTeleports()} — replaces the single hardcoded "Farming" button that used to sit as a
 * 4th fixed row (Sprint "teletransportes dinámicos"). Same {@link PagedFlagGrid} 2-column
 * pagination + "islandcoreclient.pagination.prev/next/page_indicator" lang keys SettingsScreen's
 * "Permisos" tab already uses, buttons at the bottom of the content — established pattern, not a
 * new one.
 *
 * <p>The dimension whose id matches the server's FarmingConfig target (if any) is just another
 * entry in that list — it keeps its own cooldown (rendered as a "(MM:SS)" suffix on its button,
 * same countdown math as the fixed rows via {@link ClientTeleportState}) purely because the server
 * says so via {@code enabled}/{@code cooldownRemainingSeconds}, not because this screen special-cases it.
 */
public class TeleportsScreen extends BaseMenuScreen {
	private static final int CONTENT_X = 16;
	private static final int ROW_HEIGHT = 20;
	private static final int REASON_LINE_HEIGHT = 11;
	private static final int ROW_GAP = 6;
	private static final int BUTTON_WIDTH = 130;

	private static final int SECTION_GAP = 10;
	private static final int GRID_MAX_WIDTH = 480;
	private static final int GRID_COLUMN_GAP = 24;
	private static final int GRID_ROW_SPACING = 6;
	private static final int PAGINATION_ROW_HEIGHT = 20;
	private static final int PAGINATION_GAP = 8;
	private static final int PAGINATION_BUTTON_WIDTH = 90;
	private static final int CONTENT_BOTTOM_MARGIN = 12;

	private final PagedFlagGrid dimensionGrid =
			new PagedFlagGrid(0, 0, 1, 1, ROW_HEIGHT, GRID_ROW_SPACING, GRID_COLUMN_GAP);

	public TeleportsScreen(Screen parent) {
		super(Component.translatable("islandcoreclient.teleports.title"), parent);
	}

	// Called by ClientPacketHandlers when a fresh TeleportStatusS2C lands while this screen is
	// open — Screen#clearAndInit() itself is protected, so this is the public door into it.
	public void refreshFromNetwork() {
		this.rebuildWidgets();
	}

	@Override
	protected void initContent() {
		PacketDistributor.sendToServer(new TeleportStatusRequestC2S());

		int y = TOP_BAR_HEIGHT + 8;
		int buttonX = this.width - 16 - BUTTON_WIDTH;

		for (ClientTeleportType type : ClientTeleportType.values()) {
			ClientTeleportState state = ClientIslandCache.getTeleportState(type);

			Component label;
			boolean active;
			if (!state.isEnabled()) {
				label = Component.literal("-");
				active = false;
			} else if (state.getCooldownRemainingSeconds() > 0) {
				long remaining = state.getCooldownRemainingSeconds();
				label = Component.literal(String.format("%02d:%02d", remaining / 60, remaining % 60));
				active = false;
			} else {
				label = Component.translatable("islandcoreclient.teleports.teleport_button");
				active = true;
			}

			Button button = this.addRenderableWidget(Button.builder(label, b -> onFixedTeleportClicked(type))
					.bounds(buttonX, y, BUTTON_WIDTH, ROW_HEIGHT)
					.build());
			button.active = active;

			y += ROW_HEIGHT;
			if (!state.isEnabled()) {
				y += REASON_LINE_HEIGHT;
			}
			y += ROW_GAP;
		}

		// Fixed row for the real vanilla Overworld, alongside Home/Spawn/RTP — deliberately NOT a
		// ClientTeleportType entry: that enum drives label/cooldown state fed by TeleportStatusS2C,
		// which only knows about Home/Spawn/RTP; the Overworld has no cooldown/enabled concept of its
		// own, so this is just a plain always-active button sending Type.OVERWORLD directly.
		Button overworldButton = this.addRenderableWidget(Button.builder(
						Component.translatable("islandcoreclient.teleports.teleport_button"),
						b -> sendTeleportRequest(TeleportRequestC2S.fixed(TeleportRequestC2S.Type.OVERWORLD)))
				.bounds(buttonX, y, BUTTON_WIDTH, ROW_HEIGHT)
				.build());
		overworldButton.active = true;
		y += ROW_HEIGHT + ROW_GAP;

		initDimensionSection(y + SECTION_GAP);
	}

	private void initDimensionSection(int sectionTop) {
		List<ClientDimensionTeleportView> dimensions = ClientIslandCache.getDimensionTeleports();

		int gridWidth = Math.min(GRID_MAX_WIDTH, this.width - 32);
		int gridX = this.width / 2 - gridWidth / 2;
		int gridViewportHeight = Math.max(ROW_HEIGHT,
				this.height - CONTENT_BOTTOM_MARGIN - PAGINATION_ROW_HEIGHT - PAGINATION_GAP - sectionTop);
		dimensionGrid.setViewport(gridX, sectionTop, gridWidth, gridViewportHeight);
		dimensionGrid.setItemCount(dimensions.size());

		for (int i = 0; i < dimensions.size(); i++) {
			if (!dimensionGrid.isItemOnCurrentPage(i)) {
				continue;
			}
			ClientDimensionTeleportView dimension = dimensions.get(i);
			ClientTeleportState state = dimension.state();

			Button button = this.addRenderableWidget(Button.builder(
							dimensionButtonLabel(dimension), b -> onDimensionTeleportClicked(dimension))
					.bounds(dimensionGrid.getItemX(i), dimensionGrid.getItemY(i), dimensionGrid.getItemWidth(), ROW_HEIGHT)
					.build());
			button.active = state.isEnabled() && state.getCooldownRemainingSeconds() <= 0;
		}

		int paginationY = paginationRowY();
		Button prevButton = this.addRenderableWidget(Button.builder(Component.translatable("islandcoreclient.pagination.prev"),
						b -> {
							dimensionGrid.prevPage();
							this.rebuildWidgets();
						})
				.bounds(gridX, paginationY, PAGINATION_BUTTON_WIDTH, PAGINATION_ROW_HEIGHT)
				.build());
		prevButton.active = dimensionGrid.hasPrevPage();

		Button nextButton = this.addRenderableWidget(Button.builder(Component.translatable("islandcoreclient.pagination.next"),
						b -> {
							dimensionGrid.nextPage();
							this.rebuildWidgets();
						})
				.bounds(gridX + gridWidth - PAGINATION_BUTTON_WIDTH, paginationY, PAGINATION_BUTTON_WIDTH, PAGINATION_ROW_HEIGHT)
				.build());
		nextButton.active = dimensionGrid.hasNextPage();
	}

	private int paginationRowY() {
		return this.height - CONTENT_BOTTOM_MARGIN - PAGINATION_ROW_HEIGHT;
	}

	// Unlike the fixed rows (whose name is drawn separately from the button, so the button itself
	// can just show "MM:SS" while on cooldown), a grid button here has nowhere else to put the
	// name — so the name always stays part of the label, with a "(MM:SS)" suffix appended instead
	// of replacing it, and greyed out entirely when the dimension is disabled server-side (mirrors
	// FarmingConfig.isEnabled() == false for whichever entry matches its target).
	private static Component dimensionButtonLabel(ClientDimensionTeleportView dimension) {
		Component name = Component.literal(dimension.displayName());
		ClientTeleportState state = dimension.state();
		if (!state.isEnabled()) {
			return name.copy().withStyle(ChatFormatting.GRAY);
		}
		long remaining = state.getCooldownRemainingSeconds();
		if (remaining > 0) {
			return name.copy().append(Component.literal(String.format(" (%02d:%02d)", remaining / 60, remaining % 60)));
		}
		return name;
	}

	@Override
	protected void renderContent(GuiGraphics context, int mouseX, int mouseY, float delta) {
		int y = TOP_BAR_HEIGHT + 8;

		for (ClientTeleportType type : ClientTeleportType.values()) {
			ClientTeleportState state = ClientIslandCache.getTeleportState(type);

			context.drawString(this.font, type.label(),
					CONTENT_X, y + (ROW_HEIGHT - this.font.lineHeight) / 2, 0xFFFFFF);
			y += ROW_HEIGHT;

			if (!state.isEnabled() && state.reasonKey() != null) {
				context.drawString(this.font, Component.translatable(state.reasonKey()), CONTENT_X + 8, y, 0xAAAAAA);
			}
			if (!state.isEnabled()) {
				y += REASON_LINE_HEIGHT;
			}
			y += ROW_GAP;
		}

		context.drawString(this.font, Component.translatable("islandcoreclient.teleports.type.overworld"),
				CONTENT_X, y + (ROW_HEIGHT - this.font.lineHeight) / 2, 0xFFFFFF);

		Component indicator = Component.translatable("islandcoreclient.pagination.page_indicator",
				dimensionGrid.getCurrentPage() + 1, dimensionGrid.totalPages());
		int textWidth = this.font.width(indicator);
		context.drawString(this.font, indicator, this.width / 2 - textWidth / 2,
				paginationRowY() + (PAGINATION_ROW_HEIGHT - this.font.lineHeight) / 2, 0xAAAAAA);
	}

	// Closes the ENTIRE menu immediately per design (the player gets full control back, as if
	// they'd never opened it) — not just this screen. this.close() is deliberately NOT used here:
	// BaseMenuScreen overrides close() to always go to its parent (that's what powers the
	// "← Volver" button), so calling it from here would just return to the Dashboard instead of
	// releasing the screen entirely. client.setScreen(null) is vanilla Screen#close()'s own
	// un-overridden behavior (confirmed via javap), gotten here directly since BaseMenuScreen's
	// override sits between this class and it.
	//
	// The screen must not block player movement while the teleport resolves server-side.
	// HOME/SPAWN/DIMENSION's warmup progress and completion are communicated via chat messages from
	// TeleportManagerImpl, not this screen — only an immediate rejection (wrong dimension,
	// disabled, cooldown, etc.) surfaces here, as a toast, since by the time it arrives the menu
	// is usually already closed.
	private void onFixedTeleportClicked(ClientTeleportType type) {
		sendTeleportRequest(TeleportRequestC2S.fixed(TeleportRequestC2S.Type.valueOf(type.name())));
	}

	private void onDimensionTeleportClicked(ClientDimensionTeleportView dimension) {
		sendTeleportRequest(TeleportRequestC2S.dimension(dimension.id()));
	}

	private void sendTeleportRequest(TeleportRequestC2S request) {
		PacketDistributor.sendToServer(request);
		PendingActionTracker.await((success, reasonKey) -> {
			if (!success) {
				ClientErrorToasts.showReason(reasonKey);
			}
		});
		this.minecraft.setScreen(null);
	}
}
