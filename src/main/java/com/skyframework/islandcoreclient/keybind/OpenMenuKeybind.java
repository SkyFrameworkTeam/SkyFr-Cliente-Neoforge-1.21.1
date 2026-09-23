package com.skyframework.islandcoreclient.keybind;

import com.skyframework.islandcoreclient.gui.island.DashboardScreen;

import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraft.commands.Commands;

import com.mojang.blaze3d.platform.InputConstants;

import org.lwjgl.glfw.GLFW;

public final class OpenMenuKeybind {
	// Unbound by default (GLFW_KEY_UNKNOWN): players opt in via Controls settings.
	private static final KeyMapping OPEN_MENU_KEY = new KeyMapping(
			"key.islandcoreclient.open_menu",
			InputConstants.Type.KEYSYM,
			GLFW.GLFW_KEY_UNKNOWN,
			"key.category.islandcoreclient"
	);

	private OpenMenuKeybind() {
	}

	// Registers the key mapping itself: must run on the mod event bus (RegisterKeyMappingsEvent is
	// an IModBusEvent), separate from register() below which wires the tick/command listeners on the
	// game event bus.
	public static void registerKeyMapping(RegisterKeyMappingsEvent event) {
		event.register(OPEN_MENU_KEY);
	}

	public static void register() {
		NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post event) -> {
			while (OPEN_MENU_KEY.consumeClick()) {
				openMenu(Minecraft.getInstance());
			}
		});

		NeoForge.EVENT_BUS.addListener((RegisterClientCommandsEvent event) ->
				event.getDispatcher().register(Commands.literal("islandmenu").executes(context -> {
					// Deferred to the next tick: this callback runs inside ChatScreen's own Enter-key
					// handler, which closes the chat screen (client.setScreen(null)) right after
					// dispatching, which would otherwise wipe out a setScreen call made synchronously
					// here.
					Minecraft client = Minecraft.getInstance();
					client.execute(() -> openMenu(client));
					return 1;
				})));
	}

	private static void openMenu(Minecraft client) {
		client.setScreen(new DashboardScreen());
	}
}
