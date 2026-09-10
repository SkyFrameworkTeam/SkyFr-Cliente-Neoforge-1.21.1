package com.skyframework.islandcoreclient.state;

import net.minecraft.network.chat.Component;

public final class ClientBiomeView {
	private final String biomeId;
	private final Component label;

	public ClientBiomeView(String biomeId, Component label) {
		this.biomeId = biomeId;
		this.label = label;
	}

	public String biomeId() {
		return biomeId;
	}

	public Component label() {
		return label;
	}
}
