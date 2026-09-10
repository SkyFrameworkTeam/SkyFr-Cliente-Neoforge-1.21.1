package com.skyframework.islandcoreclient.network;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

// Mirrors the server's net.ActionResultS2C exactly (fields, order, codec, channel id): generic
// reply for every action C2S packet (create, upgrade, delete request/confirm, settings, biome,
// invite/accept/trust/remove, teleport). reasonKey is one of IslandCore's ActionReason constants,
// present only when success == false. Carries no numeric extra data (e.g. no exact cooldown
// seconds on a rejected biome change) — the server-side ActionOutcome#data() is not forwarded.
public record ActionResultS2C(boolean success, Optional<String> reasonKey) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<ActionResultS2C> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "action_result_s2c"));

	private static final StreamCodec<ByteBuf, Optional<String>> REASON_CODEC = ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8);

	public static final StreamCodec<RegistryFriendlyByteBuf, ActionResultS2C> CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL, ActionResultS2C::success,
			REASON_CODEC, ActionResultS2C::reasonKey,
			ActionResultS2C::new
	);

	@Override
	public CustomPacketPayload.Type<ActionResultS2C> type() {
		return TYPE;
	}
}
