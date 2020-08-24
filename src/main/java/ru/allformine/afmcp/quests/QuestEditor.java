package ru.allformine.afmcp.quests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import ru.allformine.afmcp.AFMCorePlugin;
import ru.allformine.afmcp.quests.parsers.*;

import java.io.File;
import java.io.IOException;
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

    public static QuestLevel[] questLevels;
    public static Object buffer;
    private static Object questBuffer;
    private static Path workingPath;

    private final Task.Builder registerInterestId2;
    private final Task.Builder registerInterestId3;
    private final Task.Builder registerInterestId5;
    private final Task.Builder registerInterestId6;
    private final Task.Builder registerInterestId10;
    private final Task.Builder registerInterestId11;
    private final Task.Builder registerInterestId12;
    private final Task.Builder registerInterestId13;
    private final Task.Builder registerInterestId14;
    private final Task.Builder registerInterestId15;
    private final Task.Builder registerInterestId16;
    private final Task.Builder registerInterestId17;


    /*
    private final Task.Builder registerInterestId4;
    */

    public QuestEditor(CommandSource source, Path configDir, AFMCorePlugin afmcp) {
        this.source = source;
        this.configDir = configDir;
        this.afmcp = afmcp;

        this.registerInterestId2 = Task.builder().delay(100, TimeUnit.MILLISECONDS).name("Register editor interest id 2").execute(task -> Sponge.getEventManager().registerListeners(afmcp, new interestListener(2, null, source, afmcp)));
        this.registerInterestId3 = Task.builder().delay(100, TimeUnit.MILLISECONDS).name("Register editor interest id 3").execute(task -> Sponge.getEventManager().registerListeners(afmcp, new interestListener(3, null, source, afmcp)));
        this.registerInterestId5 = Task.builder().delay(100, TimeUnit.MILLISECONDS).name("Register editor interest id 5").execute(task -> Sponge.getEventManager().registerListeners(afmcp, new interestListener(5, null, source, afmcp)));
        this.registerInterestId6 = Task.builder().delay(100, TimeUnit.MILLISECONDS).name("Register editor interest id 6").execute(task -> Sponge.getEventManager().registerListeners(afmcp, new interestListener(6, null, source, afmcp)));
        this.registerInterestId10 = Task.builder().delay(100, TimeUnit.MILLISECONDS).name("Register editor interest id 10").execute(task -> Sponge.getEventManager().registerListeners(afmcp, new interestListener(10, null, source, afmcp)));
        this.registerInterestId11 = Task.builder().delay(100, TimeUnit.MILLISECONDS).name("Register editor interest id 11").execute(task -> Sponge.getEventManager().registerListeners(afmcp, new interestListener(11, null, source, afmcp)));
        this.registerInterestId12 = Task.builder().delay(100, TimeUnit.MILLISECONDS).name("Register editor interest id 12").execute(task -> Sponge.getEventManager().registerListeners(afmcp, new interestListener(12, null, source, afmcp)));
        this.registerInterestId13 = Task.builder().delay(100, TimeUnit.MILLISECONDS).name("Register editor interest id 13").execute(task -> Sponge.getEventManager().registerListeners(afmcp, new interestListener(13, null, source, afmcp)));
        this.registerInterestId14 = Task.builder().delay(100, TimeUnit.MILLISECONDS).name("Register editor interest id 14").execute(task -> Sponge.getEventManager().registerListeners(afmcp, new interestListener(14, null, source, afmcp)));
        this.registerInterestId15 = Task.builder().delay(100, TimeUnit.MILLISECONDS).name("Register editor interest id 15").execute(task -> Sponge.getEventManager().registerListeners(afmcp, new interestListener(15, null, source, afmcp)));
        this.registerInterestId16 = Task.builder().delay(100, TimeUnit.MILLISECONDS).name("Register editor interest id 16").execute(task -> Sponge.getEventManager().registerListeners(afmcp, new interestListener(16, null, source, afmcp)));
        this.registerInterestId17 = Task.builder().delay(100, TimeUnit.MILLISECONDS).name("Register editor interest id 17").execute(task -> Sponge.getEventManager().registerListeners(afmcp, new interestListener(17, null, source, afmcp)));

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
     * 5 - Quest Level overview state
     * 6 - Quest overview state
     * 10 - Quest rename state (After release)
     * 11 - Quest lore state
     * 12 - Quest type state
     * 13 - Quest target state (Entity)
     * 14 - Quest target state (Item)
     * 15 - Quest count state
     * 16 - Quest start message state
     * 17 - Quest final message state
     * 18 - Quest time limit state
     * </p>
     * For existing quests 5 is first
     * <p>
     * Color Codes for text data: {@link QuestEditorColorCodes}
     * 'AQUA'
     * 'BLACK'
     * 'BLUE'
     * 'DARK_AQUA'
     * 'DARK_BLUE'
     * 'DARK_GRAY'
     * 'DARK_GREEN'
     * 'DARK_PURPLE'
     * 'DARK_RED'
     * 'GOLD'
     * 'GREEN'
     * 'LIGHT_PURPLE'
     * 'RED'
     * 'WHITE'
     * 'RESET'
     * 'GRAY'
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
                    String input = event.getMessage().toText().toPlain().split("> ")[1];
                    switch (interestId) {
                        // File state
                        case 1:
                            if (files == null) {
                                secondThroughCreation(input);
                            } else {
                                try {
                                    int index = Integer.parseInt(input);
                                    reply(source, Text.of("Opening file with index - " + input));
                                    reply(source, Text.of("File name - ", TextColors.GREEN, files[index].getName()));
                                    secondPart(files[index].toPath());
                                } catch (NumberFormatException e) {
                                    secondThroughCreation(input);
                                }
                            }

                            event.setCancelled(true);
                            Sponge.getEventManager().unregisterListeners(this);
                            break;

                        // Quest level array state
                        case 2:
                            QuestLevel[] array = ((QuestLevelContainer) buffer).getQuestLevels();
                            questLevels = array;
                            Text text2 = parseText(input);

                            Optional<QuestLevel> optLevel = Arrays.stream(array)
                                    .filter(l -> l.getLevelId().toPlain().equals(text2.toPlain())).findFirst();

                            if (optLevel.isPresent()) {
                                // Existing
                                outputQuestLevel(optLevel.get());
                                buffer = optLevel.get();
                                registerInterestId5.submit(afmcp);
                            } else {
                                // New
                                buffer = text2; // Quest Level id
                                reply(source, Text.of(TextColors.YELLOW, "Creating questLevel - ", text2));
                                reply(source, Text.of("Drop a desired item to set it as quest level's item"));
                                registerInterestId3.submit(afmcp);
                            }

                            event.setCancelled(true);
                            Sponge.getEventManager().unregisterListeners(this);
                            break;

                        // Quest level state
                        case 5:
                            QuestLevel level5 = (QuestLevel) buffer;

                            // Index check
                            try {
                                //// TODO: NullPointerException
                                Quest quest = level5.getQuest(Integer.parseInt(input));
                                questBuffer = quest;
                                showQuest(quest, "");
                            } catch (NumberFormatException e) {
                                if (input.toUpperCase().equals("EXIT")) {
                                    Gson gson = new GsonBuilder()
                                            .setPrettyPrinting()
                                            .serializeNulls()
                                            .registerTypeAdapter(QuestLevelContainer.class, new QuestLevelContainerSerializer())
                                            .registerTypeAdapter(QuestLevel.class, new QuestLevelSerializer())
                                            .registerTypeAdapter(Quest.class, new QuestSerializer())
                                            .create();


                                    // Overwriting
                                    if (Arrays.stream(questLevels)
                                            .anyMatch(questLevel -> questLevel.getLevelId().equals(
                                                            ((QuestLevel) buffer).getLevelId()))) {
                                        for (int i = 0; i < questLevels.length; i++) {
                                            if (((QuestLevel) buffer).getLevelId().equals(questLevels[i].getLevelId())) {
                                                questLevels[i] = (QuestLevel) buffer;
                                            }
                                        }
                                    } else {
                                        // Appending
                                        questLevels = Arrays.copyOf(questLevels, questLevels.length+1);
                                        questLevels[questLevels.length-1] = (QuestLevel) buffer;
                                    }

                                    String data = gson.toJson(new QuestLevelContainer(questLevels));
                                    try {
                                        Files.write(workingPath, data.getBytes());
                                    } catch (IOException ioException) {
                                        ioException.printStackTrace();
                                    }
                                    reply(source, Text.of(TextColors.GREEN, "Saved changes in level into quests file"));

                                    secondPart(workingPath);
                                } else {
                                    // Quest name
                                    Quest quest = level5.getQuest(input);
                                    questBuffer = quest;
                                    showQuest(quest, input);
                                }
                            }

                            event.setCancelled(true);
                            Sponge.getEventManager().unregisterListeners(this);
                            break;

                        case 6:
                            if (questBuffer instanceof Quest) {
                                switch (input.trim().toUpperCase()) {
                                    case "EDIT":
                                        reply(source, Text.of("Input new quest name ", TextColors.WHITE, "(Color support)", TextColors.GRAY, " or write ", TextColors.WHITE, "'SKIP'", TextColors.GRAY, " to proceed to other state"));

                                        Sponge.getEventManager().unregisterListeners(this);
                                        registerInterestId10.submit(afmcp);
                                        break;
                                    case "EXIT":
                                        QuestLevel questLevel = (QuestLevel) buffer;
                                        Quest[] quests = questLevel.getQuests();
                                        boolean create = true;
                                        int index = 0;
                                        if (quests != null) {
                                            for (int i = 0; i < quests.length; i++) {
                                                if (quests[i].getName() == ((Quest) questBuffer).getName()) {
                                                    create = false;
                                                    index = i;
                                                    break;
                                                }
                                            }
                                        }

                                        if (create) {
                                            try {
                                                quests = Arrays.copyOf(quests, quests.length+1);
                                            } catch (NullPointerException e) {
                                                quests = new Quest[1];
                                            }

                                            quests[quests.length-1] = (Quest) questBuffer;
                                        } else {
                                            quests[index] = (Quest) questBuffer;
                                        }


                                        buffer = new QuestLevel(quests, questLevel.getLevelId(), questLevel.getItemTypeId());

                                        event.setCancelled(true);
                                        outputQuestLevel((QuestLevel) buffer);
                                        registerInterestId5.submit(afmcp);

                                        Sponge.getEventManager().unregisterListeners(this);
                                        break;
                                    case "DELETE":
                                        QuestLevel questLevel1 = (QuestLevel) buffer;
                                        Quest[] quests1 = questLevel1.getQuests();
                                        String name = ((Quest) questBuffer).getName().toString();
                                        if (questLevel1.getQuest(name) != null) {
                                            int index1 = 0;
                                            for (int i = 0; i < quests1.length; i++) {
                                                if (index1 == 0 && quests1.length - 1 == 0) {
                                                    quests1 = new Quest[0];
                                                } else if (name.equals(quests1[i].getName().toString())) {
                                                    index = i;
                                                } else if (i == quests1.length - 1) {
                                                    quests1[index1] = quests1[i];
                                                    quests1 = Arrays.copyOf(quests1, quests1.length-1);
                                                }
                                            }

                                            buffer = new QuestLevel(quests1, questLevel1.getLevelId(), questLevel1.getItemTypeId());

                                            event.setCancelled(true);
                                            outputQuestLevel((QuestLevel) buffer);
                                            registerInterestId5.submit(afmcp);

                                            Sponge.getEventManager().unregisterListeners(this);
                                        }
                                        break;
                                    default:
                                        reply(source, Text.of(TextColors.RED, "Wrong input!"));
                                        showQuest((Quest) questBuffer, "");
                                        break;
                                }
                            } else {
                                transportingError(6);
                            }

                            event.setCancelled(true);
                            Sponge.getEventManager().unregisterListeners(this);
                            break;

                        // Rename state
                        case 10:
                            if (input.toUpperCase().equals("SKIP")) {
                                reply(source, Text.of(TextColors.DARK_GRAY, "Skipping name part..."));
                            } else {
                                Quest quest = (Quest) questBuffer;
                                Text text = parseText(input);
                                reply(source, Text.of(TextColors.GREEN, "New name ", TextColors.GRAY, " - ", text));
                                questBuffer = new Quest(text,
                                        quest.getType(), quest.getTarget(), quest.getStartMessage(), quest.getFinalMessage(),
                                        quest.getFinalMessage(), quest.getCount(), null);
                            }

                            reply(source, Text.of("Enter quest lore ", TextColors.WHITE ,"(Color support)"));
                            registerInterestId11.submit(afmcp);
                            event.setCancelled(true);
                            Sponge.getEventManager().unregisterListeners(this);
                            break;

                        // Lore state
                        case 11:
                            if (questBuffer instanceof Quest) {
                                if (input.toUpperCase().equals("SKIP")) {
                                    if (((Quest) questBuffer).getLore() != null) {
                                        reply(source, Text.of(TextColors.DARK_GRAY, "Skipping lore part..."));
                                        reply(source, Text.of("Write type of target (Entity / Item): "));
                                        registerInterestId12.submit(afmcp);
                                    } else {
                                        reply(source, Text.of(TextColors.DARK_RED, "Can't skip! Not assigned"));
                                    }
                                } else {
                                    if (!input.trim().equals("")) {
                                        Text text = parseText(input.trim());
                                        Quest quest = (Quest) questBuffer;
                                        questBuffer = new Quest(quest.getName(),
                                                quest.getType(), quest.getTarget(), quest.getStartMessage(), quest.getFinalMessage(),
                                                text, quest.getCount(), null);
                                        reply(source, Text.of(TextColors.YELLOW, "Updating Lore to ", TextColors.GRAY, "- ", text));
                                        reply(source, Text.of("Write type of target (Entity / Item): "));

                                        event.setCancelled(true);
                                        Sponge.getEventManager().unregisterListeners(this);
                                        registerInterestId12.submit(afmcp);
                                    }
                                }
                            } else {
                                transportingError(11);
                            }
                            break;

                        case 12:
                            if (questBuffer instanceof Quest) {
                                if (input.toUpperCase().equals("SKIP")) {
                                    if (((Quest) questBuffer).getType() != null) {
                                        reply(source, Text.of(TextColors.DARK_GRAY, "Skipping type part..."));
                                        if (((Quest) questBuffer).getType().equals("item")) {
                                            reply(source, Text.of("Drop a desired item to the ground to set it to the quest:"));
                                            event.setCancelled(true);
                                            Sponge.getEventManager().unregisterListeners(this);
                                            registerInterestId14.submit(afmcp);
                                        } else {
                                            reply(source, Text.of("Hit a desired entity to set it to the quest:"));
                                            event.setCancelled(true);
                                            Sponge.getEventManager().unregisterListeners(this);
                                            registerInterestId13.submit(afmcp);
                                        }
                                    } else {
                                        reply(source, Text.of(TextColors.DARK_RED, "Can't skip! Not assigned"));
                                    }
                                } else {
                                    if (input.toLowerCase().charAt(0) == 'i') {
                                        reply(source, Text.of("Drop a desired item to the ground to set it to the quest:"));
                                        event.setCancelled(true);
                                        Sponge.getEventManager().unregisterListeners(this);
                                        registerInterestId14.submit(afmcp);
                                    } else if (input.toLowerCase().charAt(0) == 'e') {
                                        reply(source, Text.of("Hit a desired entity to set it to the quest:"));
                                        event.setCancelled(true);
                                        Sponge.getEventManager().unregisterListeners(this);
                                        registerInterestId13.submit(afmcp);
                                    } else {
                                        reply(source, Text.of(TextColors.RED, "Wrong input!"));
                                        event.setCancelled(true);
                                    }
                                }
                            } else {
                                transportingError(12);
                                event.setCancelled(true);
                                Sponge.getEventManager().unregisterListeners(this);
                            }

                            break;

                        // Only SKIP variants are here of 13 and 14
                        case 13:
                            if (questBuffer instanceof Quest) {
                                if (input.toUpperCase().equals("SKIP")) {
                                    if (((Quest) questBuffer).getTarget() != null) {
                                        reply(source, Text.of(TextColors.DARK_GRAY, "Skipping target part..."));
                                        reply(source, Text.of("Insert amount of ", TextColors.WHITE, ((Quest) questBuffer).getTarget(), TextColors.GRAY," to be killed:"));
                                        event.setCancelled(true);
                                        Sponge.getEventManager().unregisterListeners(this);
                                        registerInterestId15.submit(afmcp);
                                    } else {
                                        reply(source, Text.of(TextColors.DARK_RED, "Can't skip! Not assigned"));
                                    }
                                }
                            } else {
                                transportingError(13);
                                event.setCancelled(true);
                                Sponge.getEventManager().unregisterListeners(this);
                            }
                            break;

                        case 14:
                            if (questBuffer instanceof Quest) {
                                if (input.toUpperCase().equals("SKIP")) {
                                    if (((Quest) questBuffer).getTarget() != null) {
                                        reply(source, Text.of(TextColors.DARK_GRAY, "Skipping target part..."));
                                        reply(source, Text.of("Insert amount of desired ", TextColors.WHITE,  ((Quest) questBuffer).getTarget(), TextColors.GRAY, " to be gathered:"));
                                        event.setCancelled(true);
                                        Sponge.getEventManager().unregisterListeners(this);
                                        registerInterestId15.submit(afmcp);
                                    } else {
                                        reply(source, Text.of(TextColors.DARK_RED, "Can't skip! Not assigned"));
                                    }
                                }
                            } else {
                                transportingError(14);
                                event.setCancelled(true);
                                Sponge.getEventManager().unregisterListeners(this);
                            }
                            break;

                        case 15:
                            if (questBuffer instanceof Quest) {
                                if (input.toUpperCase().equals("SKIP")) {
                                    if (((Quest) questBuffer).getCount() != 0) {
                                        reply(source, Text.of(TextColors.DARK_GRAY, "Skipping count part..."));
                                        reply(source, Text.of("Enter Start Message for quest ", TextColors.WHITE, "(Color Support)"));
                                        event.setCancelled(true);
                                        Sponge.getEventManager().unregisterListeners(this);
                                        registerInterestId16.submit(afmcp);
                                    } else {
                                        reply(source, Text.of(TextColors.DARK_RED, "Can't skip! Not assigned"));
                                    }
                                } else {
                                    try {
                                        int count = Integer.parseInt(input.trim());

                                        Quest quest = (Quest) questBuffer;
                                        questBuffer = new Quest(quest.getName(), quest.getType(),
                                                quest.getTarget(), quest.getStartMessage(),
                                                quest.getFinalMessage(), quest.getLore(), count, null);
                                        reply(source, Text.of(TextColors.GREEN, "Setting count to", TextColors.GRAY, " - ", TextColors.WHITE, count));
                                        reply(source, Text.of("Enter Start Message for quest ", TextColors.WHITE, "(Color Support)"));

                                        event.setCancelled(true);
                                        Sponge.getEventManager().unregisterListeners(this);
                                        registerInterestId16.submit(afmcp);
                                    } catch (NumberFormatException ignore) {
                                        event.setCancelled(true);
                                        reply(source, Text.of(TextColors.RED, "You have to write number! Retry"));
                                    }
                                }
                            } else {
                                transportingError(15);

                                event.setCancelled(true);
                                Sponge.getEventManager().unregisterListeners(this);
                            }
                            break;

                        case 16:
                            if (questBuffer instanceof Quest) {
                                if (input.toUpperCase().equals("SKIP")) {
                                    if (((Quest) questBuffer).getStartMessage() != null) {
                                        reply(source, Text.of(TextColors.DARK_GRAY, "Skipping start message part..."));
                                        reply(source, Text.of("Enter Final Message for quest ", TextColors.WHITE, "(Color Support)"));
                                        event.setCancelled(true);
                                        Sponge.getEventManager().unregisterListeners(this);
                                        registerInterestId17.submit(afmcp);
                                    } else {
                                        reply(source, Text.of(TextColors.DARK_RED, "Can't skip! Not assigned"));
                                    }
                                } else {
                                    Quest quest = (Quest) questBuffer;
                                    Text text = (input.trim().equals("")) ? quest.getLore() : parseText(input);

                                    questBuffer = new Quest(quest.getName(), quest.getType(),
                                            quest.getTarget(), text,
                                            quest.getFinalMessage(), quest.getLore(), quest.getCount(), null);

                                    reply(source, Text.of(TextColors.GREEN, "Setting start message to", TextColors.GRAY, " - ", text));
                                    reply(source, Text.of("Enter Final Message for quest ", TextColors.WHITE, "(Color Support)"));
                                    event.setCancelled(true);
                                    Sponge.getEventManager().unregisterListeners(this);
                                    registerInterestId17.submit(afmcp);
                                }
                            } else {
                                transportingError(16);
                                event.setCancelled(true);
                                Sponge.getEventManager().unregisterListeners(this);
                            }
                            break;

                        case 17:
                            if (questBuffer instanceof Quest) {
                                if (input.toUpperCase().equals("SKIP")) {
                                    if (((Quest) questBuffer).getStartMessage() != null) {
                                        reply(source, Text.of(TextColors.DARK_GRAY, "Skipping final message part..."));
                                        reply(source, Text.of("Enter Time Delay for quest ", TextColors.WHITE, "(minutes)"));
                                        event.setCancelled(true);
                                        Sponge.getEventManager().unregisterListeners(this);
                                        showQuest((Quest) questBuffer, "");
                                    } else {
                                        reply(source, Text.of(TextColors.DARK_RED, "Can't skip! Not assigned"));
                                    }
                                } else {
                                    Quest quest = (Quest) questBuffer;
                                    Text text = (input.trim().equals(""))
                                            ? Text.of(TextColors.GREEN, "Congratulations on finishing ", quest.getName())
                                            : parseText(input);

                                    questBuffer = new Quest(quest.getName(), quest.getType(),
                                            quest.getTarget(), quest.getStartMessage(),
                                            text, quest.getLore(), quest.getCount(), null);

                                    reply(source, Text.of(TextColors.GREEN, "Setting final message to", TextColors.GRAY, " - ", text));
                                    reply(source, Text.of("Enter Time Delay for quest ", TextColors.WHITE, "(minutes)"));
                                    event.setCancelled(true);
                                    Sponge.getEventManager().unregisterListeners(this);
                                    showQuest((Quest) questBuffer, "");
                                }
                            } else {
                                transportingError(17);
                                event.setCancelled(true);
                                Sponge.getEventManager().unregisterListeners(this);
                            }
                            break;
                    }
                }
            }
        }

        @Listener
        public void onItemDrop(DropItemEvent.Pre event) {
            // Quest Level item state
            if (event.getSource() instanceof Player) {
                if (interestId == 3) {
                    if (buffer instanceof Text) {
                        ItemType type = event.getOriginalDroppedItems().get(0).getType();
                        reply(source, Text.of(TextColors.YELLOW, "Setting Level Item Type to - "
                                + type.getTranslation().toString()));
                        buffer = new QuestLevel(null,
                                Text.of(buffer), // Quest id that we got from the last event
                                type.getId());
                    } else if (buffer instanceof QuestLevel) {
                        Text id = ((QuestLevel) buffer).getLevelId();
                        Quest[] quests = ((QuestLevel) buffer).getQuests();
                        ItemType type = event.getOriginalDroppedItems().get(0).getType();
                        reply(source, Text.of(TextColors.YELLOW, "Updating Level Item Type to - "
                                + type.getTranslation().toString()));
                        buffer = new QuestLevel(quests, id, type.getId());
                    } else {
                        throw new AssertionError("Buffer wrong contains. Buffer: " + buffer);
                    }

                    event.setCancelled(true);
                    outputQuestLevel((QuestLevel) buffer);
                    registerInterestId5.submit(afmcp);

                    Sponge.getEventManager().unregisterListeners(this);

                // Quest item state
                } else if (interestId == 14) {
                    if (questBuffer instanceof Quest) {
                        Quest quest = (Quest) questBuffer;
                        String id = event.getOriginalDroppedItems().get(0).getType().getId();
                        questBuffer = new Quest(quest.getName(), "item",
                                id, quest.getStartMessage(),
                                quest.getFinalMessage(), quest.getLore(), quest.getCount(), null);

                        reply(source, Text.of("Insert amount of desired ", TextColors.WHITE,  id, TextColors.GRAY, " to be gathered:"));
                        registerInterestId15.submit(afmcp);
                    } else {
                        transportingError(14);
                    }

                    event.setCancelled(true);
                    Sponge.getEventManager().unregisterListeners(this);
                }
            }
        }

        @Listener
        public void onPlayerJoin(ClientConnectionEvent.Join event) {
            if (event.getTargetEntity().getCommandSource().orElse(null) != source) {
                event.getTargetEntity().kick(Text.of(TextColors.RED, "Технические работы"));
            }
        }

        @Listener
        public void onEntityHit(DamageEntityEvent event) {
            // Entity quest state
            if (interestId == 13) {
                if (questBuffer instanceof Quest) {
                    Quest quest = (Quest) questBuffer;
                    String id = event.getTargetEntity().getType().getId();
                    questBuffer = new Quest(quest.getName(), "entity",
                            id, quest.getStartMessage(),
                            quest.getFinalMessage(), quest.getLore(), quest.getCount(), null);

                    reply(source, Text.of("Insert amount of ", TextColors.WHITE, id, TextColors.GRAY," to be killed:"));
                    registerInterestId15.submit(afmcp);
                } else {
                    transportingError(13);
                }

                event.setCancelled(true);
                Sponge.getEventManager().unregisterListeners(this);
            }
        }

        // reply(source, Text.of());
        private void showQuest(Quest quest, String name) {
            if (quest != null) {
                reply(source, Text.of(TextColors.GREEN, "Opening quest ", TextColors.GRAY, "- ", TextColors.WHITE, quest.getName()));
                reply(source, Text.of("Lore - ", TextColors.WHITE, quest.getLore()));
                reply(source, Text.of("Type - ", TextColors.WHITE, quest.getType()));
                reply(source, Text.of("Target - ", TextColors.WHITE, quest.getTarget()));
                reply(source, Text.of("Count - ", TextColors.WHITE, quest.getCount()));
                reply(source, Text.of("SF Messages: "));
                reply(source, quest.getStartMessage());
                reply(source, quest.getFinalMessage());
                reply(source, Text.of("-----"));
                reply(source, Text.of("Write - ", TextColors.GREEN, "EDIT", TextColors.GRAY, " to begin editing cycle ", TextColors.GOLD, "(WIP)"));
                reply(source, Text.of("Write - ", TextColors.DARK_RED, "DELETE", TextColors.GRAY, " to delete quest"));
                reply(source, Text.of("Write - ", TextColors.RED, "EXIT", TextColors.GRAY, " to go back to level overview"));

                registerInterestId6.submit(afmcp);
            } else {
                if (!name.trim().equals("")) {
                    Text text = parseText(name.trim());
                    reply(source, Text.of(TextColors.GREEN, "Creating quest ", TextColors.GRAY, "- ", TextColors.WHITE, text));
                    questBuffer = new Quest(text,
                            null, null, null, null,
                            null, 0, null);
                    reply(source, Text.of("Enter quest lore ", TextColors.WHITE ,"(Color support)"));
                    registerInterestId11.submit(afmcp);
                } else {
                    reply(source, Text.of(TextColors.RED, "Wrong input. Try again"));

                    outputQuestLevel((QuestLevel) buffer);
                    registerInterestId5.submit(afmcp);

                    Sponge.getEventManager().unregisterListeners(this);
                }
            }
        }

        private Text parseText(String string) {
            String[] parts = string.split("'");
            Text.Builder builder = Text.builder();

            TextColor globalColor = TextColors.WHITE;
            for (int i = 0; i < parts.length; i++) {
                if (i % 2 != 0) { // Odd is color code
                    TextColor color = QuestEditorColorCodes.values.get(parts[i].toUpperCase());
                    if (color != null) {
                        globalColor = color;
                    }
                } else {
                    builder.append(Text.of(globalColor, parts[i]));
                }
            }

            return builder.toText();
        }

        private void outputQuestLevel(QuestLevel level) {
            reply(source, Text.of(TextColors.GREEN, "Opening questLevel ", TextColors.GRAY, "- ", TextColors.WHITE, level.getLevelId()));
            Quest[] quests = level.getQuests();
            reply(source, Text.of(TextColors.GREEN, "Item Type ", TextColors.GRAY, "- ", TextColors.WHITE, level.getItemTypeId()));
            int length = (quests == null) ? 0 : quests.length;
            reply(source, Text.of(TextColors.GREEN, "Quest Count ", TextColors.GRAY, "- ", TextColors.WHITE, length));
            if (length > 0) {
                reply(source, Text.of("-----"));
                for (int i = 0; i < length; i++) {
                    reply(source, Text.of(
                            TextColors.WHITE, i + ". ", quests[i].getName(), ": ",
                            quests[i].getLore(), " - ",
                            TextColors.YELLOW, quests[i].getTarget(), TextColors.WHITE, "x",
                            TextColors.AQUA, quests[i].getCount()));
                }
                reply(source, Text.of("-----"));
            }
            reply(source, Text.of("Write - ", TextColors.AQUA, "QUEST INDEX", TextColors.GRAY, " or ", TextColors.GREEN, "QUEST NAME", TextColors.GRAY, " to edit quest"));
            reply(source, Text.of("Write - ", TextColors.GOLD, "NEW ", TextColors.GREEN, "QUEST NAME", TextColors.GRAY, " to create new quest ", TextColors.WHITE ,"(Color support)" ));
            reply(source, Text.of("Write - ", TextColors.RED, "EXIT", TextColors.GRAY, " to move back to file view"));
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
            workingPath = path;

            updateBufferJsonData();
            QuestLevelContainer container = (QuestLevelContainer) buffer;
            if (container.getQuestLevels().length > 0) {
                for (QuestLevel level : container.getQuestLevels()) {
                    reply(source, Text.of(
                            TextColors.GREEN, level.getLevelId(), ". ",
                            TextColors.GRAY, "Quests: " + level.getQuests().length));
                }

                reply(source, Text.of(TextColors.YELLOW, "Write questLevelId to start editing level"));
            }
            reply(source, Text.of(TextColors.YELLOW, "To create a new level write a new quest Id: ", TextColors.WHITE, "(Color Support)"));

            Sponge.getEventManager().unregisterListeners(this);
            registerInterestId2.submit(afmcp);
        }

        // Before submitting this task you should insert Path in buffer
        // Resulting buffer will contain QuestLevel array from file
        // If array.length is 0 - it's empty
        private void updateBufferJsonData() {
            if (buffer instanceof Path) {
                try {
                    String jsonData = new String(Files.readAllBytes(((Path) buffer)));

                    Gson gson = new GsonBuilder()
                            .setPrettyPrinting()
                            .serializeNulls()
                            .registerTypeAdapter(QuestLevelContainer.class, new QuestLevelContainerDeserializer())
                            .registerTypeAdapter(QuestLevel.class, new QuestLevelDeserializer())
                            .registerTypeAdapter(Quest.class, new QuestDeserializer())
                            .create();

                    if (!jsonData.equals("")) {
                        buffer = gson.fromJson(jsonData, QuestLevelContainer.class);
                    } else {
                        buffer = new QuestLevelContainer(new QuestLevel[0]);
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }



        private void transportingError(int id) {
            reply(source, Text.of(TextColors.RED, "Error on transporting QuestBuffer to ID " + id));
            reply(source, Text.of(TextColors.RED, "Unregistering listeners without saving data"));
        }
    }
}
