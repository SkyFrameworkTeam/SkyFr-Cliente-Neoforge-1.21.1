package com.skyframework.islandcoreclient.network.island;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server record exactly. settingId must be IslandSetting#getId() on the server side
// (e.g. "firespread"/"pvp"/"mobdamage") — NOT the enum constant name IslandSnapshotS2C.SettingEntry
// carries (see ClientIslandCache#SERVER_SETTING_ENUM_TO_ID for the mapping between the two).
public record IslandSettingsUpdateC2S(String settingId, boolean value) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<IslandSettingsUpdateC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "island_settings_update_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, IslandSettingsUpdateC2S> CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, IslandSettingsUpdateC2S::settingId,
			ByteBufCodecs.BOOL, IslandSettingsUpdateC2S::value,
			IslandSettingsUpdateC2S::new
	);

	@Override
	public CustomPacketPayload.Type<IslandSettingsUpdateC2S> type() {
		return TYPE;
	}
}
