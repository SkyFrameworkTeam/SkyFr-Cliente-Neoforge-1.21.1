package com.skyframework.islandcoreclient.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.Nullable;

/**
 * Transient error feedback for failed actions, using vanilla's toast system rather than adding
 * a bespoke error-banner widget to every single screen. reasonKey is one of IslandCore's
 * ActionReason constants (translated via "islandcoreclient.reason.&lt;key&gt;") or null for a
 * local timeout (no reply from the server at all).
 */
public final class ClientErrorToasts {
	private ClientErrorToasts() {
	}

	public static void showReason(@Nullable String reasonKey) {
		Component description = reasonKey != null
				? Component.translatable("islandcoreclient.reason." + reasonKey)
				: Component.translatable("islandcoreclient.reason.timeout");
		show(description);
	}

	private static void show(Component description) {
		Minecraft client = Minecraft.getInstance();
		SystemToast.add(client.getToasts(), SystemToast.SystemToastId.PACK_LOAD_FAILURE,
				Component.translatable("islandcoreclient.error.toast_title"), description);
	}
}
