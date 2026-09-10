package com.skyframework.islandcoreclient.network.admin.dimension;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server's net.admin.dimension.DimensionDeleteConfirmC2S exactly. id is the path only.
public record DimensionDeleteConfirmC2S(String id) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<DimensionDeleteConfirmC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "dimension_delete_confirm_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, DimensionDeleteConfirmC2S> CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, DimensionDeleteConfirmC2S::id,
			DimensionDeleteConfirmC2S::new
	);

	@Override
	public CustomPacketPayload.Type<DimensionDeleteConfirmC2S> type() {
		return TYPE;
	}
}
