package ru.allformine.afmcp.quests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;
import ru.allformine.afmcp.AFMCorePlugin;

import com.google.inject.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 Грубо говоря здесь будет весь интерфейс редактора.
 Интерфейс будет выполнен в текстовом виде, гуи писать долго и нудно.
 От редактора сейчас нужно - создание entity и item квестов

 При помощи ивент листенеров будем получать цели, а при помощи чата - все остальное
 С энтити - уебашить кулаком, а с предметом - выкинуть на пол
 **/
public class QuestEditor {

    private final CommandSource source;
    private final Path configDir;
    private final AFMCorePlugin afmcp;

    public static Object buffer;

    public QuestEditor(CommandSource source, Path configDir, AFMCorePlugin afmcp) {
        this.source = source;
        this.configDir = configDir;
        this.afmcp = afmcp;
        reply(source, Text.of(TextColors.GREEN, "Finished initialization of Quest Editor"));

        File[] files = fetchDirectory();
        if (files.length > 0) {
            reply(source, Text.of(TextColors.YELLOW, "Select a file by index or enter a new one's name:"));
            for (int i = 0; i < files.length; i++) {
                if (AFMCorePlugin.getConfig().getNode("quests").getNode("questsFile").getString().equals(files[i].getName()))
                    reply(source, Text.of(i + ". ", TextColors.GREEN, files[i].getName(), " < Selected"));
                else
                    reply(source, Text.of(i + ". ", files[i].getName()));
            }

            Sponge.getEventManager().registerListeners(afmcp, new chatInterestListener(1, files, source, afmcp));
        } else {
            reply(source, Text.of(TextColors.YELLOW, "Quest Files directory is empty. Enter a new file name: "));
            Sponge.getEventManager().registerListeners(afmcp, new chatInterestListener(1, null, source, afmcp));
        }


        Task.builder().async().interval(100, TimeUnit.MILLISECONDS).execute(task -> {
            if (buffer instanceof Path) {
                Path path = (Path) buffer;
                Gson gsonDeserializer = new GsonBuilder()
                        .setPrettyPrinting()
                        .serializeNulls()
                        .registerTypeAdapter(QuestLevel.class, new QuestLevelDeserializer())
                        .registerTypeAdapter(Quest.class, new QuestDeserializer())
                        .create();

                buffer = null;
            }
        }).submit(afmcp);
    }

    private File[] fetchDirectory() {
        reply(source, Text.of("Begin fetching config directory"));
        File[] files = configDir.resolve("questFiles").toFile().listFiles();
        assert files != null;
        return files;
    }

    public static void reply(CommandSource source, Text text) {
        source.sendMessage(Text.builder(" [").color(TextColors.GRAY).append(
                Text.builder("Quest Editor").color(TextColors.YELLOW).append(
                        Text.builder("] ").color(TextColors.GRAY).append(text).build()).build()).build());
    }


    public class chatInterestListener {
        private final int interestId;
        private final File[] files;
        private final CommandSource source;
        private final AFMCorePlugin afmcp;

        public chatInterestListener(int interestId, File[] files, CommandSource source, AFMCorePlugin afmcp) {
            this.interestId = interestId;
            this.files = files;
            this.source = source;
            this.afmcp = afmcp;
        }

        @Listener
        public void onChat(MessageChannelEvent.Chat event) {
            if (!AFMCorePlugin.questToggle) {
                if (event.getContext().get(EventContextKeys.OWNER).get().getCommandSource().get().equals(source)) {
                    String input = event.getMessage().toText().toPlain().split(" ")[1];
                    switch (interestId) {
                        case 1:
                            if (files == null) {
                                secondThroughCreation(input);
                            } else {
                                try {
                                    int index = Integer.parseInt(input);
                                    reply(source, Text.of("Opening file with index - " + input));
                                    reply(source, Text.of("File name - ", TextColors.YELLOW, files[index].getName() ));
                                } catch (NumberFormatException e) {
                                    secondThroughCreation(input);
                                }
                            }

                            event.setCancelled(true);
                            Sponge.getEventManager().unregisterListeners(this);
                            break;
                    }
                }
            }
        }

        private void secondThroughCreation(String input) {
            try {
                reply(source, Text.of(TextColors.YELLOW, "Creating file with name - " + input));
                buffer = Files.createFile(configDir.resolve("questFiles").resolve(input));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
