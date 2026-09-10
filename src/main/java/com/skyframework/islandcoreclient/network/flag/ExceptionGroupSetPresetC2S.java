package com.skyframework.islandcoreclient.network.flag;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server record exactly. Replaces the old boolean-shaped ExceptionGroupSetC2S —
// exception groups now resolve per role, exact mirror of FlagSetPresetC2S. preset:
// "nadie"/"miembros"/"aliados"/"todos". Only valid for owner-configurable groups.
public record ExceptionGroupSetPresetC2S(String groupId, String preset) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<ExceptionGroupSetPresetC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "exception_group_set_preset_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, ExceptionGroupSetPresetC2S> CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, ExceptionGroupSetPresetC2S::groupId,
			ByteBufCodecs.STRING_UTF8, ExceptionGroupSetPresetC2S::preset,
			ExceptionGroupSetPresetC2S::new
	);

	@Override
	public CustomPacketPayload.Type<ExceptionGroupSetPresetC2S> type() {
		return TYPE;
	}
}
