package com.skyframework.islandcoreclient.network.biome;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Empty on purpose, mirrors the server record exactly.
public record BiomeTiersRequestC2S() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<BiomeTiersRequestC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "biome_tiers_request_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, BiomeTiersRequestC2S> CODEC =
			StreamCodec.unit(new BiomeTiersRequestC2S());

	@Override
	public CustomPacketPayload.Type<BiomeTiersRequestC2S> type() {
		return TYPE;
	}
}
