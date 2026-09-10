package com.skyframework.islandcoreclient.gui.common;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

/**
 * A boolean setting row: a button showing "Label: ON/OFF" that flips itself immediately on
 * click (optimistic update) and reports the new value via {@link OnToggle}. Set inactive
 * (via the constructor's {@code enabled} flag) to render read-only, e.g. for non-owners.
 */
public class ToggleRow extends Button {
	@FunctionalInterface
	public interface OnToggle {
		void onToggle(boolean newValue);
	}

	private final Component label;
	private final OnToggle onToggle;
	private boolean value;

	public ToggleRow(int x, int y, int width, int height, Component label, boolean initialValue, boolean enabled, OnToggle onToggle) {
		super(x, y, width, height, buildMessage(label, initialValue), button -> {
		}, DEFAULT_NARRATION);
		this.label = label;
		this.value = initialValue;
		this.onToggle = onToggle;
		this.active = enabled;
	}

	@Override
	public void onPress() {
		this.value = !this.value;
		this.setMessage(buildMessage(this.label, this.value));
		this.onToggle.onToggle(this.value);
	}

	private static Component buildMessage(Component label, boolean value) {
		return label.copy().append(": ").append(CommonComponents.optionStatus(value));
	}
}
