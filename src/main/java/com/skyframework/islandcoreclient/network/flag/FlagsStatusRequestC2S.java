package com.skyframework.islandcoreclient.network.flag;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Empty on purpose, mirrors the server record exactly.
public record FlagsStatusRequestC2S() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<FlagsStatusRequestC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "flags_status_request_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, FlagsStatusRequestC2S> CODEC =
			StreamCodec.unit(new FlagsStatusRequestC2S());

	@Override
	public CustomPacketPayload.Type<FlagsStatusRequestC2S> type() {
		return TYPE;
	}
}
