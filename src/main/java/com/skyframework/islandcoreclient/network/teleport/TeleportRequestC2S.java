package com.skyframework.islandcoreclient.network.teleport;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server record exactly, including the nested Type enum (HOME/SPAWN/RTP/FARMING,
// same names as ClientTeleportType so the two can convert 1:1) and the STRING-based enum codec
// (Type::valueOf / Enum::name). The record component is named "action" (not "type") because
// CustomPacketPayload's own abstract method is itself called type() — must match the server's own
// TeleportRequestC2S#action rename exactly, since both sides read/write the same wire position by
// field order, not by name.
public record TeleportRequestC2S(Type action) implements CustomPacketPayload {

	public enum Type {
		HOME, SPAWN, RTP, FARMING
	}

	public static final CustomPacketPayload.Type<TeleportRequestC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "teleport_request_c2s"));

	private static final StreamCodec<ByteBuf, Type> ACTION_CODEC = ByteBufCodecs.STRING_UTF8.map(Type::valueOf, Enum::name);

	public static final StreamCodec<RegistryFriendlyByteBuf, TeleportRequestC2S> CODEC = StreamCodec.composite(
			ACTION_CODEC, TeleportRequestC2S::action,
			TeleportRequestC2S::new
	);

	@Override
	public CustomPacketPayload.Type<TeleportRequestC2S> type() {
		return TYPE;
	}
}
