package com.skyframework.islandcoreclient.network.party;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server record exactly. targetName: same offline-name-resolution reasoning as MemberInviteC2S.
public record PartyInviteC2S(String targetName) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<PartyInviteC2S> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "party_invite_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, PartyInviteC2S> CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, PartyInviteC2S::targetName,
			PartyInviteC2S::new
	);

	@Override
	public CustomPacketPayload.Type<PartyInviteC2S> type() {
		return TYPE;
	}
}
