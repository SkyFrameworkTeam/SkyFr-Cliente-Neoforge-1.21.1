package com.skyframework.islandcoreclient.network.party;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Empty on purpose. Arms the server's 15s disband confirmation window — mirrors the two-step
// IslandDeleteRequestC2S/IslandDeleteConfirmC2S flow, just shorter.
public record PartyDisbandRequestC2S() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<PartyDisbandRequestC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "party_disband_request_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, PartyDisbandRequestC2S> CODEC = StreamCodec.unit(new PartyDisbandRequestC2S());

	@Override
	public CustomPacketPayload.Type<PartyDisbandRequestC2S> type() {
		return TYPE;
	}
}
