package com.skyframework.islandcoreclient.network.member;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server record exactly. targetName instead of a UUID: an ally doesn't have to
// already be a member the client has a UUID for — same reasoning as MemberInviteC2S.
public record MemberAllyAddC2S(String targetName) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<MemberAllyAddC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "member_ally_add_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, MemberAllyAddC2S> CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, MemberAllyAddC2S::targetName,
			MemberAllyAddC2S::new
	);

	@Override
	public CustomPacketPayload.Type<MemberAllyAddC2S> type() {
		return TYPE;
	}
}
