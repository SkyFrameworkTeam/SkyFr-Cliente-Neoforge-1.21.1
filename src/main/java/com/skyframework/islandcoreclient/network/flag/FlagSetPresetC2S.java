package com.skyframework.islandcoreclient.network.flag;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server record exactly. preset: "nadie"/"miembros"/"aliados"/"todos". Only valid for
// ROLE_BASED flags.
public record FlagSetPresetC2S(String flagId, String preset) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<FlagSetPresetC2S> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "flag_set_preset_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, FlagSetPresetC2S> CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, FlagSetPresetC2S::flagId,
			ByteBufCodecs.STRING_UTF8, FlagSetPresetC2S::preset,
			FlagSetPresetC2S::new
	);

	@Override
	public CustomPacketPayload.Type<FlagSetPresetC2S> type() {
		return TYPE;
	}
}
