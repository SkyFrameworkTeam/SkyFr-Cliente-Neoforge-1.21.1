package com.skyframework.islandcoreclient.network.party;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Empty on purpose, mirrors the server record exactly.
public record PartyStatusRequestC2S() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<PartyStatusRequestC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "party_status_request_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, PartyStatusRequestC2S> CODEC =
			StreamCodec.unit(new PartyStatusRequestC2S());

	@Override
	public CustomPacketPayload.Type<PartyStatusRequestC2S> type() {
		return TYPE;
	}
}
