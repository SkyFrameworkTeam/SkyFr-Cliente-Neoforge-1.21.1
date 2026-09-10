package com.skyframework.islandcoreclient.network.admin.vanilla;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server's net.admin.vanilla.VanillaResetListRequestC2S exactly: empty, no fields.
public record VanillaResetListRequestC2S() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<VanillaResetListRequestC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "vanilla_reset_list_request_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, VanillaResetListRequestC2S> CODEC =
			StreamCodec.unit(new VanillaResetListRequestC2S());

	@Override
	public CustomPacketPayload.Type<VanillaResetListRequestC2S> type() {
		return TYPE;
	}
}
