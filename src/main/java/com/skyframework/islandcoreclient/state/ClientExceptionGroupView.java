package com.skyframework.islandcoreclient.state;

import net.minecraft.network.chat.Component;

import java.util.List;

// Exception groups now resolve per role exactly like ROLE_BASED flags (see the server's
// ExceptionResolver) — mirrors ClientFlagView's shape instead of the old single "enabled" boolean.
public final class ClientExceptionGroupView {
	private final String groupId;
	private final String category;
	private final List<ClientFlagView.RoleValue> resolvedByRole;
	// "nadie"/"miembros"/"aliados"/"todos" if the current role combination exactly matches one of
	// FlagSetPresetC2S's 4 presets, or "custom" otherwise.
	private String currentPreset;
	private final boolean ownerConfigurable;

	public ClientExceptionGroupView(String groupId, String category, List<ClientFlagView.RoleValue> resolvedByRole,
			String currentPreset, boolean ownerConfigurable) {
		this.groupId = groupId;
		this.category = category;
		this.resolvedByRole = resolvedByRole;
		this.currentPreset = currentPreset;
		this.ownerConfigurable = ownerConfigurable;
	}

	public String groupId() {
		return groupId;
	}

	public String category() {
		return category;
	}

	public List<ClientFlagView.RoleValue> resolvedByRole() {
		return resolvedByRole;
	}

	public String currentPreset() {
		return currentPreset;
	}

	public void setCurrentPreset(String currentPreset) {
		this.currentPreset = currentPreset;
	}

	public boolean ownerConfigurable() {
		return ownerConfigurable;
	}

	// Presentation only. Server-defined groups (doors/chests/redstone/animals) get a translated
	// label; anything else (a custom server config) falls back to the raw id.
	public Component label() {
		return labelFor(groupId);
	}

	// Static so DefaultConfigScreen (which only has a groupId + currentPreset from
	// AdminDefaultsStatusS2C, not a full ClientExceptionGroupView) can reuse the exact same labels
	// instead of duplicating this switch.
	public static Component labelFor(String groupId) {
		return switch (groupId) {
			case "doors" -> Component.translatable("islandcoreclient.exceptions.group.doors");
			case "chests" -> Component.translatable("islandcoreclient.exceptions.group.chests");
			case "animals" -> Component.translatable("islandcoreclient.exceptions.group.animals");
			case "crops" -> Component.translatable("islandcoreclient.exceptions.group.crops");
			case "furnaces" -> Component.translatable("islandcoreclient.exceptions.group.furnaces");
			// "redstone"/"mechanisms" removed — retired server-side, both were duplicates covering
			// the same buttons/levers, now split into their own independent groups below.
			case "buttons" -> Component.translatable("islandcoreclient.exceptions.group.buttons");
			case "levers" -> Component.translatable("islandcoreclient.exceptions.group.levers");
			case "bells" -> Component.translatable("islandcoreclient.exceptions.group.bells");
			case "lecterns" -> Component.translatable("islandcoreclient.exceptions.group.lecterns");
			case "beds" -> Component.translatable("islandcoreclient.exceptions.group.beds");
			case "barrels" -> Component.translatable("islandcoreclient.exceptions.group.barrels");
			case "shulker_boxes" -> Component.translatable("islandcoreclient.exceptions.group.shulker_boxes");
			case "hoppers" -> Component.translatable("islandcoreclient.exceptions.group.hoppers");
			case "dispensers_droppers" -> Component.translatable("islandcoreclient.exceptions.group.dispensers_droppers");
			case "crafting_tables" -> Component.translatable("islandcoreclient.exceptions.group.crafting_tables");
			case "anvils" -> Component.translatable("islandcoreclient.exceptions.group.anvils");
			case "enchanting_tables" -> Component.translatable("islandcoreclient.exceptions.group.enchanting_tables");
			case "jukeboxes" -> Component.translatable("islandcoreclient.exceptions.group.jukeboxes");
			case "note_blocks" -> Component.translatable("islandcoreclient.exceptions.group.note_blocks");
			case "cakes" -> Component.translatable("islandcoreclient.exceptions.group.cakes");
			default -> Component.literal(groupId);
		};
	}

	// A one-sentence explanation of what this group actually does — from the lang file, not
	// hardcoded.
	public Component description() {
		return Component.translatable("islandcoreclient.exceptions.group." + groupId + ".description");
	}

}
