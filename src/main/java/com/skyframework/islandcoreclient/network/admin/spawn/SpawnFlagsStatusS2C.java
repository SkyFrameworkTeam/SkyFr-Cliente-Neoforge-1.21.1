package com.skyframework.islandcoreclient.network.admin.spawn;

import com.skyframework.islandcoreclient.network.flag.FlagsStatusS2C;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

// Mirrors the server record exactly. Same shape/entries as a normal island's own FlagsStatusS2C —
// a separate payload id so the client routes it to ClientSpawnFlagsCache instead of
// ClientIslandCache's own flags.
public record SpawnFlagsStatusS2C(List<FlagsStatusS2C.FlagEntry> flags) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<SpawnFlagsStatusS2C> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "spawn_flags_status_s2c"));

	private static final StreamCodec<RegistryFriendlyByteBuf, List<FlagsStatusS2C.FlagEntry>> FLAG_LIST_CODEC =
			ByteBufCodecs.collection(ArrayList::new, FlagsStatusS2C.FlagEntry.CODEC);

	public static final StreamCodec<RegistryFriendlyByteBuf, SpawnFlagsStatusS2C> CODEC = StreamCodec.composite(
			FLAG_LIST_CODEC, SpawnFlagsStatusS2C::flags,
			SpawnFlagsStatusS2C::new
	);

	@Override
	public CustomPacketPayload.Type<SpawnFlagsStatusS2C> type() {
		return TYPE;
	}
}
