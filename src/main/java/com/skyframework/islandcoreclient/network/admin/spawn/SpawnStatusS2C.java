package com.skyframework.islandcoreclient.network.admin.spawn;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;

import java.util.Optional;

// Mirrors the server's net.admin.spawn.SpawnStatusS2C exactly: exists, size (0 if !exists),
// homeLocation (empty if !exists). exists = false is a normal state (Spawn island simply doesn't
// exist yet), never wrapped in an ActionResultS2C failure.
public record SpawnStatusS2C(boolean exists, int size, Optional<BlockPos> homeLocation) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<SpawnStatusS2C> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "spawn_status_s2c"));

	// See IslandSnapshotS2C's HOME_CODEC comment: ByteBufCodecs.optional needs an exact ByteBuf
	// match, so this stays typed over plain ByteBuf rather than RegistryFriendlyByteBuf.
	private static final StreamCodec<ByteBuf, Optional<BlockPos>> HOME_CODEC = ByteBufCodecs.optional(BlockPos.STREAM_CODEC);

	public static final StreamCodec<RegistryFriendlyByteBuf, SpawnStatusS2C> CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL, SpawnStatusS2C::exists,
			ByteBufCodecs.VAR_INT, SpawnStatusS2C::size,
			HOME_CODEC, SpawnStatusS2C::homeLocation,
			SpawnStatusS2C::new
	);

	@Override
	public CustomPacketPayload.Type<SpawnStatusS2C> type() {
		return TYPE;
	}
}
