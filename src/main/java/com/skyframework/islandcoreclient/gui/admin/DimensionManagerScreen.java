package com.skyframework.islandcoreclient.gui.admin;

import com.skyframework.islandcoreclient.gui.common.BaseMenuScreen;
import com.skyframework.islandcoreclient.gui.common.ToggleRow;
import com.skyframework.islandcoreclient.network.ClientErrorToasts;
import com.skyframework.islandcoreclient.network.PendingActionTracker;
import com.skyframework.islandcoreclient.network.admin.dimension.DimensionCreateC2S;
import com.skyframework.islandcoreclient.network.admin.dimension.DimensionDeleteC2S;
import com.skyframework.islandcoreclient.network.admin.dimension.DimensionDeleteConfirmC2S;
import com.skyframework.islandcoreclient.network.admin.dimension.DimensionDetailRequestC2S;
import com.skyframework.islandcoreclient.network.admin.dimension.DimensionListRequestC2S;
import com.skyframework.islandcoreclient.network.admin.dimension.DimensionRegenerateC2S;
import com.skyframework.islandcoreclient.network.admin.dimension.DimensionRegenerateConfirmC2S;
import com.skyframework.islandcoreclient.state.ClientDimensionStyle;
import com.skyframework.islandcoreclient.state.ClientDimensionView;
import com.skyframework.islandcoreclient.state.ClientIslandCache;

import net.neoforged.neoforge.network.PacketDistributor;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

/**
 * List / detail / create-form all live in this one screen, switched by {@link #mode}. The initial
 * list is requested once from the constructor; {@link #refreshFromNetwork()} only rebuilds from
 * {@link ClientIslandCache} — resending from initContent() too would loop forever while the
 * screen stays open (every reply would trigger another request).
 */
public class DimensionManagerScreen extends BaseMenuScreen {
	private enum Mode {
		LIST,
		DETAIL,
		CREATE
	}

	private enum PendingAction {
		DELETE,
		REGENERATE
	}

	private static final long PENDING_ACTION_WINDOW_SECONDS = 30L;
	private static final int CONTENT_X = 16;
	private static final int LINE_HEIGHT = 11;
	private static final int ROW_HEIGHT = 20;
	private static final int ROW_GAP = 4;
	private static final int LIST_START_Y = TOP_BAR_HEIGHT + 8;
	private static final int SUB_BACK_Y = TOP_BAR_HEIGHT + 6;
	private static final int TOP_BAR_ACTION_WIDTH = 90;
	private static final int TOP_BAR_ACTION_HEIGHT = 20;

	private Mode mode = Mode.LIST;
	@Nullable
	private String selectedDimensionId;

	@Nullable
	private PendingAction pendingAction;
	private long pendingActionExpiresAtMillis = 0L;

	private EditBox idField;
	private EditBox nameField;
	private EditBox seedField;
	private ClientDimensionStyle selectedStyle = ClientDimensionStyle.OVERWORLD_LIKE;
	private boolean randomSeed = true;
	@Nullable
	private Component createError;

	public DimensionManagerScreen(Screen parent) {
		super(Component.translatable("islandcoreclient.admin.dimension_manager.title"), parent);
		PacketDistributor.sendToServer(new DimensionListRequestC2S());
	}

	// Called by ClientPacketHandlers when a fresh DimensionListS2C/DimensionDetailS2C lands while
	// this screen is open — same pattern as TeleportsScreen/BiomeScreen.
	public void refreshFromNetwork() {
		this.rebuildWidgets();
	}

	@Override
	protected void initContent() {
		switch (this.mode) {
			case LIST -> initListContent();
			case DETAIL -> initDetailContent();
			case CREATE -> initCreateContent();
		}
	}

	private void initListContent() {
		int y = LIST_START_Y;
		for (ClientDimensionView dimension : ClientIslandCache.getDimensions()) {
			this.addRenderableWidget(Button.builder(
							Component.literal(dimension.displayName() + " (" + dimension.id() + ")"),
							button -> {
								this.selectedDimensionId = dimension.id();
								this.mode = Mode.DETAIL;
								PacketDistributor.sendToServer(new DimensionDetailRequestC2S(dimension.path()));
								this.rebuildWidgets();
							})
					.bounds(CONTENT_X, y, 360, ROW_HEIGHT)
					.build());
			y += ROW_HEIGHT + ROW_GAP;
		}

		this.addRenderableWidget(Button.builder(
						Component.translatable("islandcoreclient.admin.dimension_manager.create_button"),
						button -> {
							this.createError = null;
							this.mode = Mode.CREATE;
							this.rebuildWidgets();
						})
				.bounds(CONTENT_X, y + 8, 220, ROW_HEIGHT)
				.build());
	}

	private void addBackToListButton() {
		this.addRenderableWidget(Button.builder(
						Component.translatable("islandcoreclient.admin.dimension_manager.back_to_list"),
						button -> {
							this.mode = Mode.LIST;
							PacketDistributor.sendToServer(new DimensionListRequestC2S());
							this.rebuildWidgets();
						})
				.bounds(CONTENT_X, SUB_BACK_Y, 120, 16)
				.build());
	}

	private void initDetailContent() {
		addBackToListButton();

		ClientDimensionView dimension = getSelectedDimension();
		if (dimension == null) {
			return;
		}

		boolean pending = this.pendingAction != null;
		// +LINE_HEIGHT * 2 beyond the base 4-line block: when createdAt/updatedAt are populated (the
		// common case — see the null-check below), renderDetailContent draws 2 more detail lines
		// before this Y, which the previous flat offset didn't account for, letting these buttons'
		// top edge overlap that text.
		int buttonY = SUB_BACK_Y + 20 + LINE_HEIGHT * 4 + 16 + LINE_HEIGHT * 2;

		Button regenerateButton = this.addRenderableWidget(Button.builder(
						Component.translatable("islandcoreclient.admin.dimension_manager.regenerate_button"),
						button -> onRegenerateClicked(dimension))
				.bounds(CONTENT_X, buttonY, 150, ROW_HEIGHT)
				.build());
		Button deleteButton = this.addRenderableWidget(Button.builder(
						Component.translatable("islandcoreclient.admin.dimension_manager.delete_button").withStyle(ChatFormatting.RED),
						button -> onDeleteClicked(dimension))
				.bounds(CONTENT_X + 154, buttonY, 150, ROW_HEIGHT)
				.build());
		regenerateButton.visible = !pending;
		regenerateButton.active = !pending;
		deleteButton.visible = !pending;
		deleteButton.active = !pending;

		Button confirmButton = this.addRenderableWidget(Button.builder(
						Component.translatable("islandcoreclient.admin.dimension_manager.confirm_action_button"),
						button -> onConfirmActionClicked())
				.bounds(CONTENT_X, buttonY, 200, ROW_HEIGHT)
				.build());
		confirmButton.visible = pending;
		confirmButton.active = pending;
	}

	private void initCreateContent() {
		addBackToListButton();

		// Lives in the top bar's free right-hand space (same slot/height DashboardScreen uses for
		// its admin toggle) instead of at the bottom of the form: the form's fields alone already
		// fill most of a normal-sized window, and a bottom button would be unreachable without
		// scrolling, which this screen doesn't support.
		this.addRenderableWidget(Button.builder(
						Component.translatable("islandcoreclient.admin.dimension_manager.confirm_create_button"),
						button -> onCreateClicked())
				.bounds(this.width - 8 - TOP_BAR_ACTION_WIDTH, (TOP_BAR_HEIGHT - TOP_BAR_ACTION_HEIGHT) / 2,
						TOP_BAR_ACTION_WIDTH, TOP_BAR_ACTION_HEIGHT)
				.build());

		int y = SUB_BACK_Y + 24;
		this.idField = new EditBox(this.font, CONTENT_X, y, 200, 20,
				Component.translatable("islandcoreclient.admin.dimension_manager.id_field"));
		this.idField.setHint(Component.translatable("islandcoreclient.admin.dimension_manager.id_field"));
		this.idField.setMaxLength(48);
		this.addRenderableWidget(this.idField);

		y += 28;
		this.nameField = new EditBox(this.font, CONTENT_X, y, 200, 20,
				Component.translatable("islandcoreclient.admin.dimension_manager.name_field"));
		this.nameField.setHint(Component.translatable("islandcoreclient.admin.dimension_manager.name_field"));
		this.nameField.setMaxLength(48);
		this.addRenderableWidget(this.nameField);

		y += 32;
		for (ClientDimensionStyle style : ClientDimensionStyle.values()) {
			Component label = style == this.selectedStyle ? Component.literal("✓ ").append(style.label()) : style.label();
			this.addRenderableWidget(Button.builder(label, button -> {
						this.selectedStyle = style;
						this.rebuildWidgets();
					})
					.bounds(CONTENT_X, y, 150, 20)
					.build());
			y += 24;
		}

		y += 4;
		this.addRenderableWidget(new ToggleRow(CONTENT_X, y, 150, 20,
				Component.translatable("islandcoreclient.admin.dimension_manager.random_seed"), this.randomSeed, true,
				newValue -> {
					this.randomSeed = newValue;
					this.seedField.setEditable(!newValue);
					this.seedField.active = !newValue;
				}));

		this.seedField = new EditBox(this.font, CONTENT_X + 154, y, 150, 20,
				Component.translatable("islandcoreclient.admin.dimension_manager.seed_field"));
		this.seedField.setFilter(text -> text.isEmpty() || text.matches("-?[0-9]{1,19}"));
		this.seedField.setEditable(!this.randomSeed);
		this.seedField.active = !this.randomSeed;
		this.addRenderableWidget(this.seedField);
	}

	@Override
	protected void renderContent(GuiGraphics context, int mouseX, int mouseY, float delta) {
		switch (this.mode) {
			case LIST -> renderListContent(context);
			case DETAIL -> renderDetailContent(context);
			case CREATE -> renderCreateContent(context);
		}
	}

	private void renderListContent(GuiGraphics context) {
		if (ClientIslandCache.getDimensions().isEmpty()) {
			context.drawString(this.font,
					Component.translatable("islandcoreclient.admin.dimension_manager.empty"), CONTENT_X, LIST_START_Y, 0xAAAAAA);
		}
	}

	private void renderDetailContent(GuiGraphics context) {
		if (this.pendingActionExpiresAtMillis > 0 && System.currentTimeMillis() >= this.pendingActionExpiresAtMillis) {
			this.pendingAction = null;
			this.pendingActionExpiresAtMillis = 0L;
			this.rebuildWidgets();
			return;
		}

		ClientDimensionView dimension = getSelectedDimension();
		if (dimension == null) {
			context.drawString(this.font,
					Component.translatable("islandcoreclient.admin.dimension_manager.not_found"), CONTENT_X, SUB_BACK_Y + 24, 0xAAAAAA);
			return;
		}

		int x = CONTENT_X;
		int y = SUB_BACK_Y + 24;
		context.drawString(this.font,
				Component.translatable("islandcoreclient.admin.dimension_manager.detail_id", dimension.id()), x, y, 0xFFFFFF);
		y += LINE_HEIGHT;
		context.drawString(this.font,
				Component.translatable("islandcoreclient.admin.dimension_manager.detail_name", dimension.displayName()), x, y, 0xFFFFFF);
		y += LINE_HEIGHT;
		context.drawString(this.font,
				Component.translatable("islandcoreclient.admin.dimension_manager.detail_style", dimension.style().label()), x, y, 0xFFFFFF);
		y += LINE_HEIGHT;
		context.drawString(this.font,
				Component.translatable("islandcoreclient.admin.dimension_manager.detail_seed", dimension.seed()), x, y, 0xFFFFFF);
		y += LINE_HEIGHT;

		// Only DimensionDetailS2C carries these (the list doesn't) — null until this dimension's
		// detail has actually been fetched at least once, which initListContent's row click always
		// triggers before entering DETAIL mode, so this is normally already populated by the time
		// this renders.
		if (dimension.createdAt() != null && dimension.updatedAt() != null) {
			context.drawString(this.font,
					Component.translatable("islandcoreclient.admin.dimension_manager.detail_created_at", dimension.createdAt()), x, y, 0xAAAAAA);
			y += LINE_HEIGHT;
			context.drawString(this.font,
					Component.translatable("islandcoreclient.admin.dimension_manager.detail_updated_at", dimension.updatedAt()), x, y, 0xAAAAAA);
		}
		y += 16;

		if (this.pendingAction != null) {
			long remaining = Math.max(0L, (this.pendingActionExpiresAtMillis - System.currentTimeMillis()) / 1000L);
			String actionKey = this.pendingAction == PendingAction.DELETE
					? "islandcoreclient.admin.dimension_manager.pending_delete"
					: "islandcoreclient.admin.dimension_manager.pending_regenerate";
			context.drawString(this.font, Component.translatable(actionKey, remaining), x, y, 0xFFCC55);
		}
	}

	private void renderCreateContent(GuiGraphics context) {
		if (this.createError != null) {
			context.drawString(this.font, this.createError.copy().withStyle(ChatFormatting.RED),
					CONTENT_X, this.height - 28, 0xFFFFFF);
		}
	}

	@Nullable
	private ClientDimensionView getSelectedDimension() {
		for (ClientDimensionView dimension : ClientIslandCache.getDimensions()) {
			if (dimension.id().equals(this.selectedDimensionId)) {
				return dimension;
			}
		}
		return null;
	}

	private void onRegenerateClicked(ClientDimensionView dimension) {
		this.minecraft.setScreen(new ConfirmScreen(
				confirmed -> {
					if (confirmed) {
						requestAction(PendingAction.REGENERATE, dimension);
					}
					this.minecraft.setScreen(this);
				},
				Component.translatable("islandcoreclient.admin.dimension_manager.confirm_title"),
				Component.translatable("islandcoreclient.admin.dimension_manager.confirm_regenerate_message",
						Component.literal(dimension.displayName()).withStyle(ChatFormatting.BOLD))));
	}

	private void onDeleteClicked(ClientDimensionView dimension) {
		this.minecraft.setScreen(new ConfirmScreen(
				confirmed -> {
					if (confirmed) {
						requestAction(PendingAction.DELETE, dimension);
					}
					this.minecraft.setScreen(this);
				},
				Component.translatable("islandcoreclient.admin.dimension_manager.confirm_title"),
				Component.translatable("islandcoreclient.admin.dimension_manager.confirm_delete_message",
						Component.literal(dimension.displayName()).withStyle(ChatFormatting.BOLD))));
	}

	// Request (non-confirm) phase for both delete and regenerate: opens the server's own 30s
	// confirmation window. This screen tracks that window client-locally (pendingActionExpiresAtMillis),
	// same pattern as DeleteIslandScreen/AdminIslandDetailScreen/VanillaResetScreen.
	private void requestAction(PendingAction action, ClientDimensionView dimension) {
		if (action == PendingAction.DELETE) {
			PacketDistributor.sendToServer(new DimensionDeleteC2S(dimension.path()));
		} else {
			PacketDistributor.sendToServer(new DimensionRegenerateC2S(dimension.path(), Optional.empty()));
		}
		PendingActionTracker.await((success, reasonKey) -> {
			if (success) {
				this.pendingAction = action;
				this.pendingActionExpiresAtMillis = System.currentTimeMillis() + PENDING_ACTION_WINDOW_SECONDS * 1000L;
			} else {
				ClientErrorToasts.showReason(reasonKey);
			}
			this.rebuildWidgets();
		});
	}

	private void onConfirmActionClicked() {
		ClientDimensionView dimension = getSelectedDimension();
		if (dimension == null || this.pendingAction == null) {
			this.pendingAction = null;
			this.pendingActionExpiresAtMillis = 0L;
			this.rebuildWidgets();
			return;
		}

		if (this.pendingAction == PendingAction.DELETE) {
			PacketDistributor.sendToServer(new DimensionDeleteConfirmC2S(dimension.path()));
			PendingActionTracker.await((success, reasonKey) -> {
				this.pendingAction = null;
				this.pendingActionExpiresAtMillis = 0L;
				if (success) {
					this.mode = Mode.LIST;
					PacketDistributor.sendToServer(new DimensionListRequestC2S());
				} else {
					ClientErrorToasts.showReason(reasonKey);
				}
				this.rebuildWidgets();
			});
		} else {
			PacketDistributor.sendToServer(new DimensionRegenerateConfirmC2S(dimension.path()));
			PendingActionTracker.await((success, reasonKey) -> {
				this.pendingAction = null;
				this.pendingActionExpiresAtMillis = 0L;
				if (success) {
					// Stays in DETAIL mode: regenerating keeps the same dimension id, just changes
					// its seed — re-fetch this one dimension's detail to reflect the new seed.
					PacketDistributor.sendToServer(new DimensionDetailRequestC2S(dimension.path()));
				} else {
					ClientErrorToasts.showReason(reasonKey);
				}
				this.rebuildWidgets();
			});
		}
	}

	private void onCreateClicked() {
		String id = this.idField.getValue().trim();
		String name = this.nameField.getValue().trim();

		if (id.isEmpty() || !id.matches("[a-z0-9_]+")) {
			this.createError = Component.translatable("islandcoreclient.admin.dimension_manager.error_invalid_id");
			return;
		}
		if (name.isEmpty()) {
			this.createError = Component.translatable("islandcoreclient.admin.dimension_manager.error_invalid_name");
			return;
		}

		Optional<Long> seed = this.randomSeed ? Optional.empty() : Optional.ofNullable(parseSeed(this.seedField.getValue()));
		PacketDistributor.sendToServer(new DimensionCreateC2S(id, name, this.selectedStyle.name(), seed));
		PendingActionTracker.await((success, reasonKey) -> {
			if (success) {
				this.mode = Mode.LIST;
				PacketDistributor.sendToServer(new DimensionListRequestC2S());
				this.rebuildWidgets();
			} else {
				ClientErrorToasts.showReason(reasonKey);
			}
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
