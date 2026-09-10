package com.skyframework.islandcoreclient.gui.admin;

import com.skyframework.islandcoreclient.gui.common.BaseMenuScreen;
import com.skyframework.islandcoreclient.gui.common.ToggleRow;
import com.skyframework.islandcoreclient.network.ClientErrorToasts;
import com.skyframework.islandcoreclient.network.PendingActionTracker;
import com.skyframework.islandcoreclient.network.admin.spawn.SpawnAuthorizedPlayerAddC2S;
import com.skyframework.islandcoreclient.network.admin.spawn.SpawnBuildProtectionSetC2S;
import com.skyframework.islandcoreclient.network.admin.spawn.SpawnBuildProtectionStatusRequestC2S;
import com.skyframework.islandcoreclient.network.admin.spawn.SpawnIslandCreateC2S;
import com.skyframework.islandcoreclient.network.admin.spawn.SpawnIslandResizeC2S;
import com.skyframework.islandcoreclient.network.admin.spawn.SpawnIslandSetHomeC2S;
import com.skyframework.islandcoreclient.network.admin.spawn.SpawnStatusRequestC2S;
import com.skyframework.islandcoreclient.state.ClientIslandCache;

import net.neoforged.neoforge.network.PacketDistributor;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;

/**
 * Requests the real status once from the constructor; {@link #refreshFromNetwork()} only rebuilds
 * from {@link ClientIslandCache}. {@link SpawnIslandSetHomeC2S} carries NO coordinates — the
 * server reads the sender's actual position and validates it itself (bounds + dimension, same as
 * "/island admin spawn sethome"); this screen must never read/send a local BlockPos for it.
 *
 * <p>The build-protection section only appears once the Spawn island exists — same condition
 * {@link #initResizeForm()} already uses instead of {@link #initCreateForm()} — since there is
 * nothing to protect/authorize before that. The authorized-players LIST itself lives on its own
 * dedicated page ({@link SpawnAuthorizedPlayersScreen}, same fix already applied to
 * {@link AdminIslandDetailScreen}'s member list), reached via a "Ver jugadores autorizados (N)"
 * button that lives in the TOP BAR's free right-hand slot — same slot/height DashboardScreen's
 * admin toggle and DimensionManagerScreen's create button already use — instead of anywhere in the
 * scrollable content area, so it never competes with size/sethome/toggle/add-field for room. Only
 * the toggle and the "add authorized player" field/button — a quick action that doesn't warrant a
 * screen switch — stay in the content area, in the left column with this screen's other controls.
 */
public class SpawnManagerScreen extends BaseMenuScreen {
	private static final int CONTENT_X = 16;
	private static final int LINE_HEIGHT = 11;
	private static final int FIELD_WIDTH = 80;
	private static final int FIELD_HEIGHT = 20;
	private static final int BUTTON_WIDTH = 170;

	private static final int FORM_Y = TOP_BAR_HEIGHT + 8 + LINE_HEIGHT + 8;
	private static final int HOME_WARNING_Y = FORM_Y + FIELD_HEIGHT + 16;
	private static final int HOME_BUTTON_Y = HOME_WARNING_Y + LINE_HEIGHT * 2 + 8;

	private static final int BUILD_PROTECTION_TOGGLE_WIDTH = 220;
	private static final int BUILD_PROTECTION_TOGGLE_Y = HOME_BUTTON_Y + FIELD_HEIGHT + 16;

	// Top bar's free right-hand slot — same slot/height DashboardScreen's admin toggle and
	// DimensionManagerScreen's create button already use.
	private static final int TOP_BAR_ACTION_WIDTH = 190;
	private static final int TOP_BAR_ACTION_HEIGHT = 20;

	private static final int ADD_ROW_HEIGHT = 20;

	private EditBox sizeField;
	private Button resizeButton;
	private EditBox authorizedNameField;

	public SpawnManagerScreen(Screen parent) {
		super(Component.translatable("islandcoreclient.admin.spawn.title"), parent);
		PacketDistributor.sendToServer(new SpawnStatusRequestC2S());
		PacketDistributor.sendToServer(new SpawnBuildProtectionStatusRequestC2S());
	}

	// Called by ClientPacketHandlers when a fresh SpawnStatusS2C lands while this screen is open —
	// same pattern as TeleportsScreen/BiomeScreen.
	public void refreshFromNetwork() {
		this.rebuildWidgets();
	}

	@Override
	protected void initContent() {
		if (!ClientIslandCache.spawnExists()) {
			initCreateForm();
			return;
		}

		initResizeForm();
		initBuildProtectionSection();

		// Top bar, not the content area: frees the content area from having to make room for it
		// alongside size/sethome/toggle/add-field.
		int authorizedCount = ClientIslandCache.getSpawnAuthorizedPlayers().size();
		this.addRenderableWidget(Button.builder(
						Component.translatable("islandcoreclient.admin.spawn.view_authorized_button", authorizedCount),
						button -> onViewAuthorizedClicked())
				.bounds(this.width - 8 - TOP_BAR_ACTION_WIDTH, (TOP_BAR_HEIGHT - TOP_BAR_ACTION_HEIGHT) / 2,
						TOP_BAR_ACTION_WIDTH, TOP_BAR_ACTION_HEIGHT)
				.build());
	}

	private void initCreateForm() {
		this.sizeField = new EditBox(this.font, CONTENT_X, FORM_Y, FIELD_WIDTH, FIELD_HEIGHT,
				Component.translatable("islandcoreclient.admin.spawn.size_field"));
		this.sizeField.setValue("25");
		this.sizeField.setFilter(text -> text.isEmpty() || text.matches("[0-9]{1,4}"));
		this.addRenderableWidget(this.sizeField);

		this.addRenderableWidget(Button.builder(
						Component.translatable("islandcoreclient.admin.spawn.create_button"),
						button -> onCreateClicked())
				.bounds(CONTENT_X + FIELD_WIDTH + 8, FORM_Y, BUTTON_WIDTH, FIELD_HEIGHT)
				.build());
	}

	private void initResizeForm() {
		this.sizeField = new EditBox(this.font, CONTENT_X, FORM_Y, FIELD_WIDTH, FIELD_HEIGHT,
				Component.translatable("islandcoreclient.admin.spawn.size_field"));
		this.sizeField.setFilter(text -> text.isEmpty() || text.matches("[0-9]{1,4}"));
		this.sizeField.setResponder(this::onResizeFieldChanged);
		this.addRenderableWidget(this.sizeField);

		this.resizeButton = this.addRenderableWidget(Button.builder(
						Component.translatable("islandcoreclient.admin.spawn.resize_button"),
						button -> onResizeClicked())
				.bounds(CONTENT_X + FIELD_WIDTH + 8, FORM_Y, BUTTON_WIDTH, FIELD_HEIGHT)
				.build());
		this.resizeButton.active = false;

		this.addRenderableWidget(Button.builder(
						Component.translatable("islandcoreclient.admin.spawn.set_home_button"),
						button -> onSetHomeClicked())
				.bounds(CONTENT_X, HOME_BUTTON_Y, 200, FIELD_HEIGHT)
				.build());
	}

	private void initBuildProtectionSection() {
		this.addRenderableWidget(new ToggleRow(
				CONTENT_X, BUILD_PROTECTION_TOGGLE_Y, BUILD_PROTECTION_TOGGLE_WIDTH, FIELD_HEIGHT,
				Component.translatable("islandcoreclient.admin.spawn.build_protection_toggle"),
				ClientIslandCache.getSpawnBuildProtectionEnabled(), true,
				this::onBuildProtectionToggled));

		// Left column, bottom-pinned: a quick action, so it stays here instead of moving into
		// SpawnAuthorizedPlayersScreen along with the read-only list.
		int fieldWidth = 160;
		int buttonWidth = 70;
		int addFieldY = this.height - 16 - ADD_ROW_HEIGHT;

		this.authorizedNameField = new EditBox(this.font, CONTENT_X, addFieldY, fieldWidth, ADD_ROW_HEIGHT,
				Component.translatable("islandcoreclient.admin.spawn.authorized_add_placeholder"));
		this.authorizedNameField.setHint(Component.translatable("islandcoreclient.admin.spawn.authorized_add_placeholder"));
		this.authorizedNameField.setMaxLength(32);
		this.addRenderableWidget(this.authorizedNameField);

		this.addRenderableWidget(Button.builder(
						Component.translatable("islandcoreclient.admin.spawn.authorized_add_button"),
						button -> onAuthorizedAddClicked())
				.bounds(CONTENT_X + fieldWidth + 4, addFieldY, buttonWidth, ADD_ROW_HEIGHT)
				.build());
	}

	@Override
	protected void renderContent(GuiGraphics context, int mouseX, int mouseY, float delta) {
		if (!ClientIslandCache.spawnExists()) {
			context.drawString(this.font,
					Component.translatable("islandcoreclient.admin.spawn.not_exists"), CONTENT_X, TOP_BAR_HEIGHT + 8, 0xAAAAAA);
			return;
		}

		context.drawString(this.font,
				Component.translatable("islandcoreclient.admin.spawn.current_size", ClientIslandCache.getSpawnSize()),
				CONTENT_X, TOP_BAR_HEIGHT + 8, 0xFFFFFF);

		context.drawString(this.font,
				Component.translatable("islandcoreclient.admin.spawn.home_warning"), CONTENT_X, HOME_WARNING_Y, 0xFFCC55);

		BlockPos home = ClientIslandCache.getSpawnHomeLocation();
		Component homeText = home != null
				? Component.translatable("islandcoreclient.admin.spawn.home_current", home.getX(), home.getY(), home.getZ())
				: Component.translatable("islandcoreclient.admin.spawn.home_not_set");
		context.drawString(this.font, homeText, CONTENT_X, HOME_WARNING_Y + LINE_HEIGHT, 0xDDDDDD);
	}

	private void onCreateClicked() {
		int size = parseSize(this.sizeField.getValue(), 25);
		PacketDistributor.sendToServer(new SpawnIslandCreateC2S(size));
		PendingActionTracker.await((success, reasonKey) -> {
			if (success) {
				PacketDistributor.sendToServer(new SpawnStatusRequestC2S());
			} else {
				ClientErrorToasts.showReason(reasonKey);
			}
		});
	}

	private void onResizeFieldChanged(String text) {
		if (this.resizeButton == null) {
			return;
		}
		try {
			int value = Integer.parseInt(text.trim());
			this.resizeButton.active = value > ClientIslandCache.getSpawnSize();
		} catch (NumberFormatException e) {
			this.resizeButton.active = false;
		}
	}

	private void onResizeClicked() {
		int value = parseSize(this.sizeField.getValue(), -1);
		if (value <= ClientIslandCache.getSpawnSize()) {
			// Defense in depth: the button is already inactive in this case, this should be
			// unreachable, but resizeIsland must never shrink, so never send it regardless.
			return;
		}
		PacketDistributor.sendToServer(new SpawnIslandResizeC2S(value));
		PendingActionTracker.await((success, reasonKey) -> {
			if (success) {
				PacketDistributor.sendToServer(new SpawnStatusRequestC2S());
			} else {
				ClientErrorToasts.showReason(reasonKey);
			}
		});
	}

	private void onSetHomeClicked() {
		PacketDistributor.sendToServer(new SpawnIslandSetHomeC2S());
		PendingActionTracker.await((success, reasonKey) -> {
			if (success) {
				PacketDistributor.sendToServer(new SpawnStatusRequestC2S());
			} else {
				ClientErrorToasts.showReason(reasonKey);
			}
		});
	}

	// ToggleRow already flipped itself optimistically before this runs — same pattern
	// SettingsScreen#onSettingToggled uses for IslandSettingsUpdateC2S. On failure, flip the
	// cached value back and rebuild so the row reflects the real (unchanged) state.
	private void onBuildProtectionToggled(boolean newValue) {
		ClientIslandCache.setSpawnBuildProtectionEnabled(newValue);
		PacketDistributor.sendToServer(new SpawnBuildProtectionSetC2S(newValue));
		PendingActionTracker.await((success, reasonKey) -> {
			if (!success) {
				ClientIslandCache.setSpawnBuildProtectionEnabled(!newValue);
				ClientErrorToasts.showReason(reasonKey);
				this.rebuildWidgets();
			}
		});
	}

	private void onViewAuthorizedClicked() {
		this.minecraft.setScreen(new SpawnAuthorizedPlayersScreen(this));
	}

	private void onAuthorizedAddClicked() {
		String targetName = this.authorizedNameField.getValue().trim();
		if (targetName.isEmpty()) {
			return;
		}
		PacketDistributor.sendToServer(new SpawnAuthorizedPlayerAddC2S(targetName));
		PendingActionTracker.await((success, reasonKey) -> {
			if (success) {
				// The real name/role for the new entry comes back on the next status refresh;
				// refetch now instead of guessing it locally — same reasoning as MembersScreen's
				// invite flow.
				PacketDistributor.sendToServer(new SpawnBuildProtectionStatusRequestC2S());
			} else {
				ClientErrorToasts.showReason(reasonKey);
			}
			this.rebuildWidgets();
		});
	}

	private static int parseSize(String text, int fallback) {
		try {
			return Integer.parseInt(text.trim());
		} catch (NumberFormatException e) {
			return fallback;
		}
	}
}
