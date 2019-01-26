package ru.allformine.afmcp.net.socket;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.allformine.afmcp.AFMCorePlugin;
import ru.allformine.afmcp.References;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class APIServer extends BukkitRunnable {
    public HashMap<Player, Object[]> playerImages = new HashMap<>();

    public void run() {
        int port = AFMCorePlugin.getPlugin().getConfig().getInt("server_api.port");
        boolean acceptOnlyFromLocalhost = AFMCorePlugin.getPlugin().getConfig().getBoolean("server_api.acceptOnlyFromLocalhost");

        System.out.println("[AFMCP_APISERVER] Starting server...");

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("[AFMCP_APISERVER] Successfully started server on port " + String.valueOf(port));

            //noinspection InfiniteLoopStatement
            while (true) {
                Socket socket = serverSocket.accept();

                /* Временно идёт нахуй
                if (acceptOnlyFromLocalhost && socket.getInetAddress().isAnyLocalAddress()) {
                    System.out.println("[AFMCP_APISERVER] Client connection DENIED: not localhost.");

                    socket.close();
                    return;
                }
                */

                System.out.println("[AFMCP_APISERVER] New client connected.");

                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);

                String text;

                while (!socket.isClosed()) {
                    text = reader.readLine();
                    if(text != null) {
                        System.out.println("[AFMCP_APISERVER] Received message \""+text+"\"");

                        List<String> args = new ArrayList<String>(Arrays.asList(text.split(" ")));
                        String cmd = args.remove(0);

                        if (cmd.equals("EX_COMMAND")) {
                            String bukkitCommand = String.join(" ", args);

                            //Bukkit.getServer().dispatchCommand(References.sender, bukkitCommand);

                            //writer.println("{'status': 'OK', 'resp': '"+String.join("\n", References.sender.getOutput()+"'}"));
                        } else if(cmd.equals("TAKE_SCREENSHOT")) {
                            if(args.toArray().length > 0) {
                                String playerNick = args.get(0);
                                Player player = Bukkit.getServer().getPlayer(playerNick);

                                if(player != null) {
                                    ByteArrayOutputStream b = new ByteArrayOutputStream();
                                    DataOutputStream out = new DataOutputStream(b);

                                    try {
                                        out.writeByte(1);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    Bukkit.getServer().sendPluginMessage(AFMCorePlugin.getPlugin(), "scr", b.toByteArray());

                                    this.playerImages.put(player, new Object[]{false, ""});

                                    long timeStart = System.currentTimeMillis();

                                    while(!((boolean) this.playerImages.get(player)[0])) {
                                        if(System.currentTimeMillis() > timeStart+5000) {
                                            return;
                                        }
                                    }

                                    if((boolean) this.playerImages.get(player)[0]) {
                                        if(!((String) this.playerImages.get(player)[1]).equals("")) {
                                            writer.println("{'status': 'OK', 'resp': '"+this.playerImages.get(player)[1]+"'}");
                                        } else {
                                            writer.println("{'status': 'ERR', 'resp': 'Target player sent nothing'}");
                                        }
                                    } else {
                                        writer.println("{'status': 'ERR', 'resp': 'Target player not responding'}");
                                    }
                                    this.playerImages.remove(player);
                                } else {
                                    writer.println("{'status': 'ERR', 'resp': 'Player not found!'}");
                                }
                            } else {
                                writer.println("{'status': 'ERR', 'resp': 'Invalid arguments'}");
                            }
                        }
                    }
                }
            }

        } catch (Exception ex) {
            System.out.println("[AFMCP_APISERVER] Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
