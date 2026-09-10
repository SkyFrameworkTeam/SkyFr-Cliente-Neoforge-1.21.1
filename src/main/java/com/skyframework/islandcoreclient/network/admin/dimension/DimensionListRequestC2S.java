package com.skyframework.islandcoreclient.network.admin.dimension;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server's net.admin.dimension.DimensionListRequestC2S exactly: empty, no fields, no
// pagination (matches "/dimension list" itself, which doesn't paginate either).
public record DimensionListRequestC2S() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<DimensionListRequestC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "dimension_list_request_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, DimensionListRequestC2S> CODEC =
			StreamCodec.unit(new DimensionListRequestC2S());

	@Override
	public CustomPacketPayload.Type<DimensionListRequestC2S> type() {
		return TYPE;
	}
}
