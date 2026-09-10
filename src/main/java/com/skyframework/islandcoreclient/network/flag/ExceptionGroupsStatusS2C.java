package com.skyframework.islandcoreclient.network.flag;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

// Mirrors the server's net.flag.ExceptionGroupsStatusS2C exactly. Only sent when the requesting
// player has an island — if not, the server sends ActionResultS2C.fail("no_island") instead.
// Exception groups now resolve per role exactly like ROLE_BASED flags (see the server's
// ExceptionResolver), so GroupEntry mirrors FlagsStatusS2C.FlagEntry's shape instead of the old
// single "enabled" boolean.
public record ExceptionGroupsStatusS2C(List<GroupEntry> groups) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<ExceptionGroupsStatusS2C> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "exception_groups_status_s2c"));

	private static final StreamCodec<RegistryFriendlyByteBuf, List<GroupEntry>> GROUP_LIST_CODEC =
			ByteBufCodecs.collection(ArrayList::new, GroupEntry.CODEC);

	public static final StreamCodec<RegistryFriendlyByteBuf, ExceptionGroupsStatusS2C> CODEC = StreamCodec.composite(
			GROUP_LIST_CODEC, ExceptionGroupsStatusS2C::groups,
			ExceptionGroupsStatusS2C::new
	);

	@Override
	public CustomPacketPayload.Type<ExceptionGroupsStatusS2C> type() {
		return TYPE;
	}

	// category: "BLOCK" or "ENTITY". resolvedByRole: one entry per IslandRole, reusing
	// FlagsStatusS2C.RoleValueEntry exactly (same shape, no reason to duplicate it). currentPreset:
	// "nadie"/"miembros"/"aliados"/"todos" if the current VISITOR/ALLY/MEMBER combination exactly
	// matches one of those presets, or "custom" if not.
	public record GroupEntry(
			String groupId, String category, List<FlagsStatusS2C.RoleValueEntry> resolvedByRole, String currentPreset, boolean ownerConfigurable
	) {
		private static final StreamCodec<RegistryFriendlyByteBuf, List<FlagsStatusS2C.RoleValueEntry>> ROLE_VALUE_LIST_CODEC =
				ByteBufCodecs.collection(ArrayList::new, FlagsStatusS2C.RoleValueEntry.CODEC);

		public static final StreamCodec<RegistryFriendlyByteBuf, GroupEntry> CODEC = StreamCodec.composite(
				ByteBufCodecs.STRING_UTF8, GroupEntry::groupId,
				ByteBufCodecs.STRING_UTF8, GroupEntry::category,
				ROLE_VALUE_LIST_CODEC, GroupEntry::resolvedByRole,
				ByteBufCodecs.STRING_UTF8, GroupEntry::currentPreset,
				ByteBufCodecs.BOOL, GroupEntry::ownerConfigurable,
				GroupEntry::new
		);
	}
}
