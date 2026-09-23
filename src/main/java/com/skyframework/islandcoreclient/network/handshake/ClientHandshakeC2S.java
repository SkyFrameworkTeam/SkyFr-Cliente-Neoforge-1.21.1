package com.skyframework.islandcoreclient.network.handshake;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ClientHandshakeC2S(int protocolVersion) implements CustomPacketPayload {
	// Bumped to 6 alongside the "teletransportes dinámicos" sprint (TeleportStatusS2C's farming
	// field became a dimensions list, TeleportRequestC2S's FARMING type was replaced by DIMENSION
	// with a new dimensionId field) — must match the server's NetworkChannels.PROTOCOL_VERSION.
	public static final int CURRENT_PROTOCOL_VERSION = 6;

	// Namespaced under "islandcore", not "islandcoreclient": the server mod owns this protocol.
	public static final CustomPacketPayload.Type<ClientHandshakeC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "handshake_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, ClientHandshakeC2S> CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, ClientHandshakeC2S::protocolVersion,
			ClientHandshakeC2S::new
	);

	@Override
	public CustomPacketPayload.Type<ClientHandshakeC2S> type() {
		return TYPE;
	}
}
