package com.skyframework.islandcoreclient.network.admin.spawn;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server's net.admin.spawn.SpawnBuildProtectionSetC2S exactly: enabled (boolean).
public record SpawnBuildProtectionSetC2S(boolean enabled) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<SpawnBuildProtectionSetC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "spawn_build_protection_set_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SpawnBuildProtectionSetC2S> CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL, SpawnBuildProtectionSetC2S::enabled,
			SpawnBuildProtectionSetC2S::new
	);

	@Override
	public CustomPacketPayload.Type<SpawnBuildProtectionSetC2S> type() {
		return TYPE;
	}
}
