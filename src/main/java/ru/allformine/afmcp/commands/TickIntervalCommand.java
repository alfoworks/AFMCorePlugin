package ru.allformine.afmcp.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.scheduler.Task;
import ru.allformine.afmcp.AFMCorePlugin;

public class TickIntervalCommand extends AFMCPCommand {
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		int interval = args.<Integer>getOne("interval").orElse(0);
		
		if (AFMCorePlugin.lagTask != null) {
			AFMCorePlugin.lagTask.cancel();
		}
		
		AFMCorePlugin.lagTask = Task.builder().intervalTicks(1).execute(() -> {
			try {
				Thread.sleep(interval);
			} catch (InterruptedException ignored) {
			
			}
		}).submit(AFMCorePlugin.instance);
		
		return CommandResult.success();
	}
	
	@Override
	public String getName() {
		return "Fuck";
	}
}
