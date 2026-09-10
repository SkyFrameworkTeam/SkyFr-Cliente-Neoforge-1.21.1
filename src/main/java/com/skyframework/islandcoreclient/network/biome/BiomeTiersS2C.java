package com.skyframework.islandcoreclient.network.biome;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Mirrors the server record exactly. permissionRequired is empty for a base tier. label
// (BiomeEntry) is a plain display string computed server-side from the biome's ResourceLocation path
// (e.g. "minecraft:dark_forest" -> "Dark Forest") — NOT a translation key.
public record BiomeTiersS2C(List<TierEntry> tiers) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<BiomeTiersS2C> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "biome_tiers_s2c"));

	private static final StreamCodec<RegistryFriendlyByteBuf, List<TierEntry>> TIERS_CODEC =
			ByteBufCodecs.collection(ArrayList::new, TierEntry.CODEC);

	public static final StreamCodec<RegistryFriendlyByteBuf, BiomeTiersS2C> CODEC = StreamCodec.composite(
			TIERS_CODEC, BiomeTiersS2C::tiers,
			BiomeTiersS2C::new
	);

	@Override
	public CustomPacketPayload.Type<BiomeTiersS2C> type() {
		return TYPE;
	}

	public record TierEntry(String tierId, Optional<String> permissionRequired, boolean unlocked, List<BiomeEntry> biomes) {

		private static final StreamCodec<ByteBuf, Optional<String>> PERMISSION_CODEC = ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8);
		private static final StreamCodec<RegistryFriendlyByteBuf, List<BiomeEntry>> BIOMES_CODEC =
				ByteBufCodecs.collection(ArrayList::new, BiomeEntry.CODEC);

		public static final StreamCodec<RegistryFriendlyByteBuf, TierEntry> CODEC = StreamCodec.composite(
				ByteBufCodecs.STRING_UTF8, TierEntry::tierId,
				PERMISSION_CODEC, TierEntry::permissionRequired,
				ByteBufCodecs.BOOL, TierEntry::unlocked,
				BIOMES_CODEC, TierEntry::biomes,
				TierEntry::new
		);
	}

	public record BiomeEntry(String biomeId, String label) {
		public static final StreamCodec<RegistryFriendlyByteBuf, BiomeEntry> CODEC = StreamCodec.composite(
				ByteBufCodecs.STRING_UTF8, BiomeEntry::biomeId,
				ByteBufCodecs.STRING_UTF8, BiomeEntry::label,
				BiomeEntry::new
		);
	}
}
