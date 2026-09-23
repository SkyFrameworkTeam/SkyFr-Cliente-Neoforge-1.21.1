package com.skyframework.islandcoreclient.network.admin.spawn;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server record exactly. Empty on purpose, like SpawnStatusRequestC2S.
public record SpawnFlagsStatusRequestC2S() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<SpawnFlagsStatusRequestC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "spawn_flags_status_request_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SpawnFlagsStatusRequestC2S> CODEC =
			StreamCodec.unit(new SpawnFlagsStatusRequestC2S());

	@Override
	public CustomPacketPayload.Type<SpawnFlagsStatusRequestC2S> type() {
		return TYPE;
	}
}
