package ru.allformine.afmcp.quests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import ru.allformine.afmcp.AFMCorePlugin;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Грубо говоря здесь будет весь интерфейс редактора.
 * Интерфейс будет выполнен в текстовом виде, гуи писать долго и нудно.
 * От редактора сейчас нужно - создание entity и item квестов
 * <p>
 * При помощи ивент листенеров будем получать цели, а при помощи чата - все остальное
 * С энтити - уебашить кулаком, а с предметом - выкинуть на пол
 **/
public class QuestEditor {

    private final CommandSource source;
    private final Path configDir;
    private final AFMCorePlugin afmcp;

    public static Object buffer;

    // Before submitting this task you should insert Path in buffer
    // Resulting buffer will contain QuestLevel array from file
    // If array.length is 0 - it's empty
    private final Task.Builder updateBufferJsonData = Task.builder().name("Buffer Editor update").execute(task -> {
        if (buffer instanceof Path) {
            try {
                String jsonData = new String(Files.readAllBytes(((Path) buffer)));

                Gson gson = new GsonBuilder()
                        .setPrettyPrinting()
                        .serializeNulls()
                        .registerTypeAdapter(QuestLevel[].class, new QuestLevelDeserializer())
                        .registerTypeAdapter(Quest.class, new QuestDeserializer())
                        .create();

                if (!jsonData.equals("")) {
                    buffer = gson.fromJson(jsonData, QuestLevel[].class);
                } else {
                    buffer = new QuestLevel[0];
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    });

    private Task.Builder registerInterestId2;

    public QuestEditor(CommandSource source, Path configDir, AFMCorePlugin afmcp) {
        this.source = source;
        this.configDir = configDir;
        this.afmcp = afmcp;

        this.registerInterestId2 = Task.builder().delay(100, TimeUnit.MILLISECONDS).name("Register editor interest id 2").execute(task -> Sponge.getEventManager().registerListeners(afmcp, new interestListener(2, null, source, afmcp)));


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

            Sponge.getEventManager().registerListeners(afmcp, new interestListener(1, files, source, afmcp));
        } else {
            reply(source, Text.of(TextColors.YELLOW, "Quest Files directory is empty. Enter a new file name: "));
            Sponge.getEventManager().registerListeners(afmcp, new interestListener(1, null, source, afmcp));
        }


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



    /**
     * <p>
     * Interest ID's used in listeners
     * 1 - File state
     * 2 - Quest Level array state
     * 3 - Quest Level item state
     * 4 - Quest Level rename state
     * 5 - Quest array state
     * 10 - Quest name state
     * 11 - Quest lore state
     * 12 - Quest type state
     * 13 - Quest target state (Entity)
     * 14 - Quest target state (Item)
     * 15 - Quest start message state
     * 16 - Quest final message state
     * 17 - Quest time limit state
     * 18 - Quest overview state
     * </p>
     * For existing quests 18 is first
     * <p>
     * Color Codes for text data: {@link QuestEditorColorCodes}
     * ^%AQUA^
     * ^%BLACK^
     * ^%BLUE^
     * ^%DARK_AQUA^
     * ^%DARK_BLUE^
     * ^%DARK_GRAY^
     * ^%DARK_GREEN^
     * ^%DARK_PURPLE^
     * ^%DARK_RED^
     * ^%GOLD^
     * ^%GREEN^
     * ^%LIGHT_PURPLE^
     * ^%RED^
     * </p>
     **/
    public class interestListener {
        private final int interestId;
        private final File[] files;
        private final CommandSource source;
        private final AFMCorePlugin afmcp;

        public interestListener(int interestId, File[] files, CommandSource source, AFMCorePlugin afmcp) {
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
                                    reply(source, Text.of("File name - ", TextColors.YELLOW, files[index].getName()));
                                    secondPart(files[index].toPath());
                                } catch (NumberFormatException e) {
                                    secondThroughCreation(input);
                                }
                            }

                            event.setCancelled(true);
                            Sponge.getEventManager().unregisterListeners(this);
                            break;
                        case 2:
                            QuestLevel[] array = (QuestLevel[]) buffer;

                            Optional<QuestLevel> optLevel = Arrays.stream(array)
                                    .filter(l -> l.getLevelId().equals(input)).findFirst();

                            QuestLevel level = null;

                            if (optLevel.isPresent()) {
                                // Existing
                                reply(source, Text.of(TextColors.GREEN, "Opening questLevel - " + input));
                                reply(source, Text.of("To exit write - ", TextColors.RED, "EXIT"));
                                level = optLevel.get();
                            }

                            break;
                    }
                }
            }
        }


        private void secondThroughCreation(String input) {
            try {
                reply(source, Text.of(TextColors.YELLOW, "Creating file with name - " + input));
                secondPart(Files.createFile(configDir.resolve("questFiles").resolve(input)));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        private void secondPart(Path path) {
            buffer = path;
            updateBufferJsonData.submit(afmcp);

            if (buffer instanceof QuestLevel[]) {
                if (((QuestLevel[]) buffer).length > 0) {
                    for (QuestLevel level : (QuestLevel[]) buffer) {
                        reply(source, Text.of(
                                TextColors.GREEN, level.getLevelId() + ". ",
                                TextColors.GRAY, "Quests: " + level.getQuests().length));

                        reply(source, Text.of(TextColors.YELLOW, "Write questLevelId to start editing level"));
                    }
                }
                reply(source, Text.of(TextColors.YELLOW, "To create a new level write a new quest Id:"));

                Sponge.getEventManager().unregisterListeners(this);
                registerInterestId2.submit(afmcp);
            } else {
                reply(source, Text.of(TextColors.RED, "Something went wrong while fetching json data"));
            }
        }
    }
}
