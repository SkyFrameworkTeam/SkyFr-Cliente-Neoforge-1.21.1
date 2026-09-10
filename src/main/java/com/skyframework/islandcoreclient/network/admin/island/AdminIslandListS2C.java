package com.skyframework.islandcoreclient.network.admin.island;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.UUIDUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Mirrors the server's net.admin.island.AdminIslandListS2C exactly: same 3 fields (islands,
// totalPages, currentPage) and same nested IslandEntry (9 fields, past PacketCodec.tuple's
// 6-argument limit, hand-written with PacketCodec.of like IslandSnapshotS2C).
public record AdminIslandListS2C(List<IslandEntry> islands, int totalPages, int currentPage) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<AdminIslandListS2C> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "admin_island_list_s2c"));

	private static final StreamCodec<RegistryFriendlyByteBuf, List<IslandEntry>> ISLAND_LIST_CODEC =
			ByteBufCodecs.collection(ArrayList::new, IslandEntry.CODEC);

	public static final StreamCodec<RegistryFriendlyByteBuf, AdminIslandListS2C> CODEC = StreamCodec.composite(
			ISLAND_LIST_CODEC, AdminIslandListS2C::islands,
			ByteBufCodecs.VAR_INT, AdminIslandListS2C::totalPages,
			ByteBufCodecs.VAR_INT, AdminIslandListS2C::currentPage,
			AdminIslandListS2C::new
	);

	@Override
	public CustomPacketPayload.Type<AdminIslandListS2C> type() {
		return TYPE;
	}

	// Wire field order: ownerUuid, ownerName, size, maxSize, type, currentBiomeId (a LIVE lookup of
	// the biome at the island's center, not any locally-tracked value), state, memberCount,
	// isSpawnIsland (NEW — true when ownerUuid is the server's synthetic Island.SERVER_OWNER_UUID;
	// see AdminIslandListScreen for how this replaces ownerName in the row label).
	public record IslandEntry(
			UUID ownerUuid,
			String ownerName,
			int size,
			int maxSize,
			String type,
			String currentBiomeId,
			String state,
			int memberCount,
			boolean isSpawnIsland
	) {
		public static final StreamCodec<RegistryFriendlyByteBuf, IslandEntry> CODEC = StreamCodec.of(
				(buf, value) -> {
					UUIDUtil.STREAM_CODEC.encode(buf, value.ownerUuid());
					ByteBufCodecs.STRING_UTF8.encode(buf, value.ownerName());
					ByteBufCodecs.VAR_INT.encode(buf, value.size());
					ByteBufCodecs.VAR_INT.encode(buf, value.maxSize());
					ByteBufCodecs.STRING_UTF8.encode(buf, value.type());
					ByteBufCodecs.STRING_UTF8.encode(buf, value.currentBiomeId());
					ByteBufCodecs.STRING_UTF8.encode(buf, value.state());
					ByteBufCodecs.VAR_INT.encode(buf, value.memberCount());
					ByteBufCodecs.BOOL.encode(buf, value.isSpawnIsland());
				},
				buf -> new IslandEntry(
						UUIDUtil.STREAM_CODEC.decode(buf),
						ByteBufCodecs.STRING_UTF8.decode(buf),
						ByteBufCodecs.VAR_INT.decode(buf),
						ByteBufCodecs.VAR_INT.decode(buf),
						ByteBufCodecs.STRING_UTF8.decode(buf),
						ByteBufCodecs.STRING_UTF8.decode(buf),
						ByteBufCodecs.STRING_UTF8.decode(buf),
						ByteBufCodecs.VAR_INT.decode(buf),
						ByteBufCodecs.BOOL.decode(buf)
				)
		);
	}
}
