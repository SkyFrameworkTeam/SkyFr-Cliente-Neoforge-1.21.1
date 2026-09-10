package com.skyframework.islandcoreclient.state;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;

public final class ClientBiomeTierView {
	private final String tierId;
	@Nullable
	private final Component permissionLabel;
	private final boolean unlocked;
	private final List<ClientBiomeView> biomes;

	public ClientBiomeTierView(String tierId, @Nullable Component permissionLabel, boolean unlocked, List<ClientBiomeView> biomes) {
		this.tierId = tierId;
		this.permissionLabel = permissionLabel;
		this.unlocked = unlocked;
		this.biomes = biomes;
	}

	public String tierId() {
		return tierId;
	}

	@Nullable
	public Component permissionLabel() {
		return permissionLabel;
	}

	public boolean unlocked() {
		return unlocked;
	}

	public List<ClientBiomeView> biomes() {
		return biomes;
	}
}
