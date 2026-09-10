package com.skyframework.islandcoreclient.network.admin.defaults;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

// Mirrors the server record exactly. The server-wide default configuration for every ROLE_BASED
// flag and every exception group — not any specific island. Scoped to preset-shaped entries only:
// the 3 ISLAND_GLOBAL flags have no preset concept and stay command-only server-side (see the
// server's AdminDefaultsStatusS2C class javadoc), so DefaultConfigScreen never shows them.
public record AdminDefaultsStatusS2C(List<FlagDefaultEntry> flagDefaults, List<ExceptionDefaultEntry> exceptionDefaults) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<AdminDefaultsStatusS2C> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "admin_defaults_status_s2c"));

	private static final StreamCodec<RegistryFriendlyByteBuf, List<FlagDefaultEntry>> FLAG_DEFAULT_LIST_CODEC =
			ByteBufCodecs.collection(ArrayList::new, FlagDefaultEntry.CODEC);
	private static final StreamCodec<RegistryFriendlyByteBuf, List<ExceptionDefaultEntry>> EXCEPTION_DEFAULT_LIST_CODEC =
			ByteBufCodecs.collection(ArrayList::new, ExceptionDefaultEntry.CODEC);

	public static final StreamCodec<RegistryFriendlyByteBuf, AdminDefaultsStatusS2C> CODEC = StreamCodec.composite(
			FLAG_DEFAULT_LIST_CODEC, AdminDefaultsStatusS2C::flagDefaults,
			EXCEPTION_DEFAULT_LIST_CODEC, AdminDefaultsStatusS2C::exceptionDefaults,
			AdminDefaultsStatusS2C::new
	);

	@Override
	public CustomPacketPayload.Type<AdminDefaultsStatusS2C> type() {
		return TYPE;
	}

	public record FlagDefaultEntry(String flagId, String currentPreset) {
		public static final StreamCodec<RegistryFriendlyByteBuf, FlagDefaultEntry> CODEC = StreamCodec.composite(
				ByteBufCodecs.STRING_UTF8, FlagDefaultEntry::flagId,
				ByteBufCodecs.STRING_UTF8, FlagDefaultEntry::currentPreset,
				FlagDefaultEntry::new
		);
	}

	public record ExceptionDefaultEntry(String groupId, String currentPreset) {
		public static final StreamCodec<RegistryFriendlyByteBuf, ExceptionDefaultEntry> CODEC = StreamCodec.composite(
				ByteBufCodecs.STRING_UTF8, ExceptionDefaultEntry::groupId,
				ByteBufCodecs.STRING_UTF8, ExceptionDefaultEntry::currentPreset,
				ExceptionDefaultEntry::new
		);
	}
}
