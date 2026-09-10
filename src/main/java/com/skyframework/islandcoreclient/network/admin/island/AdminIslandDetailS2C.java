package com.skyframework.islandcoreclient.network.admin.island;

import com.skyframework.islandcoreclient.network.island.IslandSnapshotS2C;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Mirrors the server's net.admin.island.AdminIslandDetailS2C exactly: same 20 fields in the same
// order (islandId, ownerUuid, ownerName, dimension, gridX, gridZ, center, boundsMin, boundsMax,
// plotBoundsMin, plotBoundsMax, islandSize, maxSize, plotSize, islandType, homeLocation, members,
// state, createdAt, updatedAt, entities), past PacketCodec.tuple's 6-argument limit so
// hand-written with PacketCodec.of like IslandSnapshotS2C. members/entities reuse
// IslandSnapshotS2C's own nested MemberEntry/EntityCounts types as-is (confirmed identical field
// order to the server's copy), exactly like the server reuses its own IslandSnapshotS2C for the
// same reason. createdAt/updatedAt arrive already formatted (dd/MM/yyyy HH:mm) — no client-side
// date formatting needed. maxSize was added after islandSize (mirroring AdminIslandListS2C
// .IslandEntry's adjacent size/maxSize pairing) so the detail screen never needs to fall back to
// the list's cached row for it.
public record AdminIslandDetailS2C(
		UUID islandId,
		UUID ownerUuid,
		String ownerName,
		String dimension,
		int gridX,
		int gridZ,
		BlockPos center,
		BlockPos boundsMin,
		BlockPos boundsMax,
		BlockPos plotBoundsMin,
		BlockPos plotBoundsMax,
		int islandSize,
		int maxSize,
		int plotSize,
		String islandType,
		BlockPos homeLocation,
		List<IslandSnapshotS2C.MemberEntry> members,
		String state,
		String createdAt,
		String updatedAt,
		IslandSnapshotS2C.EntityCounts entities
) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<AdminIslandDetailS2C> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "admin_island_detail_s2c"));

	private static final StreamCodec<RegistryFriendlyByteBuf, List<IslandSnapshotS2C.MemberEntry>> MEMBER_LIST_CODEC =
			ByteBufCodecs.collection(ArrayList::new, IslandSnapshotS2C.MemberEntry.CODEC);

	public static final StreamCodec<RegistryFriendlyByteBuf, AdminIslandDetailS2C> CODEC = StreamCodec.of(
			(buf, value) -> {
				UUIDUtil.STREAM_CODEC.encode(buf, value.islandId());
				UUIDUtil.STREAM_CODEC.encode(buf, value.ownerUuid());
				ByteBufCodecs.STRING_UTF8.encode(buf, value.ownerName());
				ByteBufCodecs.STRING_UTF8.encode(buf, value.dimension());
				ByteBufCodecs.VAR_INT.encode(buf, value.gridX());
				ByteBufCodecs.VAR_INT.encode(buf, value.gridZ());
				BlockPos.STREAM_CODEC.encode(buf, value.center());
				BlockPos.STREAM_CODEC.encode(buf, value.boundsMin());
				BlockPos.STREAM_CODEC.encode(buf, value.boundsMax());
				BlockPos.STREAM_CODEC.encode(buf, value.plotBoundsMin());
				BlockPos.STREAM_CODEC.encode(buf, value.plotBoundsMax());
				ByteBufCodecs.VAR_INT.encode(buf, value.islandSize());
				ByteBufCodecs.VAR_INT.encode(buf, value.maxSize());
				ByteBufCodecs.VAR_INT.encode(buf, value.plotSize());
				ByteBufCodecs.STRING_UTF8.encode(buf, value.islandType());
				BlockPos.STREAM_CODEC.encode(buf, value.homeLocation());
				MEMBER_LIST_CODEC.encode(buf, value.members());
				ByteBufCodecs.STRING_UTF8.encode(buf, value.state());
				ByteBufCodecs.STRING_UTF8.encode(buf, value.createdAt());
				ByteBufCodecs.STRING_UTF8.encode(buf, value.updatedAt());
				IslandSnapshotS2C.EntityCounts.CODEC.encode(buf, value.entities());
			},
			buf -> new AdminIslandDetailS2C(
					UUIDUtil.STREAM_CODEC.decode(buf),
					UUIDUtil.STREAM_CODEC.decode(buf),
					ByteBufCodecs.STRING_UTF8.decode(buf),
					ByteBufCodecs.STRING_UTF8.decode(buf),
					ByteBufCodecs.VAR_INT.decode(buf),
					ByteBufCodecs.VAR_INT.decode(buf),
					BlockPos.STREAM_CODEC.decode(buf),
					BlockPos.STREAM_CODEC.decode(buf),
					BlockPos.STREAM_CODEC.decode(buf),
					BlockPos.STREAM_CODEC.decode(buf),
					BlockPos.STREAM_CODEC.decode(buf),
					ByteBufCodecs.VAR_INT.decode(buf),
					ByteBufCodecs.VAR_INT.decode(buf),
					ByteBufCodecs.VAR_INT.decode(buf),
					ByteBufCodecs.STRING_UTF8.decode(buf),
					BlockPos.STREAM_CODEC.decode(buf),
					MEMBER_LIST_CODEC.decode(buf),
					ByteBufCodecs.STRING_UTF8.decode(buf),
					ByteBufCodecs.STRING_UTF8.decode(buf),
					ByteBufCodecs.STRING_UTF8.decode(buf),
					IslandSnapshotS2C.EntityCounts.CODEC.decode(buf)
			)
	);

	@Override
	public CustomPacketPayload.Type<AdminIslandDetailS2C> type() {
		return TYPE;
	}
}
