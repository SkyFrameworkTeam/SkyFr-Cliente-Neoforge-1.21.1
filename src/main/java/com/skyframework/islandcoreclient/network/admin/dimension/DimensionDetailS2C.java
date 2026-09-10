package com.skyframework.islandcoreclient.network.admin.dimension;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server's net.admin.dimension.DimensionDetailS2C exactly: 7 fields (id, displayName,
// style, seed, state, createdAt, updatedAt), past PacketCodec.tuple's 6-argument limit so
// hand-written with PacketCodec.of. Sent only on success — not found replies with
// ActionResultS2C.fail(DIMENSION_NOT_FOUND) instead.
public record DimensionDetailS2C(
		String id,
		String displayName,
		String style,
		long seed,
		String state,
		String createdAt,
		String updatedAt
) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<DimensionDetailS2C> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "dimension_detail_s2c"));

	public static final StreamCodec<RegistryFriendlyByteBuf, DimensionDetailS2C> CODEC = StreamCodec.of(
			(buf, value) -> {
				ByteBufCodecs.STRING_UTF8.encode(buf, value.id());
				ByteBufCodecs.STRING_UTF8.encode(buf, value.displayName());
				ByteBufCodecs.STRING_UTF8.encode(buf, value.style());
				ByteBufCodecs.VAR_LONG.encode(buf, value.seed());
				ByteBufCodecs.STRING_UTF8.encode(buf, value.state());
				ByteBufCodecs.STRING_UTF8.encode(buf, value.createdAt());
				ByteBufCodecs.STRING_UTF8.encode(buf, value.updatedAt());
			},
			buf -> new DimensionDetailS2C(
					ByteBufCodecs.STRING_UTF8.decode(buf),
					ByteBufCodecs.STRING_UTF8.decode(buf),
					ByteBufCodecs.STRING_UTF8.decode(buf),
					ByteBufCodecs.VAR_LONG.decode(buf),
					ByteBufCodecs.STRING_UTF8.decode(buf),
					ByteBufCodecs.STRING_UTF8.decode(buf),
					ByteBufCodecs.STRING_UTF8.decode(buf)
			)
	);

	@Override
	public CustomPacketPayload.Type<DimensionDetailS2C> type() {
		return TYPE;
	}
}
