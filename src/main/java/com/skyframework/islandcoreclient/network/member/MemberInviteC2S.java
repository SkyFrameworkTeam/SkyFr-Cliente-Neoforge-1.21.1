package com.skyframework.islandcoreclient.network.member;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server record exactly. targetName instead of a UUID: the client has no reliable
// way to know an offline player's UUID up front; the server resolves it (online players first,
// then its offline profile cache).
public record MemberInviteC2S(String targetName) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<MemberInviteC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "member_invite_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, MemberInviteC2S> CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, MemberInviteC2S::targetName,
			MemberInviteC2S::new
	);

	@Override
	public CustomPacketPayload.Type<MemberInviteC2S> type() {
		return TYPE;
	}
}
