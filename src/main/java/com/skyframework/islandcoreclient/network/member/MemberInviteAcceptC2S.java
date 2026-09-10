package com.skyframework.islandcoreclient.network.member;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Empty on purpose, mirrors the server record exactly.
public record MemberInviteAcceptC2S() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<MemberInviteAcceptC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "member_invite_accept_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, MemberInviteAcceptC2S> CODEC =
			StreamCodec.unit(new MemberInviteAcceptC2S());

	@Override
	public CustomPacketPayload.Type<MemberInviteAcceptC2S> type() {
		return TYPE;
	}
}
