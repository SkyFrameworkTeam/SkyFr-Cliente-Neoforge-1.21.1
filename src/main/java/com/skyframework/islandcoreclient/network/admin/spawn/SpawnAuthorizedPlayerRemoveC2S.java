package com.skyframework.islandcoreclient.network.admin.spawn;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.UUIDUtil;

import java.util.UUID;

// Mirrors the server's net.admin.spawn.SpawnAuthorizedPlayerRemoveC2S exactly: targetUuid (UUID) —
// always targets an existing entry from the authorizedPlayers list the client already fetched via
// SpawnBuildProtectionStatusS2C.
public record SpawnAuthorizedPlayerRemoveC2S(UUID targetUuid) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<SpawnAuthorizedPlayerRemoveC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "spawn_authorized_player_remove_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SpawnAuthorizedPlayerRemoveC2S> CODEC = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC, SpawnAuthorizedPlayerRemoveC2S::targetUuid,
			SpawnAuthorizedPlayerRemoveC2S::new
	);

	@Override
	public CustomPacketPayload.Type<SpawnAuthorizedPlayerRemoveC2S> type() {
		return TYPE;
	}
}
