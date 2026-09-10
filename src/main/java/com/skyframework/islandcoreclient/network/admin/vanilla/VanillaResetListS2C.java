package com.skyframework.islandcoreclient.network.admin.vanilla;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.UUIDUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Mirrors the server's net.admin.vanilla.VanillaResetListS2C exactly.
public record VanillaResetListS2C(List<QueueEntry> queue) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<VanillaResetListS2C> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "vanilla_reset_list_s2c"));

	private static final StreamCodec<RegistryFriendlyByteBuf, List<QueueEntry>> QUEUE_CODEC =
			ByteBufCodecs.collection(ArrayList::new, QueueEntry.CODEC);

	public static final StreamCodec<RegistryFriendlyByteBuf, VanillaResetListS2C> CODEC = StreamCodec.composite(
			QUEUE_CODEC, VanillaResetListS2C::queue,
			VanillaResetListS2C::new
	);

	@Override
	public CustomPacketPayload.Type<VanillaResetListS2C> type() {
		return TYPE;
	}

	// Wire field order: dimensionKey ("overworld"/"nether"/"end"), seed (empty means "keeps the
	// dimension's current seed"), seedMode (PendingVanillaReset.SeedMode name —
	// "RANDOM"/"KEEP"/"CUSTOM", persisted verbatim server-side from how the seed was actually
	// decided at confirm time, since a resolved RANDOM seed and a CUSTOM one are otherwise
	// indistinguishable once resolved — do NOT re-derive this from whether seed is present),
	// requestedBy, status (PendingVanillaReset.Status name — always "IN_PROGRESS" today, since
	// that's the only status ever persisted to the queue file).
	public record QueueEntry(String dimensionKey, Optional<Long> seed, String seedMode, UUID requestedBy, String status) {
		private static final StreamCodec<ByteBuf, Optional<Long>> SEED_CODEC = ByteBufCodecs.optional(ByteBufCodecs.VAR_LONG);

		public static final StreamCodec<RegistryFriendlyByteBuf, QueueEntry> CODEC = StreamCodec.composite(
				ByteBufCodecs.STRING_UTF8, QueueEntry::dimensionKey,
				SEED_CODEC, QueueEntry::seed,
				ByteBufCodecs.STRING_UTF8, QueueEntry::seedMode,
				UUIDUtil.STREAM_CODEC, QueueEntry::requestedBy,
				ByteBufCodecs.STRING_UTF8, QueueEntry::status,
				QueueEntry::new
		);
	}
}
