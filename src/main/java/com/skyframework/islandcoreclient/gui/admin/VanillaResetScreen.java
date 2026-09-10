package com.skyframework.islandcoreclient.gui.admin;

import java.util.Locale;
import java.util.Optional;

import com.skyframework.islandcoreclient.gui.common.BaseMenuScreen;
import com.skyframework.islandcoreclient.network.ClientErrorToasts;
import com.skyframework.islandcoreclient.network.PendingActionTracker;
import com.skyframework.islandcoreclient.network.admin.vanilla.VanillaResetCancelC2S;
import com.skyframework.islandcoreclient.network.admin.vanilla.VanillaResetConfirmC2S;
import com.skyframework.islandcoreclient.network.admin.vanilla.VanillaResetListRequestC2S;
import com.skyframework.islandcoreclient.network.admin.vanilla.VanillaResetQueueC2S;
import com.skyframework.islandcoreclient.state.ClientIslandCache;
import com.skyframework.islandcoreclient.state.ClientResetDimension;
import com.skyframework.islandcoreclient.state.ClientVanillaResetState;

import net.neoforged.neoforge.network.PacketDistributor;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import org.jetbrains.annotations.Nullable;

/**
 * The 3 dimension rows are fixed ({@link ClientResetDimension}), not data-driven — only their
 * pending/queued state is. The queue is requested once from the constructor;
 * {@link #refreshFromNetwork()} only rebuilds from {@link ClientIslandCache}.
 *
 * <p>"Encolar" opens an options step (this same screen, {@link Mode#OPTIONS}) for the seed-mode
 * form; "Continuar" from there sends the real {@link VanillaResetQueueC2S} REQUEST (opening the
 * server's own 30s confirmation window — tracked here client-locally, same pattern as every other
 * 3-layer flow in this project) and moves to {@link Mode#PENDING}; "Confirmar" there sends
 * {@link VanillaResetConfirmC2S}, which actually enqueues the reset in {@code
 * pending_vanilla_reset.json} — only THEN does it show up as "queued" (Cancelar available) back
 * in {@link Mode#LIST} on the next {@link VanillaResetListRequestC2S} refetch.
 */
public class VanillaResetScreen extends BaseMenuScreen {
	private enum Mode {
		LIST,
		OPTIONS,
		PENDING
	}

	private static final long PENDING_WINDOW_SECONDS = 30L;
	private static final int CONTENT_X = 16;
	private static final int LINE_HEIGHT = 11;
	private static final int ROW_HEIGHT = 20;
	private static final int ROW_GAP = 8;
	private static final int LIST_START_Y = TOP_BAR_HEIGHT + 8 + LINE_HEIGHT * 2 + 12;
	private static final int SUB_BACK_Y = TOP_BAR_HEIGHT + 6;
	private static final int OPTIONS_WARNING_Y = SUB_BACK_Y + 22;
	private static final int OPTIONS_FORM_Y = OPTIONS_WARNING_Y + LINE_HEIGHT * 3 + 8;
	private static final int PENDING_WARNING_Y = SUB_BACK_Y + 22;
	private static final int PENDING_COUNTDOWN_Y = PENDING_WARNING_Y + LINE_HEIGHT + 4;
	// The countdown text above ends around PENDING_COUNTDOWN_Y + textRenderer.lineHeight (~9px); a
	// flat "+ 44" here previously left only ~2px of clearance, letting the button's top edge touch
	// the "Confirmando..." countdown text. A small explicit margin below the text fixes that.
	private static final int PENDING_CONFIRM_BUTTON_Y = PENDING_COUNTDOWN_Y + LINE_HEIGHT + 8;

	private Mode mode = Mode.LIST;
	@Nullable
	private ClientResetDimension selectedDimension;
	private ClientVanillaResetState.SeedMode selectedSeedMode = ClientVanillaResetState.SeedMode.RANDOM;
	private EditBox seedField;

	// 0 = not counting down. Screen-local: nothing is committed until the real
	// VanillaResetConfirmC2S succeeds, exactly like DeleteIslandScreen.
	private long pendingExpiresAtMillis = 0L;

	public VanillaResetScreen(Screen parent) {
		super(Component.translatable("islandcoreclient.admin.vanilla_reset.title"), parent);
		PacketDistributor.sendToServer(new VanillaResetListRequestC2S());
	}

	// Called by ClientPacketHandlers when a fresh VanillaResetListS2C lands while this screen is
	// open — same pattern as TeleportsScreen/BiomeScreen.
	public void refreshFromNetwork() {
		this.rebuildWidgets();
	}

	@Override
	protected void initContent() {
		switch (this.mode) {
			case LIST -> initListContent();
			case OPTIONS -> initOptionsContent();
			case PENDING -> initPendingContent();
		}
	}

	private void initListContent() {
		int y = LIST_START_Y;
		for (ClientResetDimension dimension : ClientResetDimension.values()) {
			ClientVanillaResetState state = ClientIslandCache.getVanillaResetState(dimension);
			if (state.isPending()) {
				this.addRenderableWidget(Button.builder(
								Component.translatable("islandcoreclient.admin.vanilla_reset.cancel_button"),
								button -> onCancelClicked(dimension))
						.bounds(CONTENT_X + 200, y, 150, ROW_HEIGHT)
						.build());
			} else {
				this.addRenderableWidget(Button.builder(
								Component.translatable("islandcoreclient.admin.vanilla_reset.queue_button"),
								button -> onQueueClicked(dimension))
						.bounds(CONTENT_X + 200, y, 150, ROW_HEIGHT)
						.build());
			}
			y += rowHeight(state) + ROW_GAP;
		}
	}

	private void addBackToListButton() {
		this.addRenderableWidget(Button.builder(
						Component.translatable("islandcoreclient.admin.vanilla_reset.back_to_list"),
						button -> {
							this.mode = Mode.LIST;
							this.pendingExpiresAtMillis = 0L;
							PacketDistributor.sendToServer(new VanillaResetListRequestC2S());
							this.rebuildWidgets();
						})
				.bounds(CONTENT_X, SUB_BACK_Y, 120, 16)
				.build());
	}

	private void initOptionsContent() {
		addBackToListButton();

		int y = OPTIONS_FORM_Y;
		for (ClientVanillaResetState.SeedMode seedMode : ClientVanillaResetState.SeedMode.values()) {
			Component label = seedMode == this.selectedSeedMode ? Component.literal("✓ ").append(seedModeLabel(seedMode)) : seedModeLabel(seedMode);
			this.addRenderableWidget(Button.builder(label, button -> {
						this.selectedSeedMode = seedMode;
						this.rebuildWidgets();
					})
					.bounds(CONTENT_X, y, 200, 20)
					.build());
			y += 24;
		}

		// Aligned with the SPECIFIED row above (the last one drawn in the loop).
		this.seedField = new EditBox(this.font, CONTENT_X + 210, y - 24, 120, 20,
				Component.translatable("islandcoreclient.admin.vanilla_reset.seed_field"));
		this.seedField.setFilter(text -> text.isEmpty() || text.matches("-?[0-9]{1,19}"));
		boolean specified = this.selectedSeedMode == ClientVanillaResetState.SeedMode.SPECIFIED;
		this.seedField.setEditable(specified);
		this.seedField.active = specified;
		this.addRenderableWidget(this.seedField);

		y += 16;
		this.addRenderableWidget(Button.builder(
						Component.translatable("islandcoreclient.admin.vanilla_reset.continue_button"),
						button -> onContinueClicked())
				.bounds(CONTENT_X, y, 150, 20)
				.build());
	}

	private void initPendingContent() {
		addBackToListButton();

		this.addRenderableWidget(Button.builder(
						Component.translatable("islandcoreclient.admin.vanilla_reset.confirm_button"),
						button -> onConfirmQueueClicked())
				.bounds(CONTENT_X, PENDING_CONFIRM_BUTTON_Y, 200, 20)
				.build());
	}

	@Override
	protected void renderContent(GuiGraphics context, int mouseX, int mouseY, float delta) {
		switch (this.mode) {
			case LIST -> renderListContent(context);
			case OPTIONS -> renderOptionsContent(context);
			case PENDING -> renderPendingContent(context);
		}
	}

	private void renderListContent(GuiGraphics context) {
		context.drawString(this.font,
				Component.translatable("islandcoreclient.admin.vanilla_reset.warning_line1").withStyle(ChatFormatting.GOLD),
				CONTENT_X, TOP_BAR_HEIGHT + 8, 0xFFFFFF);
		context.drawString(this.font,
				Component.translatable("islandcoreclient.admin.vanilla_reset.warning_line2"),
				CONTENT_X, TOP_BAR_HEIGHT + 8 + LINE_HEIGHT, 0xAAAAAA);

		int y = LIST_START_Y;
		for (ClientResetDimension dimension : ClientResetDimension.values()) {
			ClientVanillaResetState state = ClientIslandCache.getVanillaResetState(dimension);
			context.drawString(this.font, dimension.label(),
					CONTENT_X, y + (ROW_HEIGHT - this.font.lineHeight) / 2, 0xFFFFFF);
			if (state.isPending()) {
				context.drawString(this.font, seedModeLabel(state.seedMode()), CONTENT_X, y + ROW_HEIGHT + 1, 0xAAAAAA);
			}
			y += rowHeight(state) + ROW_GAP;
		}
	}

	private void renderOptionsContent(GuiGraphics context) {
		context.drawString(this.font,
				Component.translatable("islandcoreclient.admin.vanilla_reset.warning_line1").withStyle(ChatFormatting.GOLD),
				CONTENT_X, OPTIONS_WARNING_Y, 0xFFFFFF);
		context.drawString(this.font,
				Component.translatable("islandcoreclient.admin.vanilla_reset.warning_line2"),
				CONTENT_X, OPTIONS_WARNING_Y + LINE_HEIGHT, 0xAAAAAA);

		boolean anotherSpecified = false;
		for (ClientResetDimension dimension : ClientResetDimension.values()) {
			if (dimension == this.selectedDimension) {
				continue;
			}
			ClientVanillaResetState state = ClientIslandCache.getVanillaResetState(dimension);
			if (state.isPending() && state.seedMode() == ClientVanillaResetState.SeedMode.SPECIFIED) {
				anotherSpecified = true;
				break;
			}
		}
		if (anotherSpecified) {
			context.drawString(this.font,
					Component.translatable("islandcoreclient.admin.vanilla_reset.warning_specified_conflict"),
					CONTENT_X, OPTIONS_WARNING_Y + LINE_HEIGHT * 2, 0xFF5555);
		}
	}

	private void renderPendingContent(GuiGraphics context) {
		if (this.pendingExpiresAtMillis > 0 && System.currentTimeMillis() >= this.pendingExpiresAtMillis) {
			this.mode = Mode.LIST;
			this.pendingExpiresAtMillis = 0L;
			PacketDistributor.sendToServer(new VanillaResetListRequestC2S());
			this.rebuildWidgets();
			return;
		}

		long remaining = Math.max(0L, (this.pendingExpiresAtMillis - System.currentTimeMillis()) / 1000L);
		context.drawString(this.font,
				Component.translatable("islandcoreclient.admin.vanilla_reset.evacuate_warning").withStyle(ChatFormatting.RED),
				CONTENT_X, PENDING_WARNING_Y, 0xFFFFFF);
		context.drawString(this.font,
				Component.translatable("islandcoreclient.admin.vanilla_reset.countdown", remaining),
				CONTENT_X, PENDING_COUNTDOWN_Y, 0xFFCC55);
	}

	private static int rowHeight(ClientVanillaResetState state) {
		return state.isPending() ? ROW_HEIGHT + LINE_HEIGHT + 2 : ROW_HEIGHT;
	}

	private static Component seedModeLabel(ClientVanillaResetState.SeedMode seedMode) {
		return Component.translatable("islandcoreclient.admin.vanilla_reset.seed_mode_" + seedMode.name().toLowerCase(Locale.ROOT));
	}

	private void onQueueClicked(ClientResetDimension dimension) {
		this.selectedDimension = dimension;
		this.selectedSeedMode = ClientVanillaResetState.SeedMode.RANDOM;
		this.mode = Mode.OPTIONS;
		this.rebuildWidgets();
	}

	private void onContinueClicked() {
		if (this.selectedDimension == null) {
			return;
		}
		if (this.selectedSeedMode == ClientVanillaResetState.SeedMode.SPECIFIED && parseSeed(this.seedField.getValue()) == null) {
			return;
		}

		String dimensionKey = this.selectedDimension.name().toLowerCase(Locale.ROOT);
		// The server only understands "CUSTOM" (explicit seed) or "DEFAULT" (defer to its own
		// VanillaResetConfig) — RANDOM and KEEP both map to "DEFAULT" here, since requestReset has
		// no 3-way mode of its own; see VanillaResetQueueC2S's javadoc.
		String seedMode = this.selectedSeedMode == ClientVanillaResetState.SeedMode.SPECIFIED ? "CUSTOM" : "DEFAULT";
		Optional<Long> seedValue = this.selectedSeedMode == ClientVanillaResetState.SeedMode.SPECIFIED
				? Optional.ofNullable(parseSeed(this.seedField.getValue()))
				: Optional.empty();

		PacketDistributor.sendToServer(new VanillaResetQueueC2S(dimensionKey, seedMode, seedValue));
		PendingActionTracker.await((success, reasonKey) -> {
			if (success) {
				this.mode = Mode.PENDING;
				this.pendingExpiresAtMillis = System.currentTimeMillis() + PENDING_WINDOW_SECONDS * 1000L;
			} else {
				ClientErrorToasts.showReason(reasonKey);
			}
			this.rebuildWidgets();
		});
	}

	private void onConfirmQueueClicked() {
		if (this.selectedDimension == null) {
			this.mode = Mode.LIST;
			this.pendingExpiresAtMillis = 0L;
			this.rebuildWidgets();
			return;
		}

		String dimensionKey = this.selectedDimension.name().toLowerCase(Locale.ROOT);
		PacketDistributor.sendToServer(new VanillaResetConfirmC2S(dimensionKey));
		PendingActionTracker.await((success, reasonKey) -> {
			this.mode = Mode.LIST;
			this.pendingExpiresAtMillis = 0L;
			if (success) {
				PacketDistributor.sendToServer(new VanillaResetListRequestC2S());
			} else {
				ClientErrorToasts.showReason(reasonKey);
			}
			this.rebuildWidgets();
		});
	}

	private void onCancelClicked(ClientResetDimension dimension) {
		String dimensionKey = dimension.name().toLowerCase(Locale.ROOT);
		PacketDistributor.sendToServer(new VanillaResetCancelC2S(dimensionKey));
		PendingActionTracker.await((success, reasonKey) -> {
			if (success) {
				PacketDistributor.sendToServer(new VanillaResetListRequestC2S());
			} else {
				ClientErrorToasts.showReason(reasonKey);
			}
			this.rebuildWidgets();
		});
	}

	@Nullable
	private static Long parseSeed(String text) {
		try {
			return Long.parseLong(text.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
