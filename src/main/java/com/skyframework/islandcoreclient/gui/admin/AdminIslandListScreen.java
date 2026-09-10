package com.skyframework.islandcoreclient.gui.admin;

import java.util.List;

import com.skyframework.islandcoreclient.gui.common.BaseMenuScreen;
import com.skyframework.islandcoreclient.network.admin.island.AdminIslandListRequestC2S;
import com.skyframework.islandcoreclient.state.ClientAdminIslandSummaryView;
import com.skyframework.islandcoreclient.state.ClientIslandCache;

import net.neoforged.neoforge.network.PacketDistributor;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

/**
 * Global, server-paginated island list for admins. The request is sent once from the constructor
 * (not from {@link #initContent()}): {@link #refreshFromNetwork()} only rebuilds widgets from
 * whatever {@link ClientIslandCache} now holds — resending the request from initContent() too
 * would re-trigger on every single rebuild this reply itself causes, looping forever while the
 * screen stays open.
 */
public class AdminIslandListScreen extends BaseMenuScreen {
	public static final int PAGE_SIZE = 10;
	private static final int CONTENT_X = 16;
	private static final int SEARCH_Y = TOP_BAR_HEIGHT + 8;
	private static final int ROWS_START_Y = SEARCH_Y + 24;
	private static final int ROW_WIDTH = 280;
	private static final int ROW_HEIGHT = 20;
	private static final int ROW_GAP = 4;
	private static final int SEARCH_FIELD_WIDTH = 200;
	private static final int SEARCH_BUTTON_WIDTH = 70;
	private static final int PAGE_BUTTON_WIDTH = 60;
	private static final int SPAWN_BUTTON_HEIGHT = 20;

	private String searchQuery = "";
	private EditBox searchField;

	public AdminIslandListScreen(Screen parent) {
		super(Component.translatable("islandcoreclient.admin.island_list.title"), parent);
		PacketDistributor.sendToServer(new AdminIslandListRequestC2S(0, PAGE_SIZE, ""));
	}

	// Called by ClientPacketHandlers when a fresh AdminIslandListS2C lands while this screen is
	// open — same pattern as TeleportsScreen/BiomeScreen.
	public void refreshFromNetwork() {
		this.rebuildWidgets();
	}

	@Override
	protected void initContent() {
		this.searchField = new EditBox(this.font, CONTENT_X, SEARCH_Y, SEARCH_FIELD_WIDTH, 20,
				Component.translatable("islandcoreclient.admin.island_list.search_placeholder"));
		this.searchField.setHint(Component.translatable("islandcoreclient.admin.island_list.search_placeholder"));
		this.searchField.setMaxLength(32);
		this.searchField.setValue(this.searchQuery);
		this.searchField.setResponder(text -> this.searchQuery = text);
		this.addRenderableWidget(this.searchField);

		this.addRenderableWidget(Button.builder(
						Component.translatable("islandcoreclient.admin.island_list.search_button"),
						button -> requestPage(0))
				.bounds(CONTENT_X + SEARCH_FIELD_WIDTH + 8, SEARCH_Y, SEARCH_BUTTON_WIDTH, 20)
				.build());

		List<ClientAdminIslandSummaryView> summaries = ClientIslandCache.getAdminIslands();
		int y = ROWS_START_Y;
		for (ClientAdminIslandSummaryView summary : summaries) {
			boolean deleting = "DELETING".equals(summary.state());
			// isSpawnIsland=true: ownerName would otherwise show the server's synthetic all-zero
			// owner UUID unresolved (Island.SERVER_OWNER_UUID never maps to a real player profile).
			String ownerLabel = summary.isSpawnIsland()
					? Component.translatable("islandcoreclient.admin.island_list.spawn_label").getString()
					: summary.ownerName();
			Component label = Component.literal(ownerLabel + " — " + summary.size() + "/" + summary.maxSize()
					+ " " + summary.type() + " · " + summary.state() + " · " + summary.memberCount() + " miembros");
			if (summary.isSpawnIsland()) {
				label = label.copy().withStyle(ChatFormatting.GOLD);
			} else if (deleting) {
				label = label.copy().withStyle(ChatFormatting.RED);
			}

			Button button = this.addRenderableWidget(Button.builder(label,
							b -> this.minecraft.setScreen(new AdminIslandDetailScreen(summary.ownerUuid(), this)))
					.bounds(CONTENT_X, y, ROW_WIDTH, ROW_HEIGHT)
					.build());
			button.active = !deleting;
			y += ROW_HEIGHT + ROW_GAP;
		}

		int currentPage = ClientIslandCache.getAdminIslandsCurrentPage();
		int totalPages = ClientIslandCache.getAdminIslandsTotalPages();
		int pageRowY = y + 8;

		Button prevButton = this.addRenderableWidget(Button.builder(
						Component.translatable("islandcoreclient.admin.island_list.prev_page"),
						button -> requestPage(currentPage - 1))
				.bounds(CONTENT_X, pageRowY, PAGE_BUTTON_WIDTH, ROW_HEIGHT)
				.build());
		prevButton.active = currentPage > 0;

		Button nextButton = this.addRenderableWidget(Button.builder(
						Component.translatable("islandcoreclient.admin.island_list.next_page"),
						button -> requestPage(currentPage + 1))
				.bounds(CONTENT_X + PAGE_BUTTON_WIDTH + 8, pageRowY, PAGE_BUTTON_WIDTH, ROW_HEIGHT)
				.build());
		nextButton.active = currentPage + 1 < totalPages;

		this.addRenderableWidget(Button.builder(
						Component.translatable("islandcoreclient.admin.island_list.manage_spawn_button"),
						button -> this.minecraft.setScreen(new SpawnManagerScreen(this)))
				.bounds(CONTENT_X, this.height - 16 - SPAWN_BUTTON_HEIGHT, 220, SPAWN_BUTTON_HEIGHT)
				.build());
	}

	@Override
	protected void renderContent(GuiGraphics context, int mouseX, int mouseY, float delta) {
		if (ClientIslandCache.getAdminIslands().isEmpty()) {
			context.drawString(this.font,
					Component.translatable("islandcoreclient.admin.island_list.empty"), CONTENT_X, ROWS_START_Y, 0xAAAAAA);
		}

		int currentPage = ClientIslandCache.getAdminIslandsCurrentPage();
		int totalPages = ClientIslandCache.getAdminIslandsTotalPages();
		if (totalPages > 0) {
			context.drawString(this.font,
					Component.translatable("islandcoreclient.admin.island_list.page_indicator", currentPage + 1, totalPages),
					CONTENT_X + (PAGE_BUTTON_WIDTH + 8) * 2, this.height - 16 - SPAWN_BUTTON_HEIGHT - 28, 0xAAAAAA);
		}
	}

	private void requestPage(int page) {
		PacketDistributor.sendToServer(new AdminIslandListRequestC2S(page, PAGE_SIZE, this.searchQuery));
	}
}
