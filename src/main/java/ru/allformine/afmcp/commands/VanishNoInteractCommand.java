package ru.allformine.afmcp.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import ru.allformine.afmcp.vanish.VanishManager;

public class VanishNoInteractCommand extends AFMCPCommand {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(src instanceof Player){
            if(!VanishManager.isVanished((Player) src)){
                throw new CommandException(Text.of("You must be vanished"));
            }
            boolean state = VanishManager.switchCanInteract((Player) src);
            Text.Builder text = Text.builder();
            text.append(Text.of("Now you "));
            if(!state){
                text.append(Text.builder().append(Text.of("can't")).color(TextColors.RED).build());
            }else{
                text.append(Text.of("can"));
            }
            text.append(Text.of(" interact."));
        }else{
            throw new CommandException(Text.of("Invalid command source"));
        }
        return CommandResult.success();
    }

    @Override
    public String getName() {
        return "VanishNoInteract";
    }
}