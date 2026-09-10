package com.skyframework.islandcoreclient.network.admin.spawn;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server's net.admin.spawn.SpawnStatusRequestC2S exactly: empty, no fields.
public record SpawnStatusRequestC2S() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<SpawnStatusRequestC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "spawn_status_request_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SpawnStatusRequestC2S> CODEC =
			StreamCodec.unit(new SpawnStatusRequestC2S());

	@Override
	public CustomPacketPayload.Type<SpawnStatusRequestC2S> type() {
		return TYPE;
	}
}
