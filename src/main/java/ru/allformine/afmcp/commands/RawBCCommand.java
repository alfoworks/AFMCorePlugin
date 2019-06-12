package ru.allformine.afmcp.commands;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

public class RawBCCommand extends AFMCPCommand {
    @Override
    public CommandResult execute(CommandSource scr, CommandContext args) {
        for (Player player : Sponge.getServer().getOnlinePlayers()) {
            player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(Text.of(args.<String>getOne("text").get()).toPlain()));
        }

        reply(scr, "Сообщение отправлено.");

        return CommandResult.success();
    }

    @Override
    public String getName() {
        return "RawBC";
    }

    @Override
    public TextColor getColor() {
        return TextColors.RED;
    }
}
