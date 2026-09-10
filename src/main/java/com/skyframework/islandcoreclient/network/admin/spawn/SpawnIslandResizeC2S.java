package com.skyframework.islandcoreclient.network.admin.spawn;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server's net.admin.spawn.SpawnIslandResizeC2S exactly.
public record SpawnIslandResizeC2S(int newSize) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<SpawnIslandResizeC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "spawn_island_resize_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SpawnIslandResizeC2S> CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, SpawnIslandResizeC2S::newSize,
			SpawnIslandResizeC2S::new
	);

	@Override
	public CustomPacketPayload.Type<SpawnIslandResizeC2S> type() {
		return TYPE;
	}
}
