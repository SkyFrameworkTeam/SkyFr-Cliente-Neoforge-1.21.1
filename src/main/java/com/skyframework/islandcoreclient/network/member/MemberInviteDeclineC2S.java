package com.skyframework.islandcoreclient.network.member;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Empty on purpose, mirrors the server record exactly.
public record MemberInviteDeclineC2S() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<MemberInviteDeclineC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "member_invite_decline_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, MemberInviteDeclineC2S> CODEC =
			StreamCodec.unit(new MemberInviteDeclineC2S());

	@Override
	public CustomPacketPayload.Type<MemberInviteDeclineC2S> type() {
		return TYPE;
	}
}
