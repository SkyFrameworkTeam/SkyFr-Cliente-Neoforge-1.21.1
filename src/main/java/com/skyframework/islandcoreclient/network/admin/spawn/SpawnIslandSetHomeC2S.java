package com.skyframework.islandcoreclient.network.admin.spawn;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server's net.admin.spawn.SpawnIslandSetHomeC2S exactly: empty, no coordinates. The
// server reads the ACTUAL sender's position itself — the client must NEVER send a BlockPos here.
public record SpawnIslandSetHomeC2S() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<SpawnIslandSetHomeC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "spawn_island_set_home_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SpawnIslandSetHomeC2S> CODEC =
			StreamCodec.unit(new SpawnIslandSetHomeC2S());

	@Override
	public CustomPacketPayload.Type<SpawnIslandSetHomeC2S> type() {
		return TYPE;
	}
}
