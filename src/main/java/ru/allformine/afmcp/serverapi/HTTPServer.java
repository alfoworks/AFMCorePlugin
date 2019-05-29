package ru.allformine.afmcp.serverapi;

import com.google.gson.Gson;
import com.sun.net.httpserver.*;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import ru.allformine.afmcp.AFMCorePlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HTTPServer implements Runnable {
    @Override
    public void run() {
        int port = AFMCorePlugin.getConfig().getNode("server_api", "port").getInt();
        HttpServer server;

        AFMCorePlugin.logger.info("[AFMCP_APISERVER] Starting HTTP server at port " + port);

        try {
            server = HttpServer.create();
            server.bind(new InetSocketAddress(port), 0);

            HttpContext context = server.createContext("/serverAPI", new EchoHandler());
            context.setAuthenticator(new Auth());

            server.setExecutor(null);
            server.start();

            AFMCorePlugin.logger.info("[AFMCP_APISERVER] HTTP server has been successfully started!");
        } catch (Exception e) {
            AFMCorePlugin.logger.info("[AFMCP_APISERVER] Error starting api server!");
            e.printStackTrace();
        }
    }

    class EchoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody());
            BufferedReader br = new BufferedReader(isr);
            String input = br.readLine();

            AFMCorePlugin.logger.info("[AFMCP_APISERVER] Input: " + input);
            List<String> args = new ArrayList<>(Arrays.asList(input.split(" ")));
            String cmd = args.remove(0);

            switch (cmd) {
                case "EX_COMMAND":
                    String minecraftCommand = String.join(" ", args);

                    if (minecraftCommand.length() > 0) {
                        ServerAPISender sender = new ServerAPISender();

                        Task.builder().execute(() -> Sponge.getCommandManager().process(sender, minecraftCommand))
                                .name("AFMCP APISERVER command")
                                .submit(Sponge.getPluginManager().getPlugin("afmcp").get().getInstance().get());

                        long timeStart = System.currentTimeMillis();
                        do {
                            try {
                                Thread.sleep(0);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } while (sender.getOutput().size() == 0 && System.currentTimeMillis() < timeStart + 2500);

                        String commandOutput = String.join("\n", sender.getOutput());

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
                    List<String> playerList = new ArrayList<>();

                    for (Player player : Sponge.getServer().getOnlinePlayers()) {
                        playerList.add(player.getName());
                    }

                    if (playerList.size() > 0) {
                        ServerUtils.responseString(exchange, 200, new Gson().toJson(playerList));
                    } else {
                        ServerUtils.responseString(exchange, 204, "");
                    }
                    break;
                case "TAKE_SCREENSHOT":
                    ServerUtils.responseString(exchange, 500, "Иди нахуй.");
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