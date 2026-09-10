package com.skyframework.islandcoreclient.network.island;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Empty on purpose, mirrors the server record exactly.
public record IslandUpgradeC2S() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<IslandUpgradeC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "island_upgrade_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, IslandUpgradeC2S> CODEC = StreamCodec.unit(new IslandUpgradeC2S());

	@Override
	public CustomPacketPayload.Type<IslandUpgradeC2S> type() {
		return TYPE;
	}
}
