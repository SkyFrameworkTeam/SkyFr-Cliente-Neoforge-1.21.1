package com.skyframework.islandcoreclient.network.admin.spawn;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server's net.admin.spawn.SpawnIslandCreateC2S exactly.
public record SpawnIslandCreateC2S(int size) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<SpawnIslandCreateC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "spawn_island_create_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SpawnIslandCreateC2S> CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, SpawnIslandCreateC2S::size,
			SpawnIslandCreateC2S::new
	);

	@Override
	public CustomPacketPayload.Type<SpawnIslandCreateC2S> type() {
		return TYPE;
	}
}
