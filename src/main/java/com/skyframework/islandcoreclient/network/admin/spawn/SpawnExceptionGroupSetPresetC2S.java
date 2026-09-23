package com.skyframework.islandcoreclient.network.admin.spawn;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server record exactly. Network equivalent of "/island admin spawn exceptions preset".
public record SpawnExceptionGroupSetPresetC2S(String groupId, String preset) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<SpawnExceptionGroupSetPresetC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "spawn_exception_group_set_preset_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SpawnExceptionGroupSetPresetC2S> CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, SpawnExceptionGroupSetPresetC2S::groupId,
			ByteBufCodecs.STRING_UTF8, SpawnExceptionGroupSetPresetC2S::preset,
			SpawnExceptionGroupSetPresetC2S::new
	);

	@Override
	public CustomPacketPayload.Type<SpawnExceptionGroupSetPresetC2S> type() {
		return TYPE;
	}
}
