package com.skyframework.islandcoreclient.state;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public enum ClientDimensionStyle {
	OVERWORLD_LIKE(ChatFormatting.GREEN),
	NETHER_LIKE(ChatFormatting.RED),
	END_LIKE(ChatFormatting.LIGHT_PURPLE),
	// Same 4-color convention ClientResetDimension already uses for Overworld/Nether/End (LIGHT_PURPLE
	// there too) — VOID_FLAT has no vanilla-dimension counterpart there, so BLACK is this enum's own
	// addition, not reused from anywhere else.
	VOID_FLAT(ChatFormatting.BLACK);

	private final ChatFormatting color;

	ClientDimensionStyle(ChatFormatting color) {
		this.color = color;
	}

	public Component label() {
		return Component.translatable("islandcoreclient.admin.dimension_style." + name().toLowerCase(Locale.ROOT));
	}

	// Used by DimensionManagerScreen's own dimension list to color each row's displayName per its
	// style — kept as a plain accessor rather than a "colored label" helper since label() here is
	// this STYLE's own translated name ("Overworld-like", etc.), not a dimension's displayName.
	public ChatFormatting color() {
		return color;
	}
}
