package com.skyframework.islandcoreclient.network.admin.spawn;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.UUIDUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Mirrors the server's net.admin.spawn.SpawnBuildProtectionStatusS2C exactly: enabled (the Spawn
// island's current BUILD_PROTECTION value, false if Spawn doesn't exist), authorizedPlayers (list
// of AuthorizedPlayerEntry: the Spawn island's MEMBER/CO_OWNER members, empty if Spawn doesn't
// exist).
public record SpawnBuildProtectionStatusS2C(boolean enabled, List<AuthorizedPlayerEntry> authorizedPlayers) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<SpawnBuildProtectionStatusS2C> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "spawn_build_protection_status_s2c"));

	private static final StreamCodec<RegistryFriendlyByteBuf, List<AuthorizedPlayerEntry>> AUTHORIZED_PLAYERS_CODEC =
			ByteBufCodecs.collection(ArrayList::new, AuthorizedPlayerEntry.CODEC);

	public static final StreamCodec<RegistryFriendlyByteBuf, SpawnBuildProtectionStatusS2C> CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL, SpawnBuildProtectionStatusS2C::enabled,
			AUTHORIZED_PLAYERS_CODEC, SpawnBuildProtectionStatusS2C::authorizedPlayers,
			SpawnBuildProtectionStatusS2C::new
	);

	@Override
	public CustomPacketPayload.Type<SpawnBuildProtectionStatusS2C> type() {
		return TYPE;
	}

	public record AuthorizedPlayerEntry(UUID uuid, String name, String role) {
		public static final StreamCodec<RegistryFriendlyByteBuf, AuthorizedPlayerEntry> CODEC = StreamCodec.composite(
				UUIDUtil.STREAM_CODEC, AuthorizedPlayerEntry::uuid,
				ByteBufCodecs.STRING_UTF8, AuthorizedPlayerEntry::name,
				ByteBufCodecs.STRING_UTF8, AuthorizedPlayerEntry::role,
				AuthorizedPlayerEntry::new
		);
	}
}
