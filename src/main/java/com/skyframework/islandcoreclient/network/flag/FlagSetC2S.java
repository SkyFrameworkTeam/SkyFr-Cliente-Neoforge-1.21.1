package com.skyframework.islandcoreclient.network.flag;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server record exactly. value: "allow"/"deny"/"default".
public record FlagSetC2S(String flagId, String value) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<FlagSetC2S> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "flag_set_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, FlagSetC2S> CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, FlagSetC2S::flagId,
			ByteBufCodecs.STRING_UTF8, FlagSetC2S::value,
			FlagSetC2S::new
	);

	@Override
	public CustomPacketPayload.Type<FlagSetC2S> type() {
		return TYPE;
	}
}
