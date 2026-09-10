package com.skyframework.islandcoreclient.network.island;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Empty on purpose, mirrors the server record exactly.
public record IslandSnapshotRequestC2S() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<IslandSnapshotRequestC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "island_snapshot_request_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, IslandSnapshotRequestC2S> CODEC =
			StreamCodec.unit(new IslandSnapshotRequestC2S());

	@Override
	public CustomPacketPayload.Type<IslandSnapshotRequestC2S> type() {
		return TYPE;
	}
}
