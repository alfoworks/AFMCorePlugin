package ru.allformine.afmcp.serverapi;

import com.google.gson.Gson;
import com.sun.net.httpserver.*;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.network.ChannelBinding;
import org.spongepowered.api.scheduler.Task;
import ru.allformine.afmcp.AFMCorePlugin;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.*;

public class HTTPServer implements Runnable {

    public HashMap<Player, byte[]> playerScreenshotData = new HashMap<>();
    public HashMap<Player, Boolean> playerScreenshotConfirmation = new HashMap<>();


    private String[] allowedPicModes = {"highres", "lowres", "extralowres", "grayscale"};

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
                    //ServerUtils.responseString(exchange, 500, "Иди нахуй.");
                    if(args.size() > 0){
                        Optional<Player> object = Sponge.getServer().getPlayer("123");
                        if(object.isPresent()){
                            Player player = object.get();
                            ByteArrayOutputStream b = new ByteArrayOutputStream();
                            DataOutputStream out = new DataOutputStream(b);

                            String mode = "16colors";

                            if (args.size() > 1) {
                                if (Arrays.asList(allowedPicModes).contains(args.get(1))) {
                                    mode = args.get(1);
                                }
                            }

                            try {
                                out.writeUTF(mode);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            // Мб можно оптимизировать, похуй
                            AFMCorePlugin.channel
                                    .get("screenshot")
                                    .sendTo(player, buf -> buf.writeByteArray(b.toByteArray()));
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
                        }else{
                            ServerUtils.responseString(exchange, 410, "");
                        }
                    }
                    break;
                case "SERVER_INFO":
                    int playerCount = Sponge.getServer().getOnlinePlayers().size();
                    int maxPlayers = Sponge.getServer().getMaxPlayers();
                    long uptime = System.currentTimeMillis() - AFMCorePlugin.startTime;

                    HashMap<String, Object> info = new HashMap<>();
                    info.put("players", playerCount);
                    info.put("maxPlayers", maxPlayers);
                    info.put("serverUptime", uptime);

                    String json = new Gson().toJson(info);
                    ServerUtils.responseString(exchange, 200, json);
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