package com.skyframework.islandcoreclient.network.party;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.UUIDUtil;

import java.util.UUID;

// Mirrors the server record exactly. targetUuid: the client already has this from its own
// PartyStatusS2C member list.
public record PartyKickC2S(UUID targetUuid) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<PartyKickC2S> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "party_kick_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, PartyKickC2S> CODEC = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC, PartyKickC2S::targetUuid,
			PartyKickC2S::new
	);

	@Override
	public CustomPacketPayload.Type<PartyKickC2S> type() {
		return TYPE;
	}
}
