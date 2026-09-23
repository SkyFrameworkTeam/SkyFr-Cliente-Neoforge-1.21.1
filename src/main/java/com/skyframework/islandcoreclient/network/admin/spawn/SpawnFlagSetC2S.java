package com.skyframework.islandcoreclient.network.admin.spawn;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server record exactly. Network equivalent of a normal island's own FlagSetC2S,
// targeting the Spawn island instead.
public record SpawnFlagSetC2S(String flagId, String value) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<SpawnFlagSetC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "spawn_flag_set_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SpawnFlagSetC2S> CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, SpawnFlagSetC2S::flagId,
			ByteBufCodecs.STRING_UTF8, SpawnFlagSetC2S::value,
			SpawnFlagSetC2S::new
	);

	@Override
	public CustomPacketPayload.Type<SpawnFlagSetC2S> type() {
		return TYPE;
	}
}
