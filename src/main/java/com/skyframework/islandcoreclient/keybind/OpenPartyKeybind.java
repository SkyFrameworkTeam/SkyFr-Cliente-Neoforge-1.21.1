package com.skyframework.islandcoreclient.keybind;

import com.skyframework.islandcoreclient.gui.party.PartyScreen;

import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraft.commands.Commands;

import com.mojang.blaze3d.platform.InputConstants;

import org.lwjgl.glfw.GLFW;

// Mirrors OpenMenuKeybind exactly, but opens PartyScreen directly instead of the Dashboard — a
// party is independent of owning an island, so it gets its own shortcut rather than only being
// reachable through the island menu. Bound to P by default (unlike OpenMenuKeybind, which ships
// unbound): players can still rebind or unbind it in Controls like any other key.
public final class OpenPartyKeybind {
	private static final KeyMapping OPEN_PARTY_KEY = new KeyMapping(
			"key.islandcoreclient.open_party",
			InputConstants.Type.KEYSYM,
			GLFW.GLFW_KEY_P,
			"key.category.islandcoreclient"
	);

	private OpenPartyKeybind() {
	}

	// Registers the key mapping itself: must run on the mod event bus (RegisterKeyMappingsEvent is
	// an IModBusEvent), separate from register() below which wires the tick/command listeners on the
	// game event bus.
	public static void registerKeyMapping(RegisterKeyMappingsEvent event) {
		event.register(OPEN_PARTY_KEY);
	}

	public static void register() {
		NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post event) -> {
			while (OPEN_PARTY_KEY.consumeClick()) {
				openParty(Minecraft.getInstance());
			}
		});

		NeoForge.EVENT_BUS.addListener((RegisterClientCommandsEvent event) ->
				event.getDispatcher().register(Commands.literal("islandparty").executes(context -> {
					Minecraft client = Minecraft.getInstance();
					// Deferred to the next client tick via Minecraft#execute: this command's
					// executes() callback runs SYNCHRONOUSLY inside ChatScreen's own Enter-key
					// handler, which — after dispatching the typed command/message — unconditionally
					// closes the chat screen with its own client.setScreen(null) call. Calling
					// setScreen(new PartyScreen(...)) directly here would open the screen for one
					// frame and then have it immediately wiped out by that follow-up call, which is
					// exactly what "/islandparty no hace nada" looks like from the player's side (see
					// the Bloque D / punto 13 investigation). Scheduling the real setScreen for the
					// next tick lets ChatScreen finish closing itself first, so PartyScreen is what's
					// actually on screen afterward. The P keybind path below isn't affected — it never
					// runs while a chat/text screen has input focus, so nothing schedules a
					// setScreen(null) after it.
					client.execute(() -> openParty(client));
					return 1;
				})));
	}

	private static void openParty(Minecraft client) {
		// Only meaningful once a screen can legitimately be opened at all — same guard implicit in
		// OpenMenuKeybind (consumeClick() only fires during normal gameplay, never while another
		// screen/GUI already has input focus, matching vanilla's own key-binding behavior).
		client.setScreen(new PartyScreen(client.screen));
	}
}
