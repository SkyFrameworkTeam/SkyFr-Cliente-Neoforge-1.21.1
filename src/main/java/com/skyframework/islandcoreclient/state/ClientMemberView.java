package com.skyframework.islandcoreclient.state;

import java.util.UUID;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public final class ClientMemberView {
	public enum Role {
		OWNER(ChatFormatting.GOLD),
		// Formerly TRUSTED — renamed server-side to CO_OWNER (hard-coded always-ALLOW, exactly as
		// strong as OWNER). Wire value must match the server's IslandRole#name() exactly.
		CO_OWNER(ChatFormatting.AQUA),
		MEMBER(ChatFormatting.GREEN),
		ALLY(ChatFormatting.YELLOW);

		private final ChatFormatting color;

		Role(ChatFormatting color) {
			this.color = color;
		}

		// Centralized here so every screen that lists members shows the same role colors. CO_OWNER
		// gets a translated display name ("Copropietario") instead of the raw enum name; the other
		// three keep printing their raw name (pre-existing behavior, unchanged).
		public Component label() {
			MutableComponent text = this == CO_OWNER ? Component.translatable("islandcoreclient.members.role_co_owner") : Component.literal(name());
			return text.withStyle(color);
		}
	}

	private final UUID uuid;
	private final String name;
	private Role role;

	public ClientMemberView(UUID uuid, String name, Role role) {
		this.uuid = uuid;
		this.name = name;
		this.role = role;
	}

	public UUID uuid() {
		return uuid;
	}

	public String name() {
		return name;
	}

	public Role role() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
}
