package com.skyframework.islandcoreclient.network.island;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Mirrors the server's net.island.IslandSnapshotS2C exactly: same 13 fields in the same order,
// same hand-written StreamCodec.of (past StreamCodec.composite's argument limit), same nested
// per-entry records/codecs. islandType is the island's IslandType id (server-side always "plains"
// for now — a distinct, mostly-unused concept from the current biome, NOT what BiomeScreen
// changes). currentBiomeId is the real current biome (e.g. "minecraft:jungle", or
// IslandData.DEFAULT_BIOME_ID = "minecraft:the_void" for an island that's never had /island biome
// used on it) — this is what BiomeScreen's "current" marker and Dashboard's summary line should
// read, not islandType.
//
// Wire format changed: biomeCooldownRemainingSeconds was inserted after currentBiomeId (grouped
// with the other biome field), and incomingInvite was inserted after pendingInvites (grouped with
// the other invite field). Both are now the real, server-computed values — see ClientIslandCache
// #applySnapshot, which no longer needs to simulate either locally.
//
// The island-type field is named islandType (not type) because CustomPacketPayload's own abstract
// method is itself called type() — Fabric's equivalent CustomPayload#getId() didn't collide with a
// same-named record component the way NeoForge/Mojmap's type() does, so this field had to be
// renamed on this port (must match the server's own IslandSnapshotS2C#islandType rename exactly,
// since both sides read/write the same wire position by field order, not by name).
public record IslandSnapshotS2C(
		boolean exists,
		int size,
		int maxSize,
		String islandType,
		String currentBiomeId,
		int biomeCooldownRemainingSeconds,
		Optional<BlockPos> home,
		String state,
		List<MemberEntry> members,
		List<PendingInviteEntry> pendingInvites,
		Optional<IncomingInviteEntry> incomingInvite,
		List<SettingEntry> settings,
		EntityCounts entities
) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<IslandSnapshotS2C> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "island_snapshot_s2c"));

	private static final StreamCodec<ByteBuf, Optional<BlockPos>> HOME_CODEC = ByteBufCodecs.optional(BlockPos.STREAM_CODEC);
	// IncomingInviteEntry.CODEC is already typed over RegistryFriendlyByteBuf (like every other nested
	// entry here), so unlike HOME_CODEC above this needs no ByteBuf/RegistryFriendlyByteBuf split.
	private static final StreamCodec<RegistryFriendlyByteBuf, Optional<IncomingInviteEntry>> INCOMING_INVITE_CODEC =
			ByteBufCodecs.optional(IncomingInviteEntry.CODEC);
	private static final StreamCodec<RegistryFriendlyByteBuf, List<MemberEntry>> MEMBER_LIST_CODEC =
			ByteBufCodecs.collection(ArrayList::new, MemberEntry.CODEC);
	private static final StreamCodec<RegistryFriendlyByteBuf, List<PendingInviteEntry>> PENDING_INVITE_LIST_CODEC =
			ByteBufCodecs.collection(ArrayList::new, PendingInviteEntry.CODEC);
	private static final StreamCodec<RegistryFriendlyByteBuf, List<SettingEntry>> SETTING_LIST_CODEC =
			ByteBufCodecs.collection(ArrayList::new, SettingEntry.CODEC);

	public static final StreamCodec<RegistryFriendlyByteBuf, IslandSnapshotS2C> CODEC = StreamCodec.of(
			(buf, value) -> {
				ByteBufCodecs.BOOL.encode(buf, value.exists());
				ByteBufCodecs.VAR_INT.encode(buf, value.size());
				ByteBufCodecs.VAR_INT.encode(buf, value.maxSize());
				ByteBufCodecs.STRING_UTF8.encode(buf, value.islandType());
				ByteBufCodecs.STRING_UTF8.encode(buf, value.currentBiomeId());
				ByteBufCodecs.VAR_INT.encode(buf, value.biomeCooldownRemainingSeconds());
				HOME_CODEC.encode(buf, value.home());
				ByteBufCodecs.STRING_UTF8.encode(buf, value.state());
				MEMBER_LIST_CODEC.encode(buf, value.members());
				PENDING_INVITE_LIST_CODEC.encode(buf, value.pendingInvites());
				INCOMING_INVITE_CODEC.encode(buf, value.incomingInvite());
				SETTING_LIST_CODEC.encode(buf, value.settings());
				EntityCounts.CODEC.encode(buf, value.entities());
			},
			buf -> new IslandSnapshotS2C(
					ByteBufCodecs.BOOL.decode(buf),
					ByteBufCodecs.VAR_INT.decode(buf),
					ByteBufCodecs.VAR_INT.decode(buf),
					ByteBufCodecs.STRING_UTF8.decode(buf),
					ByteBufCodecs.STRING_UTF8.decode(buf),
					ByteBufCodecs.VAR_INT.decode(buf),
					HOME_CODEC.decode(buf),
					ByteBufCodecs.STRING_UTF8.decode(buf),
					MEMBER_LIST_CODEC.decode(buf),
					PENDING_INVITE_LIST_CODEC.decode(buf),
					INCOMING_INVITE_CODEC.decode(buf),
					SETTING_LIST_CODEC.decode(buf),
					EntityCounts.CODEC.decode(buf)
			)
	);

	@Override
	public CustomPacketPayload.Type<IslandSnapshotS2C> type() {
		return TYPE;
	}

	public record MemberEntry(UUID uuid, String name, String role) {
		public static final StreamCodec<RegistryFriendlyByteBuf, MemberEntry> CODEC = StreamCodec.composite(
				net.minecraft.core.UUIDUtil.STREAM_CODEC, MemberEntry::uuid,
				ByteBufCodecs.STRING_UTF8, MemberEntry::name,
				ByteBufCodecs.STRING_UTF8, MemberEntry::role,
				MemberEntry::new
		);
	}

	public record PendingInviteEntry(String targetName, int expiresInSeconds) {
		public static final StreamCodec<RegistryFriendlyByteBuf, PendingInviteEntry> CODEC = StreamCodec.composite(
				ByteBufCodecs.STRING_UTF8, PendingInviteEntry::targetName,
				ByteBufCodecs.VAR_INT, PendingInviteEntry::expiresInSeconds,
				PendingInviteEntry::new
		);
	}

	// An invite where the receiving player is the INVITEE, not the island's owner (contrast
	// PendingInviteEntry above, which lists invites the player's own island sent out). Empty
	// (IslandSnapshotS2C#incomingInvite) means no pending incoming invite, or it already expired.
	public record IncomingInviteEntry(String inviterName, int expiresInSeconds) {
		public static final StreamCodec<RegistryFriendlyByteBuf, IncomingInviteEntry> CODEC = StreamCodec.composite(
				ByteBufCodecs.STRING_UTF8, IncomingInviteEntry::inviterName,
				ByteBufCodecs.VAR_INT, IncomingInviteEntry::expiresInSeconds,
				IncomingInviteEntry::new
		);
	}

	// key is the server's IslandSetting enum CONSTANT NAME (e.g. "FIRE_SPREAD"), not its id
	// ("firespread") — see ClientIslandCache's mapping when consuming this.
	public record SettingEntry(String key, boolean value) {
		public static final StreamCodec<RegistryFriendlyByteBuf, SettingEntry> CODEC = StreamCodec.composite(
				ByteBufCodecs.STRING_UTF8, SettingEntry::key,
				ByteBufCodecs.BOOL, SettingEntry::value,
				SettingEntry::new
		);
	}

	public record EntityCounts(int players, int hostile, int passive, int cobblemon, int items, int other) {
		public static final StreamCodec<RegistryFriendlyByteBuf, EntityCounts> CODEC = StreamCodec.composite(
				ByteBufCodecs.VAR_INT, EntityCounts::players,
				ByteBufCodecs.VAR_INT, EntityCounts::hostile,
				ByteBufCodecs.VAR_INT, EntityCounts::passive,
				ByteBufCodecs.VAR_INT, EntityCounts::cobblemon,
				ByteBufCodecs.VAR_INT, EntityCounts::items,
				ByteBufCodecs.VAR_INT, EntityCounts::other,
				EntityCounts::new
		);

		public static final EntityCounts EMPTY = new EntityCounts(0, 0, 0, 0, 0, 0);
	}
}
