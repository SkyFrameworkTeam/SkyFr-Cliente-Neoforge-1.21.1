package com.skyframework.islandcoreclient.network.admin.dimension;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

// Mirrors the server's net.admin.dimension.DimensionRegenerateC2S exactly. id is the path only;
// seed absent means "random", same as DimensionCreateC2S.
public record DimensionRegenerateC2S(String id, Optional<Long> seed) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<DimensionRegenerateC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "dimension_regenerate_c2s"));

	private static final StreamCodec<ByteBuf, Optional<Long>> SEED_CODEC = ByteBufCodecs.optional(ByteBufCodecs.VAR_LONG);

	public static final StreamCodec<RegistryFriendlyByteBuf, DimensionRegenerateC2S> CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, DimensionRegenerateC2S::id,
			SEED_CODEC, DimensionRegenerateC2S::seed,
			DimensionRegenerateC2S::new
	);

	@Override
	public CustomPacketPayload.Type<DimensionRegenerateC2S> type() {
		return TYPE;
	}
}
