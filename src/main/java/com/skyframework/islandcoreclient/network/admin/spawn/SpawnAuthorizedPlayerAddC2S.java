package com.skyframework.islandcoreclient.network.admin.spawn;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server's net.admin.spawn.SpawnAuthorizedPlayerAddC2S exactly: targetName (String) —
// the client has no reliable way to know an offline player's UUID up front, resolved server-side.
public record SpawnAuthorizedPlayerAddC2S(String targetName) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<SpawnAuthorizedPlayerAddC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "spawn_authorized_player_add_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SpawnAuthorizedPlayerAddC2S> CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, SpawnAuthorizedPlayerAddC2S::targetName,
			SpawnAuthorizedPlayerAddC2S::new
	);

	@Override
	public CustomPacketPayload.Type<SpawnAuthorizedPlayerAddC2S> type() {
		return TYPE;
	}
}
