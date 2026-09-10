package com.skyframework.islandcoreclient.gui.common;

import java.util.List;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

/**
 * A 4-way selector (Nadie/Miembros/Aliados/Todos) for ROLE_BASED flags, replacing {@link TriStateRow}
 * for those 6 flags — see the server's {@code FlagSetPresetC2S}/{@code FlagsStatusS2C#currentPreset}.
 * Cycles through the 4 presets in order on click (optimistic update, same immediate-then-report
 * shape as {@link TriStateRow}/{@link ToggleRow}), reporting the chosen preset id ("nadie"/
 * "miembros"/"aliados"/"todos") via {@link OnSelect}.
 *
 * <p>A preset of {@code "custom"} (the resolved VISITOR/ALLY/MEMBER combination doesn't
 * match any of the 4 presets — e.g. set up via {@code /island flags set} on individual roles
 * before this UI existed) is shown as "Personalizado"; clicking from custom starts the cycle over
 * at "nadie" instead of trying to guess a "next" preset that doesn't apply to it.
 */
public class FlagPresetRow extends Button {
	@FunctionalInterface
	public interface OnSelect {
		void onSelect(String preset);
	}

	private static final List<String> CYCLE = List.of("nadie", "miembros", "aliados", "todos");

	private final Component label;
	private final OnSelect onSelect;
	private String preset;

	public FlagPresetRow(int x, int y, int width, int height, Component label, String initialPreset, boolean enabled, OnSelect onSelect) {
		super(x, y, width, height, buildMessage(label, initialPreset), button -> {
		}, DEFAULT_NARRATION);
		this.label = label;
		this.preset = initialPreset;
		this.onSelect = onSelect;
		this.active = enabled;
	}

	@Override
	public void onPress() {
		int currentIndex = CYCLE.indexOf(this.preset);
		this.preset = CYCLE.get(currentIndex < 0 ? 0 : (currentIndex + 1) % CYCLE.size());
		this.setMessage(buildMessage(this.label, this.preset));
		this.onSelect.onSelect(this.preset);
	}

	private static Component buildMessage(Component label, String preset) {
		return label.copy().append(": ").append(presetLabel(preset));
	}

	private static Component presetLabel(String preset) {
		return switch (preset) {
			case "nadie" -> Component.translatable("islandcoreclient.flags.preset.nadie");
			case "miembros" -> Component.translatable("islandcoreclient.flags.preset.miembros");
			case "aliados" -> Component.translatable("islandcoreclient.flags.preset.aliados");
			case "todos" -> Component.translatable("islandcoreclient.flags.preset.todos");
			default -> Component.translatable("islandcoreclient.flags.preset.custom");
		};
	}
}
