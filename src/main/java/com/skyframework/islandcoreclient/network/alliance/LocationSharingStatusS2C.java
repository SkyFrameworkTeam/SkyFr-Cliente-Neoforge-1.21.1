package com.skyframework.islandcoreclient.network.alliance;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Mirrors the server record exactly. The receiving player's own four location-sharing toggles: two
// independent pairs, party and allies, each with its own send/receive half.
//
// Wire field order: sendPositionToParty, receivePositionsFromParty, sendPositionToAllies,
// receivePositionsFromAllies.
public record LocationSharingStatusS2C(
		boolean sendPositionToParty,
		boolean receivePositionsFromParty,
		boolean sendPositionToAllies,
		boolean receivePositionsFromAllies
) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<LocationSharingStatusS2C> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "location_sharing_status_s2c"));

	public static final StreamCodec<RegistryFriendlyByteBuf, LocationSharingStatusS2C> CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL, LocationSharingStatusS2C::sendPositionToParty,
			ByteBufCodecs.BOOL, LocationSharingStatusS2C::receivePositionsFromParty,
			ByteBufCodecs.BOOL, LocationSharingStatusS2C::sendPositionToAllies,
			ByteBufCodecs.BOOL, LocationSharingStatusS2C::receivePositionsFromAllies,
			LocationSharingStatusS2C::new
	);

	@Override
	public CustomPacketPayload.Type<LocationSharingStatusS2C> type() {
		return TYPE;
	}
}
