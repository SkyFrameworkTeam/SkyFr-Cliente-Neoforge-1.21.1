package com.skyframework.islandcoreclient.network.admin.vanilla;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

// Mirrors the server's net.admin.vanilla.VanillaResetQueueC2S exactly: dimension
// ("overworld"/"nether"/"end"), seedMode (the server ONLY understands "CUSTOM" or "DEFAULT" here —
// NOT the 3-way RANDOM/KEEP/SPECIFIED distinction ClientVanillaResetState.SeedMode has locally;
// "CUSTOM" maps to explicitSeed = seedValue, anything else (RANDOM or KEEP) maps to
// explicitSeed = null, deferring to the server's own VanillaResetConfig seed-mode default), and
// seedValue (only meaningful when seedMode == "CUSTOM").
public record VanillaResetQueueC2S(String dimension, String seedMode, Optional<Long> seedValue) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<VanillaResetQueueC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "vanilla_reset_queue_c2s"));

	private static final StreamCodec<ByteBuf, Optional<Long>> SEED_CODEC = ByteBufCodecs.optional(ByteBufCodecs.VAR_LONG);

	public static final StreamCodec<RegistryFriendlyByteBuf, VanillaResetQueueC2S> CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, VanillaResetQueueC2S::dimension,
			ByteBufCodecs.STRING_UTF8, VanillaResetQueueC2S::seedMode,
			SEED_CODEC, VanillaResetQueueC2S::seedValue,
			VanillaResetQueueC2S::new
	);

	@Override
	public CustomPacketPayload.Type<VanillaResetQueueC2S> type() {
		return TYPE;
	}
}
