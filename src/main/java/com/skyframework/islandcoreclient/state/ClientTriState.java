package com.skyframework.islandcoreclient.state;

import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

// Mirrors the server's protection.flag.TriState (ALLOW/DENY/DEFAULT). Shared between
// ClientFlagView and the TriStateRow widget so both flag categories (ROLE_BASED/ISLAND_GLOBAL)
// use one consistent 3-state cycle instead of ToggleRow's plain boolean.
public enum ClientTriState {
	ALLOW(ChatFormatting.GREEN),
	DENY(ChatFormatting.RED),
	DEFAULT(ChatFormatting.GRAY);

	private final ChatFormatting color;

	ClientTriState(ChatFormatting color) {
		this.color = color;
	}

	// Cycle order matches "/island flags set"'s own accepted values (allow/deny/default).
	public ClientTriState next() {
		return switch (this) {
			case ALLOW -> DENY;
			case DENY -> DEFAULT;
			case DEFAULT -> ALLOW;
		};
	}

	public Component label() {
		return Component.translatable("islandcoreclient.tristate." + name().toLowerCase(java.util.Locale.ROOT)).withStyle(color);
	}

	// The wire format sends the server's TriState#name() verbatim (ALLOW/DENY/DEFAULT) — same
	// constant names on both sides, so this is a direct valueOf, not a translation table.
	public static ClientTriState fromWire(String value) {
		return ClientTriState.valueOf(value);
	}
}
