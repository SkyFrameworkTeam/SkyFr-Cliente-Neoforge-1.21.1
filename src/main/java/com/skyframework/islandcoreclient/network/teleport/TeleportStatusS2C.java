package com.skyframework.islandcoreclient.network.teleport;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

// Mirrors the server record exactly. reasonKey is only ever present when enabled == false:
// SPAWN_DISABLED/FARMING_DISABLED for the two config-gated destinations, RTP_DISABLED/
// RTP_DIMENSION_NOT_ALLOWED for rtp. home never sets it — there's no server-wide toggle for
// /island home.
public record TeleportStatusS2C(StatusEntry home, StatusEntry spawn, StatusEntry rtp, StatusEntry farming) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<TeleportStatusS2C> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "teleport_status_s2c"));

	public static final StreamCodec<RegistryFriendlyByteBuf, TeleportStatusS2C> CODEC = StreamCodec.composite(
			StatusEntry.CODEC, TeleportStatusS2C::home,
			StatusEntry.CODEC, TeleportStatusS2C::spawn,
			StatusEntry.CODEC, TeleportStatusS2C::rtp,
			StatusEntry.CODEC, TeleportStatusS2C::farming,
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
}
