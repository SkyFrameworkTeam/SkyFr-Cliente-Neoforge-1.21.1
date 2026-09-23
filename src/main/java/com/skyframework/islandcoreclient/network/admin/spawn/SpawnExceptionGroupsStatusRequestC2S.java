package com.skyframework.islandcoreclient.network.admin.spawn;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server record exactly. Empty on purpose, like SpawnFlagsStatusRequestC2S.
public record SpawnExceptionGroupsStatusRequestC2S() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<SpawnExceptionGroupsStatusRequestC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "spawn_exception_groups_status_request_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SpawnExceptionGroupsStatusRequestC2S> CODEC =
			StreamCodec.unit(new SpawnExceptionGroupsStatusRequestC2S());

	@Override
	public CustomPacketPayload.Type<SpawnExceptionGroupsStatusRequestC2S> type() {
		return TYPE;
	}
}
