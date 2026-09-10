package com.skyframework.islandcoreclient.network.admin.island;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.UUIDUtil;

import java.util.UUID;

// Mirrors the server's net.admin.island.AdminIslandDeleteC2S exactly.
public record AdminIslandDeleteC2S(UUID targetUuid) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<AdminIslandDeleteC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "admin_island_delete_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, AdminIslandDeleteC2S> CODEC = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC, AdminIslandDeleteC2S::targetUuid,
			AdminIslandDeleteC2S::new
	);

	@Override
	public CustomPacketPayload.Type<AdminIslandDeleteC2S> type() {
		return TYPE;
	}
}
