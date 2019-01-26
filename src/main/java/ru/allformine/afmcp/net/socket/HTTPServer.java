package ru.allformine.afmcp.net.socket;

import com.sun.net.httpserver.*;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.scheduler.BukkitRunnable;
import ru.allformine.afmcp.AFMCorePlugin;
import ru.allformine.afmcp.ServerAPICommandSender;
import ru.allformine.afmcp.net.NetUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Map;

public class HTTPServer extends BukkitRunnable {
    public void run() {
        int port = AFMCorePlugin.getPlugin().getConfig().getInt("server_api.port");
        HttpServer server;

        System.out.println("[AFMCP_APISERVER] Starting HTTP server at port "+port);

        try {
            server = HttpServer.create();
            server.bind(new InetSocketAddress(port), 0);


            HttpContext context = server.createContext("/", new EchoHandler());
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
            Map<String, String> params = NetUtils.queryToMap(exchange.getRequestURI().getQuery());
            String cmd = params.get("cmd");

            String response = "";

            if(cmd != null) {
                if(cmd.equals("EX_COMMAND")) {
                    String extra = params.get("extra");
                    if(extra != null) {
                        ServerAPICommandSender sender = new ServerAPICommandSender();

                        Bukkit.getServer().dispatchCommand(sender, extra);

                        response = String.join(" ", sender.getOutput());
                    } else {
                        response = "{'status': 'err', 'resp': 'Command for execute at server is required'}";
                    }
                }
            } else {
                response = "{'status': 'err', 'resp': 'No command specified'}";
            }

            byte[] bytes = response.getBytes();
            exchange.sendResponseHeaders(200, bytes.length);

            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
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