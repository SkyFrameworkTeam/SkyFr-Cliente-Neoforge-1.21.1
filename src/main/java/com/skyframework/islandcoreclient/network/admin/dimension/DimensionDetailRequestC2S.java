package com.skyframework.islandcoreclient.network.admin.dimension;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server's net.admin.dimension.DimensionDetailRequestC2S exactly. id is the
// dimension's PATH ONLY (e.g. "foo" for "islandcore:foo") — the server builds the full ResourceLocation
// itself. Note this differs from DimensionListS2C.DimensionEntry#id, which carries the FULL
// identifier string; callers must strip the "islandcore:" namespace before sending this request.
public record DimensionDetailRequestC2S(String id) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<DimensionDetailRequestC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "dimension_detail_request_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, DimensionDetailRequestC2S> CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, DimensionDetailRequestC2S::id,
			DimensionDetailRequestC2S::new
	);

	@Override
	public CustomPacketPayload.Type<DimensionDetailRequestC2S> type() {
		return TYPE;
	}
}
