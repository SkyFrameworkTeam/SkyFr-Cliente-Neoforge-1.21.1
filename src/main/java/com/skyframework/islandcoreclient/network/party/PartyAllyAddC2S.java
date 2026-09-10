package com.skyframework.islandcoreclient.network.party;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server record exactly. targetPartyName: parties have no client-side UUID cache to
// pick from, resolved server-side by name.
public record PartyAllyAddC2S(String targetPartyName) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<PartyAllyAddC2S> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "party_ally_add_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, PartyAllyAddC2S> CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, PartyAllyAddC2S::targetPartyName,
			PartyAllyAddC2S::new
	);

	@Override
	public CustomPacketPayload.Type<PartyAllyAddC2S> type() {
		return TYPE;
	}
}
