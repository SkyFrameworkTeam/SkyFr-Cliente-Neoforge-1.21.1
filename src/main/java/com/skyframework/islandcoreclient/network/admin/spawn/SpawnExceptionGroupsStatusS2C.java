package com.skyframework.islandcoreclient.network.admin.spawn;

import com.skyframework.islandcoreclient.network.flag.ExceptionGroupsStatusS2C;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

// Mirrors the server record exactly. Same shape/entries as a normal island's own
// ExceptionGroupsStatusS2C — a separate payload id so the client routes it to
// ClientSpawnFlagsCache instead of ClientIslandCache's own exceptionGroups.
public record SpawnExceptionGroupsStatusS2C(List<ExceptionGroupsStatusS2C.GroupEntry> groups) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<SpawnExceptionGroupsStatusS2C> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "spawn_exception_groups_status_s2c"));

	private static final StreamCodec<RegistryFriendlyByteBuf, List<ExceptionGroupsStatusS2C.GroupEntry>> GROUP_LIST_CODEC =
			ByteBufCodecs.collection(ArrayList::new, ExceptionGroupsStatusS2C.GroupEntry.CODEC);

	public static final StreamCodec<RegistryFriendlyByteBuf, SpawnExceptionGroupsStatusS2C> CODEC = StreamCodec.composite(
			GROUP_LIST_CODEC, SpawnExceptionGroupsStatusS2C::groups,
			SpawnExceptionGroupsStatusS2C::new
	);

	@Override
	public CustomPacketPayload.Type<SpawnExceptionGroupsStatusS2C> type() {
		return TYPE;
	}
}
