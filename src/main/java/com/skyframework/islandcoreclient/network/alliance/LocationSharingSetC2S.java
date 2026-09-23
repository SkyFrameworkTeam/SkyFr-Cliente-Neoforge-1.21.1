package com.skyframework.islandcoreclient.network.alliance;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server record exactly. Sets all four toggles at once — the full desired state, not a
// single flip.
//
// Wire field order: sendPositionToParty, receivePositionsFromParty, sendPositionToAllies,
// receivePositionsFromAllies.
public record LocationSharingSetC2S(
		boolean sendPositionToParty,
		boolean receivePositionsFromParty,
		boolean sendPositionToAllies,
		boolean receivePositionsFromAllies
) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<LocationSharingSetC2S> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "location_sharing_set_c2s"));

	public static final StreamCodec<RegistryFriendlyByteBuf, LocationSharingSetC2S> CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL, LocationSharingSetC2S::sendPositionToParty,
			ByteBufCodecs.BOOL, LocationSharingSetC2S::receivePositionsFromParty,
			ByteBufCodecs.BOOL, LocationSharingSetC2S::sendPositionToAllies,
			ByteBufCodecs.BOOL, LocationSharingSetC2S::receivePositionsFromAllies,
			LocationSharingSetC2S::new
	);

	@Override
	public CustomPacketPayload.Type<LocationSharingSetC2S> type() {
		return TYPE;
	}
}
