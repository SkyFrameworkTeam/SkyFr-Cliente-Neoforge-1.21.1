package com.skyframework.islandcoreclient.network.admin.spawn;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server record exactly. Network equivalent of a normal island's own FlagSetPresetC2S,
// targeting the Spawn island instead.
public record SpawnFlagSetPresetC2S(String flagId, String preset) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<SpawnFlagSetPresetC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "spawn_flag_set_preset_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SpawnFlagSetPresetC2S> CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, SpawnFlagSetPresetC2S::flagId,
			ByteBufCodecs.STRING_UTF8, SpawnFlagSetPresetC2S::preset,
			SpawnFlagSetPresetC2S::new
	);

	@Override
	public CustomPacketPayload.Type<SpawnFlagSetPresetC2S> type() {
		return TYPE;
	}
}
