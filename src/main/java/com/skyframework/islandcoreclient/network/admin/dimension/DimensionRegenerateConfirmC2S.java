package com.skyframework.islandcoreclient.network.admin.dimension;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server's net.admin.dimension.DimensionRegenerateConfirmC2S exactly. id is the path only.
public record DimensionRegenerateConfirmC2S(String id) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<DimensionRegenerateConfirmC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "dimension_regenerate_confirm_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, DimensionRegenerateConfirmC2S> CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, DimensionRegenerateConfirmC2S::id,
			DimensionRegenerateConfirmC2S::new
	);

	@Override
	public CustomPacketPayload.Type<DimensionRegenerateConfirmC2S> type() {
		return TYPE;
	}
}
