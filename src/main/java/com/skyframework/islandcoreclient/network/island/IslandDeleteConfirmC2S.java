package com.skyframework.islandcoreclient.network.island;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Empty on purpose, mirrors the server record exactly.
public record IslandDeleteConfirmC2S() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<IslandDeleteConfirmC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "island_delete_confirm_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, IslandDeleteConfirmC2S> CODEC =
			StreamCodec.unit(new IslandDeleteConfirmC2S());

	@Override
	public CustomPacketPayload.Type<IslandDeleteConfirmC2S> type() {
		return TYPE;
	}
}
