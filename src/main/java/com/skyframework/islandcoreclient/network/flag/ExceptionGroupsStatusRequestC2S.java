package com.skyframework.islandcoreclient.network.flag;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Empty on purpose, mirrors the server record exactly.
public record ExceptionGroupsStatusRequestC2S() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<ExceptionGroupsStatusRequestC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "exception_groups_status_request_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, ExceptionGroupsStatusRequestC2S> CODEC =
			StreamCodec.unit(new ExceptionGroupsStatusRequestC2S());

	@Override
	public CustomPacketPayload.Type<ExceptionGroupsStatusRequestC2S> type() {
		return TYPE;
	}
}
