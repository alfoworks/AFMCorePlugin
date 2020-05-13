package ru.allformine.afmcp.listeners;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import ru.allformine.afmcp.PluginConfig;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MOTDEventListener {
	
	@Listener
	public void onPing(ClientPingServerEvent event) {
		Text.Builder description = Text.builder();
		List<String> descriptionSplit = Arrays.asList(PluginConfig.motdDescription.split("\\n"));
		
		description.append(Text.builder().append(Text.of(centralizeString(descriptionSplit.get(0)))).color(getRandomColor()).build());
		
		if (descriptionSplit.size() > 1) {
			description.append(Text.of("\n"));
			description.append(Text.builder().append(Text.of(centralizeString(descriptionSplit.get(1)))).color(getRandomColor()).build());
		}
		
		event.getResponse().setDescription(description.build());
	}
	
	private TextColor getRandomColor() {
		TextColor[] colors = new TextColor[]{TextColors.AQUA, TextColors.BLACK, TextColors.BLUE, TextColors.DARK_AQUA, TextColors.DARK_BLUE, TextColors.DARK_GRAY, TextColors.DARK_GREEN, TextColors.DARK_PURPLE, TextColors.DARK_RED, TextColors.GOLD, TextColors.GRAY, TextColors.GREEN, TextColors.LIGHT_PURPLE, TextColors.RED, TextColors.WHITE, TextColors.YELLOW};
		
		return colors[new Random().nextInt(colors.length)];
	}
	
	private String centralizeString(String s) {
		int width = 40;
		
		return String.format("%-" + width + "s", String.format("%" + (s.length() + (width - s.length()) / 2) + "s", s)).replaceAll(" ", "  ");
	}
}
