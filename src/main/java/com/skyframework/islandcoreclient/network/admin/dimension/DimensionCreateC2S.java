package com.skyframework.islandcoreclient.network.admin.dimension;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

// Mirrors the server's net.admin.dimension.DimensionCreateC2S exactly. id is the path only (see
// DimensionDetailRequestC2S); style is DimensionGeneratorStyle's name; seed absent means "random".
public record DimensionCreateC2S(String id, String displayName, String style, Optional<Long> seed) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<DimensionCreateC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "dimension_create_c2s"));

	private static final StreamCodec<ByteBuf, Optional<Long>> SEED_CODEC = ByteBufCodecs.optional(ByteBufCodecs.VAR_LONG);

	public static final StreamCodec<RegistryFriendlyByteBuf, DimensionCreateC2S> CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, DimensionCreateC2S::id,
			ByteBufCodecs.STRING_UTF8, DimensionCreateC2S::displayName,
			ByteBufCodecs.STRING_UTF8, DimensionCreateC2S::style,
			SEED_CODEC, DimensionCreateC2S::seed,
			DimensionCreateC2S::new
	);

	@Override
	public CustomPacketPayload.Type<DimensionCreateC2S> type() {
		return TYPE;
	}
}
