package com.skyframework.islandcoreclient.state;

import java.util.Locale;

import net.minecraft.network.chat.Component;

public enum ClientDimensionStyle {
	OVERWORLD_LIKE,
	NETHER_LIKE,
	END_LIKE,
	VOID_FLAT;

	public Component label() {
		return Component.translatable("islandcoreclient.admin.dimension_style." + name().toLowerCase(Locale.ROOT));
	}
}
