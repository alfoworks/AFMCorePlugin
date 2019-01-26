package ru.allformine.afmcp.net.socket;

import com.sun.net.httpserver.*;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.scheduler.BukkitRunnable;
import ru.allformine.afmcp.AFMCorePlugin;
import ru.allformine.afmcp.ServerAPICommandSender;
import ru.allformine.afmcp.net.NetUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HTTPServer extends BukkitRunnable {
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

    static class EchoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody());
            BufferedReader br = new BufferedReader(isr);

            String response = "";

            List<String> args = Arrays.asList(br.readLine().split(" "));
            String cmd = args.remove(0);

            if(cmd.equals("EX_COMMAND")) {
                String commandData = String.join(" ", args);

                if(commandData.length() > 0) {
                    ServerAPICommandSender sender = new ServerAPICommandSender();
                    Bukkit.getServer().dispatchCommand(sender, commandData);

                    String commandOut = String.join("\n", sender.getOutput());

                    if(commandOut.length() > 0) {
                        response = NetUtils.statusTextResponse("ok", commandOut);
                    } else {
                        response = NetUtils.statusTextResponse("ok", "<no response from command>");
                    }
                }
            } else {
                response = NetUtils.statusTextResponse("err", "Wrong command");
            }

            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());

            os.close();
        }
    }

    static class Auth extends Authenticator {
        @Override
        public Result authenticate(HttpExchange httpExchange) {
            if ("/forbidden".equals(httpExchange.getRequestURI().toString()))
                return new Failure(403);
            else
                return new Success(new HttpPrincipal("c0nst", "realm"));
        }
    }
}