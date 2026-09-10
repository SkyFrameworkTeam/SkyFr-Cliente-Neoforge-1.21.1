package com.skyframework.islandcoreclient.network.flag;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

// Mirrors the server's net.flag.FlagsStatusS2C exactly. Only sent when the requesting player has
// an island — if not, the server sends ActionResultS2C.fail("no_island") instead. flags is in
// FlagRegistry.all()'s registration order (construccion, interact, entities, redstone, fire_spread,
// pvp_damage, mob_damage, crop_trample, natural_mob_spawning, raids).
public record FlagsStatusS2C(List<FlagEntry> flags) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<FlagsStatusS2C> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "flags_status_s2c"));

	private static final StreamCodec<RegistryFriendlyByteBuf, List<FlagEntry>> FLAG_LIST_CODEC =
			ByteBufCodecs.collection(ArrayList::new, FlagEntry.CODEC);

	public static final StreamCodec<RegistryFriendlyByteBuf, FlagsStatusS2C> CODEC = StreamCodec.composite(
			FLAG_LIST_CODEC, FlagsStatusS2C::flags,
			FlagsStatusS2C::new
	);

	@Override
	public CustomPacketPayload.Type<FlagsStatusS2C> type() {
		return TYPE;
	}

	// category: "ROLE_BASED" or "ISLAND_GLOBAL". resolvedValue: ISLAND_GLOBAL only, meaningless
	// (false) for a ROLE_BASED entry — read resolvedByRole instead. resolvedByRole: ROLE_BASED only,
	// one entry per IslandRole, empty for an ISLAND_GLOBAL entry. islandOverride: this island's own
	// override ("ALLOW"/"DENY"/"DEFAULT") — a single value even for ROLE_BASED, since "/island flags
	// set" (unlike the preset) always overrides every role uniformly. currentPreset: ROLE_BASED only
	// (added after islandOverride) — "nadie"/"miembros"/"aliados"/"todos" if the current
	// VISITOR/ALLY/MEMBER combination exactly matches one of those presets, or "custom" if
	// not; always "" for an ISLAND_GLOBAL entry. missingRequiredPermission: true only if the server's
	// FlagPermissionRequirements has a node set for this flag and the requesting player (the island
	// owner) lacks it — presentation only, SettingsScreen dims the row and explains why; the real
	// gate is server-side.
	public record FlagEntry(
			String flagId, String category, boolean resolvedValue, List<RoleValueEntry> resolvedByRole, String islandOverride, String currentPreset,
			boolean missingRequiredPermission
	) {
		private static final StreamCodec<RegistryFriendlyByteBuf, List<RoleValueEntry>> ROLE_VALUE_LIST_CODEC =
				ByteBufCodecs.collection(ArrayList::new, RoleValueEntry.CODEC);

		// 7 fields is past PacketCodec#tuple's 6-argument limit, so this is hand-written with
		// PacketCodec#of instead — mirrors the server's net.flag.FlagsStatusS2C.FlagEntry#CODEC.
		public static final StreamCodec<RegistryFriendlyByteBuf, FlagEntry> CODEC = StreamCodec.of(
				(buf, value) -> {
					ByteBufCodecs.STRING_UTF8.encode(buf, value.flagId());
					ByteBufCodecs.STRING_UTF8.encode(buf, value.category());
					ByteBufCodecs.BOOL.encode(buf, value.resolvedValue());
					ROLE_VALUE_LIST_CODEC.encode(buf, value.resolvedByRole());
					ByteBufCodecs.STRING_UTF8.encode(buf, value.islandOverride());
					ByteBufCodecs.STRING_UTF8.encode(buf, value.currentPreset());
					ByteBufCodecs.BOOL.encode(buf, value.missingRequiredPermission());
				},
				buf -> new FlagEntry(
						ByteBufCodecs.STRING_UTF8.decode(buf),
						ByteBufCodecs.STRING_UTF8.decode(buf),
						ByteBufCodecs.BOOL.decode(buf),
						ROLE_VALUE_LIST_CODEC.decode(buf),
						ByteBufCodecs.STRING_UTF8.decode(buf),
						ByteBufCodecs.STRING_UTF8.decode(buf),
						ByteBufCodecs.BOOL.decode(buf)
				)
		);
	}

	// value is always "ALLOW" or "DENY" — never "DEFAULT".
	public record RoleValueEntry(String role, String value) {
		public static final StreamCodec<RegistryFriendlyByteBuf, RoleValueEntry> CODEC = StreamCodec.composite(
				ByteBufCodecs.STRING_UTF8, RoleValueEntry::role,
				ByteBufCodecs.STRING_UTF8, RoleValueEntry::value,
				RoleValueEntry::new
		);
	}
}
