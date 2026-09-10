package com.skyframework.islandcoreclient.network.admin.defaults;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server record exactly. Sets (or, with an empty permissionNode, clears) the LuckPerms
// node required to change one flag. Operator-only; reaches both ROLE_BASED and ISLAND_GLOBAL flags.
public record AdminFlagSetRequirementC2S(String flagId, String permissionNode) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<AdminFlagSetRequirementC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "admin_flag_set_requirement_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, AdminFlagSetRequirementC2S> CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, AdminFlagSetRequirementC2S::flagId,
			ByteBufCodecs.STRING_UTF8, AdminFlagSetRequirementC2S::permissionNode,
			AdminFlagSetRequirementC2S::new
	);

	@Override
	public CustomPacketPayload.Type<AdminFlagSetRequirementC2S> type() {
		return TYPE;
	}
}
