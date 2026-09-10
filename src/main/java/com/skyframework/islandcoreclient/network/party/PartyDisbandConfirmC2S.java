package com.skyframework.islandcoreclient.network.party;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Empty on purpose. Only succeeds if PartyDisbandRequestC2S was sent within the last 15s.
public record PartyDisbandConfirmC2S() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<PartyDisbandConfirmC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "party_disband_confirm_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, PartyDisbandConfirmC2S> CODEC = StreamCodec.unit(new PartyDisbandConfirmC2S());

	@Override
	public CustomPacketPayload.Type<PartyDisbandConfirmC2S> type() {
		return TYPE;
	}
}
