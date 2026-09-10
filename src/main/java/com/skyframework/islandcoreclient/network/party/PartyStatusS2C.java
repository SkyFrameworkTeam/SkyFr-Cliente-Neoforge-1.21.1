package com.skyframework.islandcoreclient.network.party;

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

// Mirrors the server's net.party.PartyStatusS2C exactly: 8 fields, hand-written PacketCodec.of
// (past PacketCodec.tuple's 6-argument limit). hasParty = false is a normal, valid state (not an
// error), same as IslandSnapshotS2C#exists — every other field is a default/empty placeholder in
// that case, EXCEPT incomingInvite, which is the one field that can still be populated then.
//
// incomingInvite mirrors IslandSnapshotS2C#incomingInvite: an invite where the receiving player is
// the INVITEE. In practice only ever non-empty when hasParty is false — the server refuses to
// invite a player who's already in a party, so a player who has one can never also have a pending
// invite.
public record PartyStatusS2C(
		boolean hasParty,
		UUID partyId,
		String name,
		UUID leaderUuid,
		String leaderName,
		List<MemberEntry> members,
		List<AlliedPartyEntry> alliedParties,
		Optional<IncomingPartyInviteEntry> incomingInvite
) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<PartyStatusS2C> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("islandcore", "party_status_s2c"));

	// Sentinel for "no party" — mirrors Island.SERVER_OWNER_UUID's own zero-UUID convention server-side.
	public static final UUID NO_PARTY_UUID = new UUID(0, 0);

	private static final StreamCodec<RegistryFriendlyByteBuf, List<MemberEntry>> MEMBER_LIST_CODEC =
			ByteBufCodecs.collection(ArrayList::new, MemberEntry.CODEC);
	private static final StreamCodec<RegistryFriendlyByteBuf, List<AlliedPartyEntry>> ALLIED_PARTY_LIST_CODEC =
			ByteBufCodecs.collection(ArrayList::new, AlliedPartyEntry.CODEC);
	private static final StreamCodec<RegistryFriendlyByteBuf, Optional<IncomingPartyInviteEntry>> INCOMING_INVITE_CODEC =
			ByteBufCodecs.optional(IncomingPartyInviteEntry.CODEC);

	public static final StreamCodec<RegistryFriendlyByteBuf, PartyStatusS2C> CODEC = StreamCodec.of(
			(buf, value) -> {
				ByteBufCodecs.BOOL.encode(buf, value.hasParty());
				UUIDUtil.STREAM_CODEC.encode(buf, value.partyId());
				ByteBufCodecs.STRING_UTF8.encode(buf, value.name());
				UUIDUtil.STREAM_CODEC.encode(buf, value.leaderUuid());
				ByteBufCodecs.STRING_UTF8.encode(buf, value.leaderName());
				MEMBER_LIST_CODEC.encode(buf, value.members());
				ALLIED_PARTY_LIST_CODEC.encode(buf, value.alliedParties());
				INCOMING_INVITE_CODEC.encode(buf, value.incomingInvite());
			},
			buf -> new PartyStatusS2C(
					ByteBufCodecs.BOOL.decode(buf),
					UUIDUtil.STREAM_CODEC.decode(buf),
					ByteBufCodecs.STRING_UTF8.decode(buf),
					UUIDUtil.STREAM_CODEC.decode(buf),
					ByteBufCodecs.STRING_UTF8.decode(buf),
					MEMBER_LIST_CODEC.decode(buf),
					ALLIED_PARTY_LIST_CODEC.decode(buf),
					INCOMING_INVITE_CODEC.decode(buf)
			)
	);

	@Override
	public CustomPacketPayload.Type<PartyStatusS2C> type() {
		return TYPE;
	}

	public record MemberEntry(UUID uuid, String name) {
		public static final StreamCodec<RegistryFriendlyByteBuf, MemberEntry> CODEC = StreamCodec.composite(
				UUIDUtil.STREAM_CODEC, MemberEntry::uuid,
				ByteBufCodecs.STRING_UTF8, MemberEntry::name,
				MemberEntry::new
		);
	}

	public record AlliedPartyEntry(UUID partyId, String name) {
		public static final StreamCodec<RegistryFriendlyByteBuf, AlliedPartyEntry> CODEC = StreamCodec.composite(
				UUIDUtil.STREAM_CODEC, AlliedPartyEntry::partyId,
				ByteBufCodecs.STRING_UTF8, AlliedPartyEntry::name,
				AlliedPartyEntry::new
		);
	}

	public record IncomingPartyInviteEntry(String inviterName, String partyName, int expiresInSeconds) {
		public static final StreamCodec<RegistryFriendlyByteBuf, IncomingPartyInviteEntry> CODEC = StreamCodec.composite(
				ByteBufCodecs.STRING_UTF8, IncomingPartyInviteEntry::inviterName,
				ByteBufCodecs.STRING_UTF8, IncomingPartyInviteEntry::partyName,
				ByteBufCodecs.VAR_INT, IncomingPartyInviteEntry::expiresInSeconds,
				IncomingPartyInviteEntry::new
		);
	}
}
