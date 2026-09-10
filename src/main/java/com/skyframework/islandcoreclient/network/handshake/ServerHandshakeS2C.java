package com.skyframework.islandcoreclient.network.handshake;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Field order: protocolVersion, protocolCompatible, isOperator — must match the server's
// net.handshake.ServerHandshakeS2C exactly. protocolCompatible is true when the protocolVersion
// this client sent in ClientHandshakeC2S matched the server's own PROTOCOL_VERSION; see
// ClientConnectionState#onServerHandshakeReceived for how the client reacts to it being false.
public record ServerHandshakeS2C(int protocolVersion, boolean protocolCompatible, boolean isOperator) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<ServerHandshakeS2C> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "handshake_s2c"));

	public static final StreamCodec<RegistryFriendlyByteBuf, ServerHandshakeS2C> CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, ServerHandshakeS2C::protocolVersion,
			ByteBufCodecs.BOOL, ServerHandshakeS2C::protocolCompatible,
			ByteBufCodecs.BOOL, ServerHandshakeS2C::isOperator,
			ServerHandshakeS2C::new
	);

	@Override
	public CustomPacketPayload.Type<ServerHandshakeS2C> type() {
		return TYPE;
	}
}
