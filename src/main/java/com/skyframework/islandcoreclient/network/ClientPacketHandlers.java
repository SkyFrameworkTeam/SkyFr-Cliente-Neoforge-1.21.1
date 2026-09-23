package com.skyframework.islandcoreclient.network;

import com.skyframework.islandcoreclient.gui.admin.AdminIslandDetailScreen;
import com.skyframework.islandcoreclient.gui.admin.AdminIslandListScreen;
import com.skyframework.islandcoreclient.gui.admin.AdminIslandMembersScreen;
import com.skyframework.islandcoreclient.gui.admin.DefaultConfigScreen;
import com.skyframework.islandcoreclient.gui.admin.DimensionManagerScreen;
import com.skyframework.islandcoreclient.gui.admin.SpawnAuthorizedPlayersScreen;
import com.skyframework.islandcoreclient.gui.admin.SpawnFlagsScreen;
import com.skyframework.islandcoreclient.gui.admin.SpawnManagerScreen;
import com.skyframework.islandcoreclient.gui.admin.VanillaResetScreen;
import com.skyframework.islandcoreclient.gui.island.BiomeScreen;
import com.skyframework.islandcoreclient.gui.island.SettingsScreen;
import com.skyframework.islandcoreclient.gui.island.TeleportsScreen;
import com.skyframework.islandcoreclient.gui.party.PartyMembersScreen;
import com.skyframework.islandcoreclient.gui.party.PartyScreen;
import com.skyframework.islandcoreclient.network.alliance.AllyLocationsS2C;
import com.skyframework.islandcoreclient.network.alliance.LocationSharingSetC2S;
import com.skyframework.islandcoreclient.network.alliance.LocationSharingStatusRequestC2S;
import com.skyframework.islandcoreclient.network.alliance.LocationSharingStatusS2C;
import com.skyframework.islandcoreclient.network.admin.dimension.DimensionCreateC2S;
import com.skyframework.islandcoreclient.network.admin.dimension.DimensionDeleteC2S;
import com.skyframework.islandcoreclient.network.admin.dimension.DimensionDeleteConfirmC2S;
import com.skyframework.islandcoreclient.network.admin.dimension.DimensionDetailRequestC2S;
import com.skyframework.islandcoreclient.network.admin.dimension.DimensionDetailS2C;
import com.skyframework.islandcoreclient.network.admin.dimension.DimensionListRequestC2S;
import com.skyframework.islandcoreclient.network.admin.dimension.DimensionListS2C;
import com.skyframework.islandcoreclient.network.admin.dimension.DimensionRegenerateC2S;
import com.skyframework.islandcoreclient.network.admin.dimension.DimensionRegenerateConfirmC2S;
import com.skyframework.islandcoreclient.network.admin.island.AdminIslandDeleteC2S;
import com.skyframework.islandcoreclient.network.admin.island.AdminIslandDeleteConfirmC2S;
import com.skyframework.islandcoreclient.network.admin.island.AdminIslandDetailRequestC2S;
import com.skyframework.islandcoreclient.network.admin.island.AdminIslandDetailS2C;
import com.skyframework.islandcoreclient.network.admin.island.AdminIslandListRequestC2S;
import com.skyframework.islandcoreclient.network.admin.island.AdminIslandListS2C;
import com.skyframework.islandcoreclient.network.admin.spawn.SpawnAuthorizedPlayerAddC2S;
import com.skyframework.islandcoreclient.network.admin.spawn.SpawnAuthorizedPlayerRemoveC2S;
import com.skyframework.islandcoreclient.network.admin.spawn.SpawnBuildProtectionSetC2S;
import com.skyframework.islandcoreclient.network.admin.spawn.SpawnBuildProtectionStatusRequestC2S;
import com.skyframework.islandcoreclient.network.admin.spawn.SpawnBuildProtectionStatusS2C;
import com.skyframework.islandcoreclient.network.admin.spawn.SpawnExceptionGroupSetPresetC2S;
import com.skyframework.islandcoreclient.network.admin.spawn.SpawnExceptionGroupsStatusRequestC2S;
import com.skyframework.islandcoreclient.network.admin.spawn.SpawnExceptionGroupsStatusS2C;
import com.skyframework.islandcoreclient.network.admin.spawn.SpawnFlagSetC2S;
import com.skyframework.islandcoreclient.network.admin.spawn.SpawnFlagSetPresetC2S;
import com.skyframework.islandcoreclient.network.admin.spawn.SpawnFlagsStatusRequestC2S;
import com.skyframework.islandcoreclient.network.admin.spawn.SpawnFlagsStatusS2C;
import com.skyframework.islandcoreclient.network.admin.spawn.SpawnIslandCreateC2S;
import com.skyframework.islandcoreclient.network.admin.spawn.SpawnIslandResizeC2S;
import com.skyframework.islandcoreclient.network.admin.spawn.SpawnIslandSetHomeC2S;
import com.skyframework.islandcoreclient.network.admin.defaults.AdminDefaultsStatusRequestC2S;
import com.skyframework.islandcoreclient.network.admin.defaults.AdminDefaultsStatusS2C;
import com.skyframework.islandcoreclient.network.admin.defaults.AdminExceptionSetServerDefaultC2S;
import com.skyframework.islandcoreclient.network.admin.defaults.AdminFlagSetRequirementC2S;
import com.skyframework.islandcoreclient.network.admin.defaults.AdminFlagSetServerDefaultC2S;
import com.skyframework.islandcoreclient.network.admin.defaults.AdminGlobalFlagSetServerDefaultC2S;
import com.skyframework.islandcoreclient.network.admin.spawn.SpawnStatusRequestC2S;
import com.skyframework.islandcoreclient.network.admin.spawn.SpawnStatusS2C;
import com.skyframework.islandcoreclient.network.admin.vanilla.VanillaResetCancelC2S;
import com.skyframework.islandcoreclient.network.admin.vanilla.VanillaResetConfirmC2S;
import com.skyframework.islandcoreclient.network.admin.vanilla.VanillaResetListRequestC2S;
import com.skyframework.islandcoreclient.network.admin.vanilla.VanillaResetListS2C;
import com.skyframework.islandcoreclient.network.admin.vanilla.VanillaResetQueueC2S;
import com.skyframework.islandcoreclient.network.biome.BiomeTiersRequestC2S;
import com.skyframework.islandcoreclient.network.biome.BiomeTiersS2C;
import com.skyframework.islandcoreclient.network.flag.ExceptionGroupSetPresetC2S;
import com.skyframework.islandcoreclient.network.flag.ExceptionGroupsStatusRequestC2S;
import com.skyframework.islandcoreclient.network.flag.ExceptionGroupsStatusS2C;
import com.skyframework.islandcoreclient.network.flag.FlagSetC2S;
import com.skyframework.islandcoreclient.network.flag.FlagSetPresetC2S;
import com.skyframework.islandcoreclient.network.flag.FlagsStatusRequestC2S;
import com.skyframework.islandcoreclient.network.flag.FlagsStatusS2C;
import com.skyframework.islandcoreclient.network.handshake.ClientHandshakeC2S;
import com.skyframework.islandcoreclient.network.handshake.ServerHandshakeS2C;
import com.skyframework.islandcoreclient.network.island.IslandBiomeChangeC2S;
import com.skyframework.islandcoreclient.network.island.IslandCreateC2S;
import com.skyframework.islandcoreclient.network.island.IslandDeleteConfirmC2S;
import com.skyframework.islandcoreclient.network.island.IslandDeleteRequestC2S;
import com.skyframework.islandcoreclient.network.island.IslandSettingsUpdateC2S;
import com.skyframework.islandcoreclient.network.island.IslandSnapshotRequestC2S;
import com.skyframework.islandcoreclient.network.island.IslandSnapshotS2C;
import com.skyframework.islandcoreclient.network.island.IslandUpgradeC2S;
import com.skyframework.islandcoreclient.network.member.MemberAllyAddC2S;
import com.skyframework.islandcoreclient.network.member.MemberAllyRemoveC2S;
import com.skyframework.islandcoreclient.network.member.MemberInviteAcceptC2S;
import com.skyframework.islandcoreclient.network.member.MemberInviteC2S;
import com.skyframework.islandcoreclient.network.member.MemberInviteDeclineC2S;
import com.skyframework.islandcoreclient.network.member.MemberRemoveC2S;
import com.skyframework.islandcoreclient.network.member.MemberTrustC2S;
import com.skyframework.islandcoreclient.network.party.PartyAcceptC2S;
import com.skyframework.islandcoreclient.network.party.PartyCreateC2S;
import com.skyframework.islandcoreclient.network.party.PartyDisbandConfirmC2S;
import com.skyframework.islandcoreclient.network.party.PartyDisbandRequestC2S;
import com.skyframework.islandcoreclient.network.party.PartyInviteC2S;
import com.skyframework.islandcoreclient.network.party.PartyKickC2S;
import com.skyframework.islandcoreclient.network.party.PartyLeaveC2S;
import com.skyframework.islandcoreclient.network.party.PartyRenameC2S;
import com.skyframework.islandcoreclient.network.party.PartyStatusRequestC2S;
import com.skyframework.islandcoreclient.network.party.PartyStatusS2C;
import com.skyframework.islandcoreclient.network.teleport.TeleportRequestC2S;
import com.skyframework.islandcoreclient.network.teleport.TeleportStatusRequestC2S;
import com.skyframework.islandcoreclient.network.teleport.TeleportStatusS2C;
import com.skyframework.islandcoreclient.state.ClientAdminDefaultsCache;
import com.skyframework.islandcoreclient.state.ClientAllyLocationView;
import com.skyframework.islandcoreclient.state.ClientAllyLocationsCache;
import com.skyframework.islandcoreclient.state.ClientConnectionState;
import com.skyframework.islandcoreclient.state.ClientIslandCache;
import com.skyframework.islandcoreclient.state.ClientLocationSharingCache;
import com.skyframework.islandcoreclient.state.ClientPartyCache;
import com.skyframework.islandcoreclient.state.ClientSpawnFlagsCache;

import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import java.util.ArrayList;
import java.util.List;

// NeoForge's networking model merges "register the wire type" and "register the receiver" into a
// single registrar.playToServer/playToClient call (Fabric's PayloadTypeRegistry.register +
// ServerPlayNetworking.registerGlobalReceiver split), and requires BOTH C2S and S2C types to be
// registered on each side (the client here registers playToServer for every payload it only ever
// SENDS too, with a no-op handler, since it never receives its own sends) — otherwise the
// client/server channel negotiation doesn't recognize the payload at all. See registerPayloadTypes.
public final class ClientPacketHandlers {
	private ClientPacketHandlers() {
	}

	// Registers every payload's wire type + receiver in one place. Must run on the MOD event bus
	// (RegisterPayloadHandlersEvent is an IModBusEvent) — called from IslandCoreClientMod's
	// constructor via modEventBus.addListener(ClientPacketHandlers::registerPayloadTypes).
	public static void registerPayloadTypes(RegisterPayloadHandlersEvent event) {
		// .optional(): this project deliberately splits into two independently-installed mods
		// sharing the same "islandcore" channel namespace — the client-side "islandcoreclient" mod
		// and the server-side "islandcore" companion mod (different modids). Without this,
		// NeoForge's own per-modid channel presence check refuses the connection outright when this
		// client (which has no mod literally called "islandcore") connects to the server, even
		// though islandcoreclient IS that channel's real client-side counterpart. Real
		// protocol-compatibility checking is already handled by our own handshake
		// (ClientHandshakeC2S/ServerHandshakeS2C), so relaxing NeoForge's own presence check here
		// doesn't weaken anything. Must match the server's own ServerPacketHandlers#register.
		PayloadRegistrar registrar = event.registrar("1").optional();

		sendOnly(registrar, ClientHandshakeC2S.TYPE, ClientHandshakeC2S.CODEC);
		receive(registrar, ServerHandshakeS2C.TYPE, ServerHandshakeS2C.CODEC, payload -> {
			ClientConnectionState.onServerHandshakeReceived(payload.protocolVersion(), payload.protocolCompatible(), payload.isOperator());
			if (payload.protocolCompatible()) {
				// The handshake just confirmed this server speaks a compatible IslandCore protocol:
				// fetch the player's own island (or the empty snapshot) right away so the Dashboard
				// has real data as soon as it's first opened, instead of waiting for a manual refresh.
				PacketDistributor.sendToServer(new IslandSnapshotRequestC2S());
			}
			// else: protocolCompatible=false — DashboardScreen shows a mismatch message and renders
			// nothing else, so there's no screen that would need this data anyway; requesting it
			// would just be a packet built against a wire format the server may not actually emit.
		});

		sendOnly(registrar, IslandSnapshotRequestC2S.TYPE, IslandSnapshotRequestC2S.CODEC);
		receive(registrar, IslandSnapshotS2C.TYPE, IslandSnapshotS2C.CODEC, ClientIslandCache::applySnapshot);

		receive(registrar, ActionResultS2C.TYPE, ActionResultS2C.CODEC, PendingActionTracker::onActionResult);

		sendOnly(registrar, IslandCreateC2S.TYPE, IslandCreateC2S.CODEC);
		sendOnly(registrar, IslandUpgradeC2S.TYPE, IslandUpgradeC2S.CODEC);
		sendOnly(registrar, IslandDeleteRequestC2S.TYPE, IslandDeleteRequestC2S.CODEC);
		sendOnly(registrar, IslandDeleteConfirmC2S.TYPE, IslandDeleteConfirmC2S.CODEC);
		sendOnly(registrar, IslandSettingsUpdateC2S.TYPE, IslandSettingsUpdateC2S.CODEC);
		sendOnly(registrar, IslandBiomeChangeC2S.TYPE, IslandBiomeChangeC2S.CODEC);

		sendOnly(registrar, MemberInviteC2S.TYPE, MemberInviteC2S.CODEC);
		sendOnly(registrar, MemberInviteAcceptC2S.TYPE, MemberInviteAcceptC2S.CODEC);
		sendOnly(registrar, MemberInviteDeclineC2S.TYPE, MemberInviteDeclineC2S.CODEC);
		sendOnly(registrar, MemberTrustC2S.TYPE, MemberTrustC2S.CODEC);
		sendOnly(registrar, MemberRemoveC2S.TYPE, MemberRemoveC2S.CODEC);
		sendOnly(registrar, MemberAllyAddC2S.TYPE, MemberAllyAddC2S.CODEC);
		sendOnly(registrar, MemberAllyRemoveC2S.TYPE, MemberAllyRemoveC2S.CODEC);

		sendOnly(registrar, TeleportRequestC2S.TYPE, TeleportRequestC2S.CODEC);
		sendOnly(registrar, TeleportStatusRequestC2S.TYPE, TeleportStatusRequestC2S.CODEC);
		receive(registrar, TeleportStatusS2C.TYPE, TeleportStatusS2C.CODEC, payload -> {
			ClientIslandCache.applyTeleportStatus(payload);
			// TeleportsScreen.initContent() sends the request but builds its buttons
			// synchronously from whatever was already cached — on the very first visit this
			// session that's the disabled-by-default placeholder, since this reply hasn't
			// landed yet. Rebuild the screen once real data arrives so those buttons don't
			// stay stuck inactive until the player leaves and reopens the screen.
			if (Minecraft.getInstance().screen instanceof TeleportsScreen screen) {
				screen.refreshFromNetwork();
			}
		});

		sendOnly(registrar, BiomeTiersRequestC2S.TYPE, BiomeTiersRequestC2S.CODEC);
		receive(registrar, BiomeTiersS2C.TYPE, BiomeTiersS2C.CODEC, payload -> {
			ClientIslandCache.applyBiomeTiers(payload);
			// Same race as TeleportStatusS2C/TeleportsScreen above: rebuild the screen once real
			// tier data arrives so it doesn't stay blank until the player leaves and reopens it.
			if (Minecraft.getInstance().screen instanceof BiomeScreen screen) {
				screen.refreshFromNetwork();
			}
		});

		sendOnly(registrar, FlagsStatusRequestC2S.TYPE, FlagsStatusRequestC2S.CODEC);
		receive(registrar, FlagsStatusS2C.TYPE, FlagsStatusS2C.CODEC, payload -> {
			ClientIslandCache.applyFlagsStatus(payload);
			if (Minecraft.getInstance().screen instanceof SettingsScreen screen) {
				screen.refreshFromNetwork();
			}
		});
		sendOnly(registrar, FlagSetC2S.TYPE, FlagSetC2S.CODEC);
		sendOnly(registrar, FlagSetPresetC2S.TYPE, FlagSetPresetC2S.CODEC);
		sendOnly(registrar, ExceptionGroupsStatusRequestC2S.TYPE, ExceptionGroupsStatusRequestC2S.CODEC);
		receive(registrar, ExceptionGroupsStatusS2C.TYPE, ExceptionGroupsStatusS2C.CODEC, payload -> {
			ClientIslandCache.applyExceptionGroupsStatus(payload);
			if (Minecraft.getInstance().screen instanceof SettingsScreen screen) {
				screen.refreshFromNetwork();
			}
		});
		sendOnly(registrar, ExceptionGroupSetPresetC2S.TYPE, ExceptionGroupSetPresetC2S.CODEC);

		sendOnly(registrar, AdminDefaultsStatusRequestC2S.TYPE, AdminDefaultsStatusRequestC2S.CODEC);
		receive(registrar, AdminDefaultsStatusS2C.TYPE, AdminDefaultsStatusS2C.CODEC, payload -> {
			ClientAdminDefaultsCache.applyStatus(payload);
			if (Minecraft.getInstance().screen instanceof DefaultConfigScreen screen) {
				screen.refreshFromNetwork();
			}
		});
		sendOnly(registrar, AdminFlagSetServerDefaultC2S.TYPE, AdminFlagSetServerDefaultC2S.CODEC);
		sendOnly(registrar, AdminGlobalFlagSetServerDefaultC2S.TYPE, AdminGlobalFlagSetServerDefaultC2S.CODEC);
		sendOnly(registrar, AdminExceptionSetServerDefaultC2S.TYPE, AdminExceptionSetServerDefaultC2S.CODEC);
		sendOnly(registrar, AdminFlagSetRequirementC2S.TYPE, AdminFlagSetRequirementC2S.CODEC);

		sendOnly(registrar, PartyStatusRequestC2S.TYPE, PartyStatusRequestC2S.CODEC);
		receive(registrar, PartyStatusS2C.TYPE, PartyStatusS2C.CODEC, payload -> {
			ClientPartyCache.applyStatus(payload);
			if (Minecraft.getInstance().screen instanceof PartyScreen screen) {
				screen.refreshFromNetwork();
			} else if (Minecraft.getInstance().screen instanceof PartyMembersScreen screen) {
				screen.refreshFromNetwork();
			}
		});
		sendOnly(registrar, PartyCreateC2S.TYPE, PartyCreateC2S.CODEC);
		sendOnly(registrar, PartyInviteC2S.TYPE, PartyInviteC2S.CODEC);
		sendOnly(registrar, PartyAcceptC2S.TYPE, PartyAcceptC2S.CODEC);
		sendOnly(registrar, PartyLeaveC2S.TYPE, PartyLeaveC2S.CODEC);
		sendOnly(registrar, PartyKickC2S.TYPE, PartyKickC2S.CODEC);
		sendOnly(registrar, PartyRenameC2S.TYPE, PartyRenameC2S.CODEC);
		sendOnly(registrar, PartyDisbandRequestC2S.TYPE, PartyDisbandRequestC2S.CODEC);
		sendOnly(registrar, PartyDisbandConfirmC2S.TYPE, PartyDisbandConfirmC2S.CODEC);

		registerAdminHandlers(registrar);
		registerLocationSharingHandlers(registrar);
	}

	// Admin network block: each S2C handler applies the real data to ClientIslandCache, then
	// rebuilds the currently open screen if it's the one waiting on that exact reply — same
	// TeleportStatusS2C/BiomeTiersS2C race-avoidance pattern used above, applied to all 5 new
	// screens from the start instead of shipping them with the same blank-on-first-visit bug.
	private static void registerAdminHandlers(PayloadRegistrar registrar) {
		sendOnly(registrar, AdminIslandListRequestC2S.TYPE, AdminIslandListRequestC2S.CODEC);
		receive(registrar, AdminIslandListS2C.TYPE, AdminIslandListS2C.CODEC, payload -> {
			ClientIslandCache.applyAdminIslandList(payload);
			if (Minecraft.getInstance().screen instanceof AdminIslandListScreen screen) {
				screen.refreshFromNetwork();
			}
		});

		sendOnly(registrar, AdminIslandDetailRequestC2S.TYPE, AdminIslandDetailRequestC2S.CODEC);
		receive(registrar, AdminIslandDetailS2C.TYPE, AdminIslandDetailS2C.CODEC, payload -> {
			ClientIslandCache.applyAdminIslandDetail(payload);
			Screen currentScreen = Minecraft.getInstance().screen;
			if (currentScreen instanceof AdminIslandDetailScreen screen) {
				screen.refreshFromNetwork();
			} else if (currentScreen instanceof AdminIslandMembersScreen screen) {
				screen.refreshFromNetwork();
			}
		});
		sendOnly(registrar, AdminIslandDeleteC2S.TYPE, AdminIslandDeleteC2S.CODEC);
		sendOnly(registrar, AdminIslandDeleteConfirmC2S.TYPE, AdminIslandDeleteConfirmC2S.CODEC);

		sendOnly(registrar, SpawnStatusRequestC2S.TYPE, SpawnStatusRequestC2S.CODEC);
		receive(registrar, SpawnStatusS2C.TYPE, SpawnStatusS2C.CODEC, payload -> {
			ClientIslandCache.applySpawnStatus(payload);
			if (Minecraft.getInstance().screen instanceof SpawnManagerScreen screen) {
				screen.refreshFromNetwork();
			}
		});
		sendOnly(registrar, SpawnIslandCreateC2S.TYPE, SpawnIslandCreateC2S.CODEC);
		sendOnly(registrar, SpawnIslandResizeC2S.TYPE, SpawnIslandResizeC2S.CODEC);
		sendOnly(registrar, SpawnIslandSetHomeC2S.TYPE, SpawnIslandSetHomeC2S.CODEC);

		sendOnly(registrar, SpawnBuildProtectionStatusRequestC2S.TYPE, SpawnBuildProtectionStatusRequestC2S.CODEC);
		receive(registrar, SpawnBuildProtectionStatusS2C.TYPE, SpawnBuildProtectionStatusS2C.CODEC, payload -> {
			ClientIslandCache.applySpawnBuildProtectionStatus(payload);
			Screen currentScreen = Minecraft.getInstance().screen;
			if (currentScreen instanceof SpawnManagerScreen screen) {
				screen.refreshFromNetwork();
			} else if (currentScreen instanceof SpawnAuthorizedPlayersScreen screen) {
				screen.refreshFromNetwork();
			}
		});
		sendOnly(registrar, SpawnBuildProtectionSetC2S.TYPE, SpawnBuildProtectionSetC2S.CODEC);
		sendOnly(registrar, SpawnAuthorizedPlayerAddC2S.TYPE, SpawnAuthorizedPlayerAddC2S.CODEC);
		sendOnly(registrar, SpawnAuthorizedPlayerRemoveC2S.TYPE, SpawnAuthorizedPlayerRemoveC2S.CODEC);

		sendOnly(registrar, SpawnFlagsStatusRequestC2S.TYPE, SpawnFlagsStatusRequestC2S.CODEC);
		receive(registrar, SpawnFlagsStatusS2C.TYPE, SpawnFlagsStatusS2C.CODEC, payload -> {
			ClientSpawnFlagsCache.applyFlagsStatus(payload);
			if (Minecraft.getInstance().screen instanceof SpawnFlagsScreen screen) {
				screen.refreshFromNetwork();
			}
		});
		sendOnly(registrar, SpawnExceptionGroupsStatusRequestC2S.TYPE, SpawnExceptionGroupsStatusRequestC2S.CODEC);
		receive(registrar, SpawnExceptionGroupsStatusS2C.TYPE, SpawnExceptionGroupsStatusS2C.CODEC, payload -> {
			ClientSpawnFlagsCache.applyExceptionGroupsStatus(payload);
			if (Minecraft.getInstance().screen instanceof SpawnFlagsScreen screen) {
				screen.refreshFromNetwork();
			}
		});
		sendOnly(registrar, SpawnFlagSetC2S.TYPE, SpawnFlagSetC2S.CODEC);
		sendOnly(registrar, SpawnFlagSetPresetC2S.TYPE, SpawnFlagSetPresetC2S.CODEC);
		sendOnly(registrar, SpawnExceptionGroupSetPresetC2S.TYPE, SpawnExceptionGroupSetPresetC2S.CODEC);

		sendOnly(registrar, DimensionListRequestC2S.TYPE, DimensionListRequestC2S.CODEC);
		receive(registrar, DimensionListS2C.TYPE, DimensionListS2C.CODEC, payload -> {
			ClientIslandCache.applyDimensionList(payload);
			if (Minecraft.getInstance().screen instanceof DimensionManagerScreen screen) {
				screen.refreshFromNetwork();
			}
		});
		sendOnly(registrar, DimensionDetailRequestC2S.TYPE, DimensionDetailRequestC2S.CODEC);
		receive(registrar, DimensionDetailS2C.TYPE, DimensionDetailS2C.CODEC, payload -> {
			ClientIslandCache.applyDimensionDetail(payload);
			if (Minecraft.getInstance().screen instanceof DimensionManagerScreen screen) {
				screen.refreshFromNetwork();
			}
		});
		sendOnly(registrar, DimensionCreateC2S.TYPE, DimensionCreateC2S.CODEC);
		sendOnly(registrar, DimensionDeleteC2S.TYPE, DimensionDeleteC2S.CODEC);
		sendOnly(registrar, DimensionDeleteConfirmC2S.TYPE, DimensionDeleteConfirmC2S.CODEC);
		sendOnly(registrar, DimensionRegenerateC2S.TYPE, DimensionRegenerateC2S.CODEC);
		sendOnly(registrar, DimensionRegenerateConfirmC2S.TYPE, DimensionRegenerateConfirmC2S.CODEC);

		sendOnly(registrar, VanillaResetListRequestC2S.TYPE, VanillaResetListRequestC2S.CODEC);
		receive(registrar, VanillaResetListS2C.TYPE, VanillaResetListS2C.CODEC, payload -> {
			ClientIslandCache.applyVanillaResetList(payload);
			if (Minecraft.getInstance().screen instanceof VanillaResetScreen screen) {
				screen.refreshFromNetwork();
			}
		});
		sendOnly(registrar, VanillaResetQueueC2S.TYPE, VanillaResetQueueC2S.CODEC);
		sendOnly(registrar, VanillaResetConfirmC2S.TYPE, VanillaResetConfirmC2S.CODEC);
		sendOnly(registrar, VanillaResetCancelC2S.TYPE, VanillaResetCancelC2S.CODEC);
	}

	// AllyLocationsS2C is a periodic push with no requesting screen to rebuild — it only ever feeds
	// AllyHudRenderer, which reads ClientAllyLocationsCache fresh every frame on its own. Renamed
	// from registerAllianceHandlers: the island-to-island alliance status handler that used to live
	// here is gone (AllianceScreen/ClientAllianceCache retired) — location sharing now refreshes
	// PartyScreen, which hosts the toggles, instead of the old AllianceScreen.
	private static void registerLocationSharingHandlers(PayloadRegistrar registrar) {
		sendOnly(registrar, LocationSharingStatusRequestC2S.TYPE, LocationSharingStatusRequestC2S.CODEC);
		receive(registrar, LocationSharingStatusS2C.TYPE, LocationSharingStatusS2C.CODEC, payload -> {
			ClientLocationSharingCache.applyStatus(payload);
			if (Minecraft.getInstance().screen instanceof PartyScreen screen) {
				screen.refreshFromNetwork();
			}
		});
		sendOnly(registrar, LocationSharingSetC2S.TYPE, LocationSharingSetC2S.CODEC);
		receive(registrar, AllyLocationsS2C.TYPE, AllyLocationsS2C.CODEC, payload -> {
			List<ClientAllyLocationView> views = new ArrayList<>();
			for (AllyLocationsS2C.Entry entry : payload.entries()) {
				views.add(new ClientAllyLocationView(entry.uuid(), entry.name(), entry.x(), entry.y(), entry.z()));
			}
			ClientAllyLocationsCache.applyLocations(views);
		});
	}

	// Registers a payload this client only ever SENDS (server-bound): a no-op handler, since this
	// client never receives its own C2S sends back — the registration is still required so the
	// channel negotiation recognizes the payload and PacketDistributor.sendToServer(...) doesn't
	// fail at runtime.
	private static <T extends CustomPacketPayload> void sendOnly(
			PayloadRegistrar registrar, CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
		registrar.playToServer(type, codec, (payload, context) -> { });
	}

	// Registers a payload this client receives (server-bound to client): the handler is scheduled
	// via IPayloadContext#enqueueWork so it runs on the main render/game thread, not the network
	// thread — every handler body here touches client-side game state (ClientIslandCache, open
	// Screens) that's only safe to mutate from there, mirroring the same threading guarantee
	// Fabric's ClientPlayNetworking receivers already provided.
	private static <T extends CustomPacketPayload> void receive(
			PayloadRegistrar registrar, CustomPacketPayload.Type<T> type,
			StreamCodec<? super RegistryFriendlyByteBuf, T> codec, ClientHandler<T> handler) {
		registrar.playToClient(type, codec, (payload, context) -> context.enqueueWork(() -> handler.handle(payload)));
	}

	@FunctionalInterface
	private interface ClientHandler<T> {
		void handle(T payload);
	}

	// Wires the game-bus listeners (join/disconnect/tick) — separate from registerPayloadTypes
	// above (mod bus) since RegisterPayloadHandlersEvent and these game-bus events fire on
	// different buses. Called directly from IslandCoreClientMod's constructor.
	public static void register() {
		// The server may not implement this protocol at all (e.g. IslandCore hasn't shipped its
		// networking yet): sending is safe regardless, the packet is simply dropped if unhandled.
		NeoForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingIn event) -> {
			ClientConnectionState.onHandshakeSent();
			PacketDistributor.sendToServer(new ClientHandshakeC2S(ClientHandshakeC2S.CURRENT_PROTOCOL_VERSION));
		});

		NeoForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingOut event) -> {
			ClientConnectionState.reset();
			PendingActionTracker.reset();
		});

		NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post event) -> {
			ClientConnectionState.tickTimeout();
			PendingActionTracker.tick();
		});
	}
}
