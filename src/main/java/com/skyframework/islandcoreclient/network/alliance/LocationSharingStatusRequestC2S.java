package com.skyframework.islandcoreclient.network.alliance;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Empty on purpose, mirrors the server record exactly.
public record LocationSharingStatusRequestC2S() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<LocationSharingStatusRequestC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "location_sharing_status_request_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, LocationSharingStatusRequestC2S> CODEC =
			StreamCodec.unit(new LocationSharingStatusRequestC2S());

	@Override
	public CustomPacketPayload.Type<LocationSharingStatusRequestC2S> type() {
		return TYPE;
	}
}
