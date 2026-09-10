package com.skyframework.islandcoreclient.network.member;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.UUIDUtil;

import java.util.UUID;

// Mirrors the server record exactly. targetUuid: the client already has this from its own
// IslandSnapshotS2C member list (an existing ALLY entry).
public record MemberAllyRemoveC2S(UUID targetUuid) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<MemberAllyRemoveC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "member_ally_remove_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, MemberAllyRemoveC2S> CODEC = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC, MemberAllyRemoveC2S::targetUuid,
			MemberAllyRemoveC2S::new
	);

	@Override
	public CustomPacketPayload.Type<MemberAllyRemoveC2S> type() {
		return TYPE;
	}
}
