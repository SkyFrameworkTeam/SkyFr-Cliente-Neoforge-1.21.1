package com.skyframework.islandcoreclient.state;

import java.util.Locale;

import net.minecraft.network.chat.Component;

public enum ClientTeleportType {
	HOME,
	SPAWN,
	RTP,
	FARMING;

	public Component label() {
		return Component.translatable("islandcoreclient.teleports.type." + name().toLowerCase(Locale.ROOT));
	}
}
