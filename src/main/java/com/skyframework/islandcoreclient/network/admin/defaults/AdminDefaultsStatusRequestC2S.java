package com.skyframework.islandcoreclient.network.admin.defaults;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server record exactly. Empty on purpose — requests the current server-wide defaults
// (not any specific island's), see AdminDefaultsStatusS2C. Operator-only.
public record AdminDefaultsStatusRequestC2S() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<AdminDefaultsStatusRequestC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "admin_defaults_status_request_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, AdminDefaultsStatusRequestC2S> CODEC =
			StreamCodec.unit(new AdminDefaultsStatusRequestC2S());

	@Override
	public CustomPacketPayload.Type<AdminDefaultsStatusRequestC2S> type() {
		return TYPE;
	}
}
