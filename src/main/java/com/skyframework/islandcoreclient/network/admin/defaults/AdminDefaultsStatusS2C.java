package com.skyframework.islandcoreclient.network.admin.defaults;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

// Mirrors the server record exactly. The server-wide default configuration for every ROLE_BASED
// flag, every exception group, and (Sprint "teletransportes dinámicos" Admin Permisos/General work)
// every ISLAND_GLOBAL flag — not any specific island.
//
// Wire format changed: globalDefaults (list of GlobalDefaultEntry) was added as a 3rd field — a
// client/server protocol break, see the server's AdminDefaultsStatusS2C class javadoc.
public record AdminDefaultsStatusS2C(
		List<FlagDefaultEntry> flagDefaults, List<ExceptionDefaultEntry> exceptionDefaults, List<GlobalDefaultEntry> globalDefaults
) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<AdminDefaultsStatusS2C> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "admin_defaults_status_s2c"));

	private static final StreamCodec<RegistryFriendlyByteBuf, List<FlagDefaultEntry>> FLAG_DEFAULT_LIST_CODEC =
			ByteBufCodecs.collection(ArrayList::new, FlagDefaultEntry.CODEC);
	private static final StreamCodec<RegistryFriendlyByteBuf, List<ExceptionDefaultEntry>> EXCEPTION_DEFAULT_LIST_CODEC =
			ByteBufCodecs.collection(ArrayList::new, ExceptionDefaultEntry.CODEC);
	private static final StreamCodec<RegistryFriendlyByteBuf, List<GlobalDefaultEntry>> GLOBAL_DEFAULT_LIST_CODEC =
			ByteBufCodecs.collection(ArrayList::new, GlobalDefaultEntry.CODEC);

	public static final StreamCodec<RegistryFriendlyByteBuf, AdminDefaultsStatusS2C> CODEC = StreamCodec.composite(
			FLAG_DEFAULT_LIST_CODEC, AdminDefaultsStatusS2C::flagDefaults,
			EXCEPTION_DEFAULT_LIST_CODEC, AdminDefaultsStatusS2C::exceptionDefaults,
			GLOBAL_DEFAULT_LIST_CODEC, AdminDefaultsStatusS2C::globalDefaults,
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

	// currentValue: "allow"/"deny"/"default" — same ClientTriState vocabulary TriStateRow already
	// cycles through for an island's own ISLAND_GLOBAL override.
	public record GlobalDefaultEntry(String flagId, String currentValue) {
		public static final StreamCodec<RegistryFriendlyByteBuf, GlobalDefaultEntry> CODEC = StreamCodec.composite(
				ByteBufCodecs.STRING_UTF8, GlobalDefaultEntry::flagId,
				ByteBufCodecs.STRING_UTF8, GlobalDefaultEntry::currentValue,
				GlobalDefaultEntry::new
		);
	}
}
