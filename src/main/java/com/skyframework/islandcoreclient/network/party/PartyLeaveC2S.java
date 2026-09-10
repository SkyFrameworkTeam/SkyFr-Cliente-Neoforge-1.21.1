package com.skyframework.islandcoreclient.network.party;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Empty on purpose, mirrors the server record exactly.
public record PartyLeaveC2S() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<PartyLeaveC2S> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "party_leave_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, PartyLeaveC2S> CODEC = StreamCodec.unit(new PartyLeaveC2S());

	@Override
	public CustomPacketPayload.Type<PartyLeaveC2S> type() {
		return TYPE;
	}
}
