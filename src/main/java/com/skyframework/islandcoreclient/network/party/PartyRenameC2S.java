package com.skyframework.islandcoreclient.network.party;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server record exactly.
public record PartyRenameC2S(String newName) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<PartyRenameC2S> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "party_rename_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, PartyRenameC2S> CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, PartyRenameC2S::newName,
			PartyRenameC2S::new
	);

	@Override
	public CustomPacketPayload.Type<PartyRenameC2S> type() {
		return TYPE;
	}
}
