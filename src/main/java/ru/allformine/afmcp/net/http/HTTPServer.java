package ru.allformine.afmcp.net.http;

import com.sun.net.httpserver.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.allformine.afmcp.AFMCorePlugin;
import ru.allformine.afmcp.ProtocolHandler;
import ru.allformine.afmcp.ServerAPICommandSender;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.*;

public class HTTPServer extends BukkitRunnable {
    public HashMap<Player, byte[]> playerScreenshotData = new HashMap<>();
    public HashMap<Player, Boolean> playerScreenshotConfirmation = new HashMap<>();

    public void run() {
        int port = AFMCorePlugin.getPlugin().getConfig().getInt("server_api.port");
        HttpServer server;

        System.out.println("[AFMCP_APISERVER] Starting HTTP server at port "+port);

        try {
            server = HttpServer.create();
            server.bind(new InetSocketAddress(port), 0);


            HttpContext context = server.createContext("/serverAPI", new EchoHandler());
            context.setAuthenticator(new Auth());

            server.setExecutor(null);
            server.start();

            System.out.println("[AFMCP_APISERVER] HTTP server has been successfully started!");
        } catch (Exception e){
            System.out.println("[AFMCP_APISERVER] Error starting api server!");
            e.printStackTrace();
        }
    }

    class EchoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody());
            BufferedReader br = new BufferedReader(isr);
            String input = br.readLine();

            System.out.println("[AFMCP_APISERVER] Input: " + input);
            List<String> args = new ArrayList<>(Arrays.asList(input.split(" ")));
            String cmd = args.remove(0);

            switch (cmd) {
                case "EX_COMMAND":
                    String minecraftCommand = String.join(" ", args);

                    if (minecraftCommand.length() > 0) {
                        ServerAPICommandSender sender = new ServerAPICommandSender();

                        Bukkit.dispatchCommand(sender, minecraftCommand);
                        String commandOutput = String.join(" ", sender.getOutput());

                        if (commandOutput.length() > 0) {
                            ServerUtils.responseString(exchange, 200, commandOutput);
                        } else {
                            ServerUtils.responseString(exchange, 204, commandOutput);
                        }
                    } else {
                        ServerUtils.responseString(exchange, 400, "");
                    }
                    break;
                case "PLAYER_LIST":
                    boolean returnVanishPlayers = false;

                    if (args.size() > 0 && args.get(0).equals("true")) {
                        returnVanishPlayers = true;
                    }

                    List<String> players = new ArrayList<>();

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (returnVanishPlayers) {
                            players.add(p.getName());
                        } else {
                            if (!ProtocolHandler.isPlayerVanished(p.getName())) {
                                players.add(p.getName());
                            }
                        }
                    }

                    if (players.size() > 0) {
                        ServerUtils.responseString(exchange, 200, String.join("\n", players));
                    } else {
                        ServerUtils.responseString(exchange, 204, "");
                    }
                    break;
                case "TAKE_SCREENSHOT":
                    if (args.size() > 0) {
                        Player player = Bukkit.getPlayer(args.get(0));

                        if (player != null) {
                            playerScreenshotData.put(player, new byte[]{});
                            playerScreenshotConfirmation.put(player, false);

                            ByteArrayOutputStream b = new ByteArrayOutputStream();
                            DataOutputStream out = new DataOutputStream(b);

                            player.sendPluginMessage(AFMCorePlugin.getPlugin(), "C234Fb", new byte[]{});

                            long startTime = System.currentTimeMillis();
                            while (!playerScreenshotConfirmation.get(player)) {
                                if (System.currentTimeMillis() >= startTime + 5000) {
                                    break;
                                }
                            }

                            if (playerScreenshotConfirmation.get(player)) {
                                if (playerScreenshotData.get(player).length > 0) {
                                    String imageString = Base64.getEncoder().encodeToString(playerScreenshotData.get(player));

                                    ServerUtils.responseString(exchange, 200, imageString);
                                } else {
                                    ServerUtils.responseString(exchange, 500, "");
                                }
                            } else {
                                ServerUtils.responseString(exchange, 524, "");
                            }
                        } else {
                            ServerUtils.responseString(exchange, 410, "");
                        }
                    }
                    break;
                default:
                    ServerUtils.responseString(exchange, 405, "");
                    break;
            }
        }
    }

    class Auth extends Authenticator {
        @Override
        public Result authenticate(HttpExchange httpExchange) {
            if ("/forbidden".equals(httpExchange.getRequestURI().toString()))
                return new Failure(403);
            else
                return new Success(new HttpPrincipal("c0nst", "realm"));
        }
    }
}