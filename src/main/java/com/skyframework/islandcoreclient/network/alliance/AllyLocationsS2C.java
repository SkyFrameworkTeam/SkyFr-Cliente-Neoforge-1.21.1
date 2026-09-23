package com.skyframework.islandcoreclient.network.alliance;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.UUIDUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Mirrors the server record exactly. Pushed periodically (not requested by this client) — see
// ClientAllyLocationsCache, the only consumer, and AllyHudRenderer, the only reader of that cache.
public record AllyLocationsS2C(List<Entry> entries) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<AllyLocationsS2C> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "ally_locations_s2c"));

	private static final StreamCodec<RegistryFriendlyByteBuf, List<Entry>> ENTRY_LIST_CODEC =
			ByteBufCodecs.collection(ArrayList::new, Entry.CODEC);

	public static final StreamCodec<RegistryFriendlyByteBuf, AllyLocationsS2C> CODEC = StreamCodec.composite(
			ENTRY_LIST_CODEC, AllyLocationsS2C::entries,
			AllyLocationsS2C::new
	);

	@Override
	public CustomPacketPayload.Type<AllyLocationsS2C> type() {
		return TYPE;
	}

	public record Entry(UUID uuid, String name, double x, double y, double z) {
		public static final StreamCodec<RegistryFriendlyByteBuf, Entry> CODEC = StreamCodec.composite(
				UUIDUtil.STREAM_CODEC, Entry::uuid,
				ByteBufCodecs.STRING_UTF8, Entry::name,
				ByteBufCodecs.DOUBLE, Entry::x,
				ByteBufCodecs.DOUBLE, Entry::y,
				ByteBufCodecs.DOUBLE, Entry::z,
				Entry::new
		);
	}
}
