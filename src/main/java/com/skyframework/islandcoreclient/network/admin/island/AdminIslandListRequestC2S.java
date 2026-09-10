package com.skyframework.islandcoreclient.network.admin.island;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server's net.admin.island.AdminIslandListRequestC2S exactly: same 3 fields in the
// same order. page is 0-indexed (page 0 is the first page); pageSize is clamped to at least 1
// server-side; searchQuery empty means "no filter" (matched against the resolved owner name).
public record AdminIslandListRequestC2S(int page, int pageSize, String searchQuery) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<AdminIslandListRequestC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "admin_island_list_request_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, AdminIslandListRequestC2S> CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, AdminIslandListRequestC2S::page,
			ByteBufCodecs.VAR_INT, AdminIslandListRequestC2S::pageSize,
			ByteBufCodecs.STRING_UTF8, AdminIslandListRequestC2S::searchQuery,
			AdminIslandListRequestC2S::new
	);

	@Override
	public CustomPacketPayload.Type<AdminIslandListRequestC2S> type() {
		return TYPE;
	}
}
