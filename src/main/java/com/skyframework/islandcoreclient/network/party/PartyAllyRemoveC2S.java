package com.skyframework.islandcoreclient.network.party;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server record exactly.
public record PartyAllyRemoveC2S(String targetPartyName) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<PartyAllyRemoveC2S> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "party_ally_remove_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, PartyAllyRemoveC2S> CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, PartyAllyRemoveC2S::targetPartyName,
			PartyAllyRemoveC2S::new
	);

	@Override
	public CustomPacketPayload.Type<PartyAllyRemoveC2S> type() {
		return TYPE;
	}
}
