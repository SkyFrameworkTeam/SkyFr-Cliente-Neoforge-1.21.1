package com.skyframework.islandcoreclient.network.admin.island;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.UUIDUtil;

import java.util.UUID;

// Mirrors the server's net.admin.island.AdminIslandDetailRequestC2S exactly. targetUuid is the
// island OWNER's uuid, not the islandId.
public record AdminIslandDetailRequestC2S(UUID targetUuid) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<AdminIslandDetailRequestC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "admin_island_detail_request_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, AdminIslandDetailRequestC2S> CODEC = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC, AdminIslandDetailRequestC2S::targetUuid,
			AdminIslandDetailRequestC2S::new
	);

	@Override
	public CustomPacketPayload.Type<AdminIslandDetailRequestC2S> type() {
		return TYPE;
	}
}
