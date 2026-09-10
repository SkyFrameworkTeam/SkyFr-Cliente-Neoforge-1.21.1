package com.skyframework.islandcoreclient.network.member;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.UUIDUtil;

import java.util.UUID;

// Mirrors the server record exactly. Covers both /island untrust and /island kick server-side —
// the server decides which applies from the target's current role, so the client only needs one
// "remove this member" button regardless of role.
public record MemberRemoveC2S(UUID targetUuid) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<MemberRemoveC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "member_remove_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, MemberRemoveC2S> CODEC = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC, MemberRemoveC2S::targetUuid,
			MemberRemoveC2S::new
	);

	@Override
	public CustomPacketPayload.Type<MemberRemoveC2S> type() {
		return TYPE;
	}
}
