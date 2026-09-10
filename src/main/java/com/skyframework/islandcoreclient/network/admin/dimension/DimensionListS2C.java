package com.skyframework.islandcoreclient.network.admin.dimension;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

// Mirrors the server's net.admin.dimension.DimensionListS2C exactly.
public record DimensionListS2C(List<DimensionEntry> dimensions) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<DimensionListS2C> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "dimension_list_s2c"));

	private static final StreamCodec<RegistryFriendlyByteBuf, List<DimensionEntry>> DIMENSION_LIST_CODEC =
			ByteBufCodecs.collection(ArrayList::new, DimensionEntry.CODEC);

	public static final StreamCodec<RegistryFriendlyByteBuf, DimensionListS2C> CODEC = StreamCodec.composite(
			DIMENSION_LIST_CODEC, DimensionListS2C::dimensions,
			DimensionListS2C::new
	);

	@Override
	public CustomPacketPayload.Type<DimensionListS2C> type() {
		return TYPE;
	}

	// Wire field order: id (full ResourceLocation as a String, e.g. "islandcore:foo"), displayName,
	// style (DimensionGeneratorStyle name), seed, state (DimensionState name).
	public record DimensionEntry(String id, String displayName, String style, long seed, String state) {
		public static final StreamCodec<RegistryFriendlyByteBuf, DimensionEntry> CODEC = StreamCodec.composite(
				ByteBufCodecs.STRING_UTF8, DimensionEntry::id,
				ByteBufCodecs.STRING_UTF8, DimensionEntry::displayName,
				ByteBufCodecs.STRING_UTF8, DimensionEntry::style,
				ByteBufCodecs.VAR_LONG, DimensionEntry::seed,
				ByteBufCodecs.STRING_UTF8, DimensionEntry::state,
				DimensionEntry::new
		);
	}
}
