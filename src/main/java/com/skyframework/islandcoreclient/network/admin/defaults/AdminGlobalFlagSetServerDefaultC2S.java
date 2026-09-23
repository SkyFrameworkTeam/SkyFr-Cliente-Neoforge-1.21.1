package com.skyframework.islandcoreclient.network.admin.defaults;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server record exactly. Sets the server-wide default TriState override for one
// ISLAND_GLOBAL flag. Operator-only; ROLE_BASED flags aren't reachable through this payload — see
// AdminFlagSetServerDefaultC2S for those.
public record AdminGlobalFlagSetServerDefaultC2S(String flagId, String value) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<AdminGlobalFlagSetServerDefaultC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "admin_global_flag_set_server_default_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, AdminGlobalFlagSetServerDefaultC2S> CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, AdminGlobalFlagSetServerDefaultC2S::flagId,
			ByteBufCodecs.STRING_UTF8, AdminGlobalFlagSetServerDefaultC2S::value,
			AdminGlobalFlagSetServerDefaultC2S::new
	);

	@Override
	public CustomPacketPayload.Type<AdminGlobalFlagSetServerDefaultC2S> type() {
		return TYPE;
	}
}
