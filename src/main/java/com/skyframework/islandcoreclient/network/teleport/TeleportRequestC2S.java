package com.skyframework.islandcoreclient.network.teleport;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import io.netty.buffer.ByteBuf;

import java.util.Optional;

// Mirrors the server record exactly, including the nested Type enum (HOME/SPAWN/RTP/DIMENSION,
// same names as ClientTeleportType so the two can convert 1:1) and the STRING-based enum codec
// (Type::valueOf / Enum::name).
//
// dimensionId is only ever present (and only ever read server-side) when type == DIMENSION: the
// fixed FARMING type from the old single hardcoded farming button was removed (Sprint
// "teletransportes dinámicos") in favor of this generic one — send back the same Identifier string
// TeleportStatusS2C.DimensionTeleportEntry#id gave for whichever dynamic-section button was
// clicked, farming's own entry included.
public record TeleportRequestC2S(Type action, Optional<String> dimensionId) implements CustomPacketPayload {

	public enum Type {
		HOME, SPAWN, RTP, DIMENSION, OVERWORLD
	}

	public static final CustomPacketPayload.Type<TeleportRequestC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "teleport_request_c2s"));

	private static final StreamCodec<ByteBuf, Type> TYPE_CODEC = ByteBufCodecs.STRING_UTF8.map(Type::valueOf, Enum::name);
	private static final StreamCodec<ByteBuf, Optional<String>> DIMENSION_ID_CODEC = ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8);

	public static final StreamCodec<RegistryFriendlyByteBuf, TeleportRequestC2S> CODEC = StreamCodec.composite(
			TYPE_CODEC, TeleportRequestC2S::action,
			DIMENSION_ID_CODEC, TeleportRequestC2S::dimensionId,
			TeleportRequestC2S::new
	);

	@Override
	public CustomPacketPayload.Type<TeleportRequestC2S> type() {
		return TYPE;
	}

	// Convenience factories, so call sites don't spell out Optional.empty()/Optional.of() by hand.
	public static TeleportRequestC2S fixed(Type type) {
		return new TeleportRequestC2S(type, Optional.empty());
	}

	public static TeleportRequestC2S dimension(String dimensionId) {
		return new TeleportRequestC2S(Type.DIMENSION, Optional.of(dimensionId));
	}
}
