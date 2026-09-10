package com.skyframework.islandcoreclient.gui.island;

import com.skyframework.islandcoreclient.gui.common.BaseMenuScreen;
import com.skyframework.islandcoreclient.gui.common.FlagPresetRow;
import com.skyframework.islandcoreclient.gui.common.PagedFlagGrid;
import com.skyframework.islandcoreclient.gui.common.TriStateRow;
import com.skyframework.islandcoreclient.network.ClientErrorToasts;
import com.skyframework.islandcoreclient.network.PendingActionTracker;
import com.skyframework.islandcoreclient.network.flag.ExceptionGroupSetPresetC2S;
import com.skyframework.islandcoreclient.network.flag.ExceptionGroupsStatusRequestC2S;
import com.skyframework.islandcoreclient.network.flag.FlagSetC2S;
import com.skyframework.islandcoreclient.network.flag.FlagSetPresetC2S;
import com.skyframework.islandcoreclient.network.flag.FlagsStatusRequestC2S;
import com.skyframework.islandcoreclient.state.ClientExceptionGroupView;
import com.skyframework.islandcoreclient.state.ClientFlagView;
import com.skyframework.islandcoreclient.state.ClientIslandCache;
import com.skyframework.islandcoreclient.state.ClientTriState;

import net.neoforged.neoforge.network.PacketDistributor;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Replaces the old 3-toggle legacy IslandSetting view (firespread/pvp/mobdamage via
 * IslandSettingsUpdateC2S) with the real flag engine: the 9 flags (6 ROLE_BASED, 3 ISLAND_GLOBAL)
 * and the server's exception groups — see FlagsStatusRequestC2S/ExceptionGroupsStatusRequestC2S.
 * The server already treats firespread/pvp/mobdamage as aliases of 3 of these 9 flags, so nothing
 * from the old view is lost.
 *
 * <p>Two tabs: "Permisos" — the 6 ROLE_BASED flags and every exception group (now that the server
 * resolves both per role via the same 4-level preset, see FlagSetPresetC2S/
 * ExceptionGroupSetPresetC2S) in one flat 2-column, paginated grid ({@link PagedFlagGrid}), all
 * shown with {@link FlagPresetRow} — and "General" — the 3 ISLAND_GLOBAL flags, unpaginated, with
 * {@link TriStateRow}, since a preset is meaningless for a flag with no per-role distinction and
 * 3 items never need pagination.
 */
public class SettingsScreen extends BaseMenuScreen {
	private enum Tab {
		PERMISSIONS, GENERAL
	}

	private static final int ROW_WIDTH = 220;
	private static final int ROW_HEIGHT = 20;
	private static final int ROW_SPACING = 4;
	private static final int TAB_BUTTON_WIDTH = 110;
	private static final int TAB_BUTTON_HEIGHT = 20;
	private static final int TAB_BUTTON_GAP = 4;
	private static final int TAB_BAR_Y = TOP_BAR_HEIGHT + 6;
	private static final int CONTENT_TOP = TAB_BAR_Y + TAB_BUTTON_HEIGHT + 10;
	private static final int CONTENT_BOTTOM_MARGIN = 12;

	private static final int GRID_MAX_WIDTH = 480;
	private static final int GRID_COLUMN_GAP = 24;
	private static final int PAGINATION_ROW_HEIGHT = 20;
	private static final int PAGINATION_GAP = 8;
	private static final int PAGINATION_BUTTON_WIDTH = 90;

	private Tab activeTab = Tab.PERMISSIONS;
	private final PagedFlagGrid grid = new PagedFlagGrid(0, CONTENT_TOP, 1, 1, ROW_HEIGHT, ROW_SPACING, GRID_COLUMN_GAP);

	public SettingsScreen(Screen parent) {
		super(Component.translatable("islandcoreclient.settings.title"), parent);
		PacketDistributor.sendToServer(new FlagsStatusRequestC2S());
		PacketDistributor.sendToServer(new ExceptionGroupsStatusRequestC2S());
	}

	// Called by ClientPacketHandlers when a fresh FlagsStatusS2C/ExceptionGroupsStatusS2C lands
	// while this screen is open — same pattern as every other status-driven screen.
	public void refreshFromNetwork() {
		this.rebuildWidgets();
	}

	@Override
	protected void initContent() {
		int tabsWidth = 2 * TAB_BUTTON_WIDTH + TAB_BUTTON_GAP;
		int tabsX = this.width / 2 - tabsWidth / 2;
		addTabButton(tabsX, Tab.PERMISSIONS, Component.translatable("islandcoreclient.settings.tab_permissions"));
		addTabButton(tabsX + (TAB_BUTTON_WIDTH + TAB_BUTTON_GAP), Tab.GENERAL, Component.translatable("islandcoreclient.settings.tab_general"));

		switch (activeTab) {
			case PERMISSIONS -> initPermissionsTab();
			case GENERAL -> initGeneralTab();
		}
	}

	private void addTabButton(int x, Tab tab, Component label) {
		boolean isActiveTab = tab == activeTab;
		Component message = isActiveTab ? label.copy().withStyle(ChatFormatting.UNDERLINE, ChatFormatting.BOLD) : label;
		Button button = this.addRenderableWidget(Button.builder(message, b -> onTabClicked(tab))
				.bounds(x, TAB_BAR_Y, TAB_BUTTON_WIDTH, TAB_BUTTON_HEIGHT)
				.build());
		// The active tab's own button is inert (you're already on it) — only the other one navigates.
		button.active = !isActiveTab;
	}

	private void onTabClicked(Tab tab) {
		this.activeTab = tab;
		this.rebuildWidgets();
	}

	private void initPermissionsTab() {
		int gridWidth = Math.min(GRID_MAX_WIDTH, this.width - 32);
		int gridX = this.width / 2 - gridWidth / 2;
		int gridViewportHeight = Math.max(ROW_HEIGHT,
				this.height - CONTENT_BOTTOM_MARGIN - PAGINATION_ROW_HEIGHT - PAGINATION_GAP - CONTENT_TOP);
		grid.setViewport(gridX, CONTENT_TOP, gridWidth, gridViewportHeight);

		boolean owner = ClientIslandCache.isOwner();
		List<ClientFlagView> roleFlags = roleBasedFlags();
		List<ClientExceptionGroupView> groups = ClientIslandCache.getExceptionGroups();
		grid.setItemCount(roleFlags.size() + groups.size());

		for (int i = 0; i < roleFlags.size(); i++) {
			if (!grid.isItemOnCurrentPage(i)) {
				continue;
			}
			ClientFlagView flag = roleFlags.get(i);
			FlagPresetRow row = new FlagPresetRow(grid.getItemX(i), grid.getItemY(i), grid.getItemWidth(), ROW_HEIGHT,
					flag.label(), flag.currentPreset(), owner, preset -> onFlagPresetChanged(flag.flagId(), preset));
			row.setTooltip(Tooltip.create(flag.description()));
			this.addRenderableWidget(row);
		}

		int groupsOffset = roleFlags.size();
		for (int j = 0; j < groups.size(); j++) {
			int i = groupsOffset + j;
			if (!grid.isItemOnCurrentPage(i)) {
				continue;
			}
			ClientExceptionGroupView group = groups.get(j);
			boolean rowEditable = owner && group.ownerConfigurable();
			FlagPresetRow row = new FlagPresetRow(grid.getItemX(i), grid.getItemY(i), grid.getItemWidth(), ROW_HEIGHT,
					group.label(), group.currentPreset(), rowEditable, preset -> onExceptionPresetChanged(group.groupId(), preset));
			row.setTooltip(Tooltip.create(group.description()));
			this.addRenderableWidget(row);
		}

		int paginationY = paginationRowY();
		Button prevButton = this.addRenderableWidget(Button.builder(Component.translatable("islandcoreclient.pagination.prev"),
						b -> {
							grid.prevPage();
							this.rebuildWidgets();
						})
				.bounds(gridX, paginationY, PAGINATION_BUTTON_WIDTH, PAGINATION_ROW_HEIGHT)
				.build());
		prevButton.active = grid.hasPrevPage();

		Button nextButton = this.addRenderableWidget(Button.builder(Component.translatable("islandcoreclient.pagination.next"),
						b -> {
							grid.nextPage();
							this.rebuildWidgets();
						})
				.bounds(gridX + gridWidth - PAGINATION_BUTTON_WIDTH, paginationY, PAGINATION_BUTTON_WIDTH, PAGINATION_ROW_HEIGHT)
				.build());
		nextButton.active = grid.hasNextPage();
	}

	private int paginationRowY() {
		return this.height - CONTENT_BOTTOM_MARGIN - PAGINATION_ROW_HEIGHT;
	}

	private void initGeneralTab() {
		boolean owner = ClientIslandCache.isOwner();
		int x = this.width / 2 - ROW_WIDTH / 2;
		int y = CONTENT_TOP;
		for (ClientFlagView flag : globalFlags()) {
			boolean editable = owner && !flag.missingRequiredPermission();
			TriStateRow row = new TriStateRow(x, y, ROW_WIDTH, ROW_HEIGHT, flag.label(), flag.islandOverride(), editable,
					newValue -> onFlagOverrideChanged(flag.flagId(), newValue));
			// Still shows the current value even when disabled — only whether the player can change
			// it is affected, same as any other owner-gated row.
			Component tooltip = flag.missingRequiredPermission()
					? flag.description().copy().append("\n").append(Component.translatable("islandcoreclient.settings.flag_permission_required"))
					: flag.description();
			row.setTooltip(Tooltip.create(tooltip));
			this.addRenderableWidget(row);
			y += ROW_HEIGHT + ROW_SPACING;
		}
	}

	@Override
	protected void renderContent(GuiGraphics context, int mouseX, int mouseY, float delta) {
		if (activeTab != Tab.PERMISSIONS) {
			return;
		}
		Component indicator = Component.translatable("islandcoreclient.pagination.page_indicator", grid.getCurrentPage() + 1, grid.totalPages());
		int textWidth = this.font.width(indicator);
		context.drawString(this.font, indicator, this.width / 2 - textWidth / 2,
				paginationRowY() + (PAGINATION_ROW_HEIGHT - this.font.lineHeight) / 2, 0xAAAAAA);
	}

	private static List<ClientFlagView> roleBasedFlags() {
		return ClientIslandCache.getFlags().stream().filter(flag -> flag.category() == ClientFlagView.Category.ROLE_BASED).toList();
	}

	private static List<ClientFlagView> globalFlags() {
		return ClientIslandCache.getFlags().stream().filter(flag -> flag.category() == ClientFlagView.Category.ISLAND_GLOBAL).toList();
	}

	// FlagPresetRow already switched itself optimistically before this runs. On success, the exact
	// resolved values (and every other flag view derived from them) depend on server/code defaults
	// this client doesn't replicate, so refetch instead of guessing — the FlagsStatusS2C reply
	// rebuilds this screen automatically (see ClientPacketHandlers). On failure, revert the cached
	// preset immediately instead of waiting on a refetch that isn't coming.
	private void onFlagPresetChanged(String flagId, String preset) {
		String previous = findFlag(flagId).map(ClientFlagView::currentPreset).orElse("custom");
		ClientIslandCache.updateFlagPreset(flagId, preset);
		PacketDistributor.sendToServer(new FlagSetPresetC2S(flagId, preset));
		PendingActionTracker.await((success, reasonKey) -> {
			if (success) {
				PacketDistributor.sendToServer(new FlagsStatusRequestC2S());
			} else {
				ClientIslandCache.updateFlagPreset(flagId, previous);
				ClientErrorToasts.showReason(reasonKey);
				this.rebuildWidgets();
			}
		});
	}

	// Exact mirror of onFlagPresetChanged above, for exception groups (ExceptionGroupSetPresetC2S).
	private void onExceptionPresetChanged(String groupId, String preset) {
		String previous = findExceptionGroup(groupId).map(ClientExceptionGroupView::currentPreset).orElse("custom");
		ClientIslandCache.updateExceptionGroupPreset(groupId, preset);
		PacketDistributor.sendToServer(new ExceptionGroupSetPresetC2S(groupId, preset));
		PendingActionTracker.await((success, reasonKey) -> {
			if (success) {
				PacketDistributor.sendToServer(new ExceptionGroupsStatusRequestC2S());
			} else {
				ClientIslandCache.updateExceptionGroupPreset(groupId, previous);
				ClientErrorToasts.showReason(reasonKey);
				this.rebuildWidgets();
			}
		});
	}

	// TriStateRow already cycled itself optimistically before this runs. On failure, revert the
	// cached override and rebuild so the row reflects the real (unchanged) state. On success, left
	// as-is (no refetch) — same optimistic pattern ToggleRow-based rows already use elsewhere.
	private void onFlagOverrideChanged(String flagId, ClientTriState newValue) {
		ClientTriState previous = findFlag(flagId).map(ClientFlagView::islandOverride).orElse(ClientTriState.DEFAULT);
		ClientIslandCache.updateFlagOverride(flagId, newValue);
		PacketDistributor.sendToServer(new FlagSetC2S(flagId, newValue.name().toLowerCase(Locale.ROOT)));
		PendingActionTracker.await((success, reasonKey) -> {
			if (!success) {
				ClientIslandCache.updateFlagOverride(flagId, previous);
				ClientErrorToasts.showReason(reasonKey);
				this.rebuildWidgets();
			}
		});
	}

	private static Optional<ClientFlagView> findFlag(String flagId) {
		return ClientIslandCache.getFlags().stream().filter(flag -> flag.flagId().equals(flagId)).findFirst();
	}

	private static Optional<ClientExceptionGroupView> findExceptionGroup(String groupId) {
		return ClientIslandCache.getExceptionGroups().stream().filter(group -> group.groupId().equals(groupId)).findFirst();
	}
}
