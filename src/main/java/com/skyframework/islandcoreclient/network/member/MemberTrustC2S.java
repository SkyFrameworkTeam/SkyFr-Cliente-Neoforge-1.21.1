package com.skyframework.islandcoreclient.network.member;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.UUIDUtil;

import java.util.UUID;

// Mirrors the server record exactly. targetUuid: the client already has this from its own
// IslandSnapshotS2C member list.
public record MemberTrustC2S(UUID targetUuid) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<MemberTrustC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "member_trust_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, MemberTrustC2S> CODEC = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC, MemberTrustC2S::targetUuid,
			MemberTrustC2S::new
	);

	@Override
	public CustomPacketPayload.Type<MemberTrustC2S> type() {
		return TYPE;
	}
}
