package com.skyframework.islandcoreclient.network.admin.vanilla;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server's net.admin.vanilla.VanillaResetConfirmC2S exactly.
public record VanillaResetConfirmC2S(String dimension) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<VanillaResetConfirmC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "vanilla_reset_confirm_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, VanillaResetConfirmC2S> CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, VanillaResetConfirmC2S::dimension,
			VanillaResetConfirmC2S::new
	);

	@Override
	public CustomPacketPayload.Type<VanillaResetConfirmC2S> type() {
		return TYPE;
	}
}
