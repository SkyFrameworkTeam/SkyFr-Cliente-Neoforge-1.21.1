package com.skyframework.islandcoreclient.network.teleport;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Empty on purpose, mirrors the server record exactly.
public record TeleportStatusRequestC2S() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<TeleportStatusRequestC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "teleport_status_request_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, TeleportStatusRequestC2S> CODEC =
			StreamCodec.unit(new TeleportStatusRequestC2S());

	@Override
	public CustomPacketPayload.Type<TeleportStatusRequestC2S> type() {
		return TYPE;
	}
}
