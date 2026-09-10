package com.skyframework.islandcoreclient.network.admin.spawn;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server's net.admin.spawn.SpawnBuildProtectionStatusRequestC2S exactly: empty, no
// fields.
public record SpawnBuildProtectionStatusRequestC2S() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<SpawnBuildProtectionStatusRequestC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "spawn_build_protection_status_request_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SpawnBuildProtectionStatusRequestC2S> CODEC =
			StreamCodec.unit(new SpawnBuildProtectionStatusRequestC2S());

	@Override
	public CustomPacketPayload.Type<SpawnBuildProtectionStatusRequestC2S> type() {
		return TYPE;
	}
}
