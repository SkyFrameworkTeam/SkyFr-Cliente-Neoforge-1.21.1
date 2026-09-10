package com.skyframework.islandcoreclient.network.admin.defaults;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server record exactly. Sets the server-wide default preset for one ROLE_BASED flag.
// Operator-only; ISLAND_GLOBAL flags aren't reachable through this payload.
public record AdminFlagSetServerDefaultC2S(String flagId, String preset) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<AdminFlagSetServerDefaultC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "admin_flag_set_server_default_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, AdminFlagSetServerDefaultC2S> CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, AdminFlagSetServerDefaultC2S::flagId,
			ByteBufCodecs.STRING_UTF8, AdminFlagSetServerDefaultC2S::preset,
			AdminFlagSetServerDefaultC2S::new
	);

	@Override
	public CustomPacketPayload.Type<AdminFlagSetServerDefaultC2S> type() {
		return TYPE;
	}
}
