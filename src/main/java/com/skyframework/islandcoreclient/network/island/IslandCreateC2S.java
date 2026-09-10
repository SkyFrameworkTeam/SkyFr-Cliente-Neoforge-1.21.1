package com.skyframework.islandcoreclient.network.island;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Empty on purpose, mirrors the server record exactly: the server acts on the connection's own
// player, never on client-supplied data.
public record IslandCreateC2S() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<IslandCreateC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "island_create_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, IslandCreateC2S> CODEC = StreamCodec.unit(new IslandCreateC2S());

	@Override
	public CustomPacketPayload.Type<IslandCreateC2S> type() {
		return TYPE;
	}
}
