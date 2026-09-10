package com.skyframework.islandcoreclient;

import com.skyframework.islandcoreclient.keybind.OpenMenuKeybind;
import com.skyframework.islandcoreclient.keybind.OpenPartyKeybind;
import com.skyframework.islandcoreclient.network.ClientPacketHandlers;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

// dist = Dist.CLIENT: FML never constructs this class on a dedicated server, matching the Fabric
// project's "environment": "client" fabric.mod.json declaration exactly.
@Mod(value = IslandCoreClientMod.MOD_ID, dist = Dist.CLIENT)
public class IslandCoreClientMod {
	public static final String MOD_ID = "islandcoreclient";
	public static final Logger LOGGER = LogUtils.getLogger();

	public IslandCoreClientMod(IEventBus modEventBus, ModContainer modContainer) {
		modEventBus.addListener(ClientPacketHandlers::registerPayloadTypes);
		modEventBus.addListener(OpenMenuKeybind::registerKeyMapping);
		modEventBus.addListener(OpenPartyKeybind::registerKeyMapping);

		ClientPacketHandlers.register();
		OpenMenuKeybind.register();
		OpenPartyKeybind.register();

		LOGGER.info("IslandCore Client (NeoForge port) initialized.");
	}
}
