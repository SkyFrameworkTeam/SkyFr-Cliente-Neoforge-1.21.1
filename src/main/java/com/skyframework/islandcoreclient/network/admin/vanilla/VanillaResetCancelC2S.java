package com.skyframework.islandcoreclient.network.admin.vanilla;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server's net.admin.vanilla.VanillaResetCancelC2S exactly. Cancels a QUEUED
// (already-confirmed) reset only.
public record VanillaResetCancelC2S(String dimension) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<VanillaResetCancelC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "vanilla_reset_cancel_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, VanillaResetCancelC2S> CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, VanillaResetCancelC2S::dimension,
			VanillaResetCancelC2S::new
	);

	@Override
	public CustomPacketPayload.Type<VanillaResetCancelC2S> type() {
		return TYPE;
	}
}
