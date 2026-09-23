package com.skyframework.islandcoreclient.state;

import net.minecraft.network.chat.Component;

import java.util.Locale;

// FARMING removed (Sprint "teletransportes dinámicos"): it's now just another entry in
// ClientIslandCache's dimensionTeleports list, same as every other DIMENSION_REGISTRY dimension —
// see ClientDimensionTeleportView.
public enum ClientTeleportType {
	HOME,
	SPAWN,
	RTP;

	public Component label() {
		return Component.translatable("islandcoreclient.teleports.type." + name().toLowerCase(Locale.ROOT));
	}
}
