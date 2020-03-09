package ru.allformine.afmcp.commands;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;
import ru.allformine.afmcp.AFMCorePlugin;
import ru.allformine.afmcp.Utils;

import java.util.concurrent.TimeUnit;

public class RestartCommand extends AFMCPCommand {
    @Override
    public CommandResult execute(CommandSource scr, CommandContext args) throws CommandException {
        int minutes = 0;

        if (args.getOne("minutes").isPresent()) {
            minutes = (Integer) args.getOne("minutes").get();

            if (minutes < 1) {
                throw new CommandException(Text.of("Кол-во минут не может быть меньше 1."));
            }
        }

        reply(scr, Text.of("Сервер будет перезапущен."));

        Task.builder().execute(new RestartRunnable(minutes, minutes == 0 ? 10 : 60)).interval(minutes, TimeUnit.MINUTES).submit(AFMCorePlugin.instance);

        return CommandResult.success();
    }

    @Override
    public String getName() {
        return "AFMRestart";
    }

    @Override
    public TextColor getColor() {
        return TextColors.RED;
    }

    private static class RestartRunnable implements Runnable {
        private int minutes;
        private int seconds = 60;

        private RestartRunnable(int minutes, int seconds) {
            this.minutes = minutes;
            this.seconds = seconds;
        }

        private static String pluralize(int number, String nomSing, String genSing, String genPl) {
            String numberString = String.valueOf(number);
            int lastDigit = Integer.parseInt(numberString.substring(numberString.length() - 1));
            int lastTwoDigits = numberString.length() > 1 ? Integer.parseInt(numberString.substring(numberString.length() - 2)) : lastDigit;

            if (lastTwoDigits >= 11 && lastTwoDigits <= 19) {
                return genPl;
            } else if (lastDigit == 1) {
                return nomSing;
            } else if (lastDigit >= 2 && lastDigit <= 4) {
                return genSing;
            } else {
                return genPl;
            }
        }

        @Override
        public void run() {
            if (minutes > 0) {
                sendRestartMessage(String.format("Сервер будет перезапущен через %s %s", minutes, pluralize(minutes, "минуту", "минуты", "минут")));

                minutes--;
            }

            if (minutes == 0) {
                Task.builder().execute(() -> {
                    if (seconds == 30 || (seconds <= 10 && seconds > 0)) {
                        sendRestartMessage(String.format("Сервер будет перезапущен через %s %s", seconds, pluralize(seconds, "секунду", "секунды", "секунд")));
                    } else if (seconds == 0) {
                        sendRestartMessage("Сервер перезапускается!");

                        Utils.afmRestart();
                    }

                    seconds--;
                }).interval(1, TimeUnit.SECONDS).submit(AFMCorePlugin.instance);
            }
        }

        private void sendRestartMessage(String message) {
            Sponge.getServer().getBroadcastChannel().send(TextSerializers.FORMATTING_CODE.deserialize("&7-&2G &cРестарт&f: " + message));
        }
    }
}
