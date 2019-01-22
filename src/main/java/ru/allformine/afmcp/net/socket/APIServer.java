package ru.allformine.afmcp.net.socket;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import ru.allformine.afmcp.AFMCorePlugin;
import ru.allformine.afmcp.References;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class APIServer extends BukkitRunnable {
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

                do {
                    text = reader.readLine();

                    List<String> args = Arrays.asList(text.split(" "));
                    String cmd = args.remove(0);

                    if (cmd.equals("EX_COMMAND")) {
                        String bukkitCommand = String.join(" ", args.subList(1, args.size()));

                        Bukkit.getServer().dispatchCommand(References.sender, bukkitCommand);

                        writer.println(References.sender.getLastOutput());
                    }
                } while (!socket.isClosed());
            }

        } catch (IOException ex) {
            System.out.println("[AFMCP_APISERVER] Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
