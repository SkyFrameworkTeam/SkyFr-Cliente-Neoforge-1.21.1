package com.skyframework.islandcoreclient.network.party;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Empty on purpose, mirrors the server record exactly.
public record PartyAcceptC2S() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<PartyAcceptC2S> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "party_accept_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, PartyAcceptC2S> CODEC = StreamCodec.unit(new PartyAcceptC2S());

	@Override
	public CustomPacketPayload.Type<PartyAcceptC2S> type() {
		return TYPE;
	}
}
