package com.skyframework.islandcoreclient.network.admin.defaults;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server record exactly. Sets the server-wide default preset for one exception group.
// Operator-only.
public record AdminExceptionSetServerDefaultC2S(String groupId, String preset) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<AdminExceptionSetServerDefaultC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "admin_exception_set_server_default_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, AdminExceptionSetServerDefaultC2S> CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, AdminExceptionSetServerDefaultC2S::groupId,
			ByteBufCodecs.STRING_UTF8, AdminExceptionSetServerDefaultC2S::preset,
			AdminExceptionSetServerDefaultC2S::new
	);

	@Override
	public CustomPacketPayload.Type<AdminExceptionSetServerDefaultC2S> type() {
		return TYPE;
	}
}
