package com.skyframework.islandcoreclient.network.teleport;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Mirrors the server record exactly. reasonKey is only ever present when enabled == false:
// SPAWN_DISABLED for the one remaining config-gated fixed destination, RTP_DISABLED/
// RTP_DIMENSION_NOT_ALLOWED for rtp. home never sets it — there's no server-wide toggle for
// /island home.
//
// Wire format changed (Sprint "teletransportes dinámicos"): the fixed 4th field `farming`
// (StatusEntry) was replaced by `dimensions` (List<DimensionTeleportEntry>) — one entry per
// DIMENSION_REGISTRY dimension instead of a single hardcoded farming slot. Final field order:
// home, spawn, rtp, dimensions.
public record TeleportStatusS2C(StatusEntry home, StatusEntry spawn, StatusEntry rtp, List<DimensionTeleportEntry> dimensions) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<TeleportStatusS2C> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "teleport_status_s2c"));

	private static final StreamCodec<RegistryFriendlyByteBuf, List<DimensionTeleportEntry>> DIMENSION_LIST_CODEC =
			ByteBufCodecs.collection(ArrayList::new, DimensionTeleportEntry.CODEC);

	public static final StreamCodec<RegistryFriendlyByteBuf, TeleportStatusS2C> CODEC = StreamCodec.composite(
			StatusEntry.CODEC, TeleportStatusS2C::home,
			StatusEntry.CODEC, TeleportStatusS2C::spawn,
			StatusEntry.CODEC, TeleportStatusS2C::rtp,
			DIMENSION_LIST_CODEC, TeleportStatusS2C::dimensions,
			TeleportStatusS2C::new
	);

	@Override
	public CustomPacketPayload.Type<TeleportStatusS2C> type() {
		return TYPE;
	}

	public record StatusEntry(boolean enabled, long cooldownRemainingSeconds, Optional<String> reasonKey) {

		private static final StreamCodec<ByteBuf, Optional<String>> REASON_CODEC = ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8);

		public static final StreamCodec<RegistryFriendlyByteBuf, StatusEntry> CODEC = StreamCodec.composite(
				ByteBufCodecs.BOOL, StatusEntry::enabled,
				ByteBufCodecs.VAR_LONG, StatusEntry::cooldownRemainingSeconds,
				REASON_CODEC, StatusEntry::reasonKey,
				StatusEntry::new
		);
	}

	// id is the dimension's Identifier.toString() (e.g. "islandcore:farming") — sent back verbatim
	// in TeleportRequestC2S's dimensionId field, so there's no id<->name resolution to keep in sync.
	public record DimensionTeleportEntry(String id, String displayName, boolean enabled, long cooldownRemainingSeconds) {

		public static final StreamCodec<RegistryFriendlyByteBuf, DimensionTeleportEntry> CODEC = StreamCodec.composite(
				ByteBufCodecs.STRING_UTF8, DimensionTeleportEntry::id,
				ByteBufCodecs.STRING_UTF8, DimensionTeleportEntry::displayName,
				ByteBufCodecs.BOOL, DimensionTeleportEntry::enabled,
				ByteBufCodecs.VAR_LONG, DimensionTeleportEntry::cooldownRemainingSeconds,
				DimensionTeleportEntry::new
		);
	}
}
