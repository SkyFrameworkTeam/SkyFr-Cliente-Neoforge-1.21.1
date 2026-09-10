package com.skyframework.islandcoreclient.state;

import java.util.Locale;

import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public enum ClientResetDimension {
	OVERWORLD(ChatFormatting.GREEN),
	NETHER(ChatFormatting.RED),
	END(ChatFormatting.LIGHT_PURPLE);

	private final ChatFormatting color;

	ClientResetDimension(ChatFormatting color) {
		this.color = color;
	}

	// Centralized here so the list and every modal referencing a dimension use the same color.
	public Component label() {
		return Component.translatable("islandcoreclient.admin.reset_dimension." + name().toLowerCase(Locale.ROOT)).withStyle(color);
	}
}
