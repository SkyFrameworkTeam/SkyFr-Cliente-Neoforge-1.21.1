package com.skyframework.islandcoreclient.network.island;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server record exactly. biomeId is the raw ResourceLocation string (e.g.
// "minecraft:jungle"), same as ClientBiomeView#biomeId().
public record IslandBiomeChangeC2S(String biomeId) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<IslandBiomeChangeC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "island_biome_change_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, IslandBiomeChangeC2S> CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, IslandBiomeChangeC2S::biomeId,
			IslandBiomeChangeC2S::new
	);

	@Override
	public CustomPacketPayload.Type<IslandBiomeChangeC2S> type() {
		return TYPE;
	}
}
