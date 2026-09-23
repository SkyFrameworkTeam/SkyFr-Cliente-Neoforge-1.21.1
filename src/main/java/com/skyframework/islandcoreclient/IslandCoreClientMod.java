package com.skyframework.islandcoreclient;

import com.skyframework.islandcoreclient.hud.AllyHudRenderer;
import com.skyframework.islandcoreclient.keybind.OpenMenuKeybind;
import com.skyframework.islandcoreclient.network.ClientPacketHandlers;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

// dist = Dist.CLIENT: FML never constructs this class on a dedicated server, matching the Fabric
// project's "environment": "client" fabric.mod.json declaration exactly. Mirrors Fabric's
// IslandCoreClientMod#onInitializeClient() (ClientPacketHandlers.register() / OpenMenuKeybind.register()
// / AllyHudRenderer.register(), in that order) — the only differences are NeoForge's two-bus wiring
// (payload types and the key mapping/GUI layer must register on the MOD event bus, everything else on
// the same bus Fabric's single onInitializeClient() ran on).
@Mod(value = IslandCoreClientMod.MOD_ID, dist = Dist.CLIENT)
public class IslandCoreClientMod {
	public static final String MOD_ID = "islandcoreclient";
	public static final Logger LOGGER = LogUtils.getLogger();

	public IslandCoreClientMod(IEventBus modEventBus, ModContainer modContainer) {
		modEventBus.addListener(ClientPacketHandlers::registerPayloadTypes);
		modEventBus.addListener(OpenMenuKeybind::registerKeyMapping);
		modEventBus.addListener(AllyHudRenderer::registerGuiLayer);

		ClientPacketHandlers.register();
		OpenMenuKeybind.register();
		AllyHudRenderer.register();

		LOGGER.info("IslandCore Client initialized.");
	}
}
