package ru.alfomine.afmcp.serverapi;

import com.google.gson.Gson;
import com.sun.net.httpserver.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.alfomine.afmcp.PluginConfig;
import ru.alfomine.afmcp.PluginStatics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class APIServer extends BukkitRunnable {
    public HashMap<Player, byte[]> playerScreenshotData = new HashMap<>();
    public HashMap<Player, Boolean> playerScreenshotConfirmation = new HashMap<>();

    private String[] allowedPicModes = {"highres", "lowres", "extralowres", "grayscale"};

    static void responseString(HttpExchange exchange, int httpCode, String string) {
        byte[] bytes = string.getBytes();

        try {
            exchange.sendResponseHeaders(httpCode, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);

            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        int port = PluginConfig.serverApiPort;
        HttpServer server;

        System.out.println("[AFMCP_APISERVER] Starting HTTP server at port " + port);

        try {
            server = HttpServer.create();
            server.bind(new InetSocketAddress(port), 0);

            HttpContext context = server.createContext("/serverAPI", new EchoHandler());
            context.setAuthenticator(new Auth());

            server.setExecutor(null);
            server.start();

            System.out.println("[AFMCP_APISERVER] HTTP server has been successfully started!");
        } catch (Exception e) {
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

                        long startTime = System.currentTimeMillis();
                        while (String.join("\n", sender.getOutput()).length() < 1) {
                            if (System.currentTimeMillis() >= startTime + 5000) {
                                break;
                            }
                        }

                        String commandOutput = String.join("\n", sender.getOutput());

                        if (commandOutput.length() > 0) {
                            responseString(exchange, 200, commandOutput);
                        } else {
                            responseString(exchange, 204, commandOutput);
                        }
                    } else {
                        responseString(exchange, 400, "");
                    }
                    break;
                case "PLAYER_LIST":
                    List<String> players = new ArrayList<>();

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        players.add(p.getName());
                    }

                    if (players.size() > 0) {
                        responseString(exchange, 200, new Gson().toJson(players));
                    } else {
                        responseString(exchange, 204, "");
                    }
                    break;
                case "TAKE_SCREENSHOT":
                    responseString(exchange, 501, "Not available!");

                    break;
                case "SERVER_INFO":
                    int playerCount = Bukkit.getOnlinePlayers().size();
                    int maxPlayers = Bukkit.getMaxPlayers();
                    long uptime = System.currentTimeMillis() - PluginStatics.startTime;


                    HashMap<String, Object> info = new HashMap<>();
                    info.put("players", playerCount);
                    info.put("maxPlayers", maxPlayers);
                    info.put("serverUptime", uptime);

                    String json = new Gson().toJson(info);
                    responseString(exchange, 200, json);
                    break;
                default:
                    responseString(exchange, 405, "");
                    break;
            }
        }
    }

    // ========================= //

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