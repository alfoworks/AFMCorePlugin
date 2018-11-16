package ru.allformine.afmcp;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.scheduler.BukkitScheduler;
import ru.allformine.afmcp.eco.server;

public final class main extends org.bukkit.plugin.java.JavaPlugin implements Listener
{
  public void onEnable()
  {
    getLogger().info("AFMCorePlugin is enabled. Author: Iterator.");
    Bukkit.getMessenger().registerOutgoingPluginChannel(this, "FactionsShow");
    getServer().getPluginManager().registerEvents(this, this);

    BukkitScheduler scheduler = getServer().getScheduler();
    scheduler.scheduleSyncRepeatingTask(this, new Runnable()
    {
      public void run() {
        try {
          for (Player player: Bukkit.getOnlinePlayers()) {
            
            ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
            DataOutputStream datastream = new DataOutputStream(bytestream);
            
            datastream.writeUTF(ChatColor.BLUE+"Временно отключено.");
            
            player.sendPluginMessage(Bukkit.getPluginManager().getPlugin("AFMCorePlugin"), "FactionsShow", bytestream.toByteArray());
          }
        } catch (IOException e) {
          e.printStackTrace(); } } }, 0L, 40L);
  }
  



  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
  {
    if (cmd.getName().equalsIgnoreCase("afm")) {
      sender.sendMessage("AFMCorePlugin is working!");
      return true; }
    if (cmd.getName().equalsIgnoreCase("tokens")) {
      if (!(sender instanceof Player)) {
        sender.sendMessage(vars.ecoPrefix + "Данная команда может быть выполнена только игроком.");
      } else {
        String bal = null;
        try {
          bal = server.get(sender.getName());
        } catch (Exception e) {
          e.printStackTrace();
        }
        
        if (bal == "Hacking attempt!") {
          sender.sendMessage(vars.ecoPrefix + "Произошла ошибка при выполнении этой команды.");
        } else {
          sender.sendMessage(vars.ecoPrefix + "Ваш баланс: " + vars.ecoColor + bal + " токен(-ов)");
        }
      }
      return true; }
    if (cmd.getName().equalsIgnoreCase("vip")) {
      if (args.length < 1) {
        sender.sendMessage(vars.ecoPrefix + "Использование команды:");
        sender.sendMessage(vars.ecoColor + "/vip " + ChatColor.WHITE + "<" + vars.ecoColor + "alpha" + ChatColor.WHITE + "/" + vars.ecoColor + "beta" + ChatColor.WHITE + "/" + vars.ecoColor + "gamma" + ChatColor.WHITE + "> - стать обладателем подписки Aplha, Beta или Gamma.");
        sender.sendMessage("Вы можете узнать подробнее о подписках на нашем сайте!");
        sender.sendMessage(vars.ecoColor + "https://allformine.ru/vip");

      }
      else if ((!args[0].equalsIgnoreCase("alpha")) && 
        (!args[0].equalsIgnoreCase("beta")) && 
        (!args[0].equalsIgnoreCase("gamma"))) {
        sender.sendMessage(vars.ecoPrefix + "Доступные подписки: Alpha | Beta | Gamma");
      } else {
        String bal = "";
        try {
            bal = server.get(sender.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (bal == "Hacking attempt!") {
          sender.sendMessage(vars.ecoPrefix + "Произошла ошибка при выполнении этой команды.");
        } else {
          int price = 0;
          
          if (args[0].equalsIgnoreCase("alpha")) {
              price = 1990;
          } else if (args[0].equalsIgnoreCase("beta")) {
            price = 4990;
          } else if (args[0].equalsIgnoreCase("gamma")) {
            price = 7990;
          }
          
          int balnum = Integer.valueOf(bal).intValue();
          
          if (balnum < price) {
            String needed = Integer.toString(price - balnum);
            
            sender.sendMessage(vars.ecoPrefix + "Недостаточно средств для приобретения.");
            sender.sendMessage(vars.ecoPrefix + "Вам нужно еще " + vars.ecoColor + needed + " токенов" + ChatColor.WHITE + ".");
          } else {
            try {
              server.rem(sender.getName(), Integer.toString(price));
            } catch (Exception e) {
              e.printStackTrace();
            }
            sender.sendMessage(vars.ecoPrefix + "Успешно. Благодарим за покупку!");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "givevip " + sender.getName() + " " + args[0] + " 30");
          }
        }
      }
      
      return true; }
    String nick; if (cmd.getName().equalsIgnoreCase("eco")) {
      if (args.length < 1) {
        sender.sendMessage(vars.ecoPrefix + "Использование команды:");
        sender.sendMessage(vars.ecoColor + "/eco " + ChatColor.WHITE + "add <" + vars.ecoColor + "кол-во" + ChatColor.WHITE + "> [" + vars.ecoColor + "ник" + ChatColor.WHITE + "] - добавить токенов себе или игроку, если он указан");
        sender.sendMessage(vars.ecoColor + "/eco " + ChatColor.WHITE + "rem <" + vars.ecoColor + "кол-во" + ChatColor.WHITE + "> [" + vars.ecoColor + "ник" + ChatColor.WHITE + "] - вычесть токены себе или игроку, если он указан");
        sender.sendMessage(vars.ecoColor + "/eco " + ChatColor.WHITE + "set <" + vars.ecoColor + "кол-во" + ChatColor.WHITE + "> [" + vars.ecoColor + "ник" + ChatColor.WHITE + "] - установить себе или игроку определенное количество токенов");
        sender.sendMessage(vars.ecoColor + "/eco " + ChatColor.WHITE + "get <" + vars.ecoColor + "ник" + ChatColor.WHITE + "> - узнать баланс токенов другого игрока (используйте /tokens чтобы узнать свой)");
      } else {
        switch (args[0]) {
        case "add": 
          if (args.length < 2) {
            sender.sendMessage(vars.ecoPrefix + "Недостаточно аргументов! Напишите /eco для помощи.");
          }
          else if (!args[1].matches("-?\\d+(\\.\\d+)?")) {
            sender.sendMessage(vars.ecoPrefix + "Ошибка: в качестве количества токенов вы указали не число.");
          } else {
            String nickname;
            if (args.length < 3) {
              nickname = sender.getName();
            } else {
              nickname = args[2];
            }
            
            String result = null;
            try {
              result = server.add(nickname, args[1]);
            } catch (Exception e) {
              e.printStackTrace();
            }
            
            if (result != "Hacking attempt!") {
              sender.sendMessage(vars.ecoPrefix + "Вы успешно добавили " + vars.ecoColor + args[1] + " токенов " + ChatColor.WHITE + " игроку " + vars.ecoColor + nickname + ChatColor.WHITE + ".");
            } else {
              sender.sendMessage(vars.ecoPrefix + "Произошла ошибка при выполнении этой команды. Проверьте введенные данные.");
            }
          }
          
          break;
        case "rem": 
          if (args.length < 2) {
            sender.sendMessage(vars.ecoPrefix + "Недостаточно аргументов! Напишите /eco для помощи.");
          }
          else if (!args[1].matches("-?\\d+(\\.\\d+)?")) {
            sender.sendMessage(vars.ecoPrefix + "Ошибка: в качестве количества токенов вы указали не число.");
          } else {
            String nickname;
            if (args.length < 3) {
              nickname = sender.getName();
            } else {
              nickname = args[2];
            }
            
            String result = null;
            try {
              result = server.rem(nickname, args[1]);
            } catch (Exception e) {
              e.printStackTrace();
            }
            
            if (result != "Hacking attempt!") {
              sender.sendMessage(vars.ecoPrefix + "Вы успешно вычли " + vars.ecoColor + args[1] + " токенов " + ChatColor.WHITE + " со счета игрока " + vars.ecoColor + nickname + ChatColor.WHITE + ".");
            } else {
              sender.sendMessage(vars.ecoPrefix + "Произошла ошибка при выполнении этой команды. Проверьте введенные данные.");
            }
          }
          
          break;
        case "set": 
          if (args.length < 2) {
            sender.sendMessage(vars.ecoPrefix + "Недостаточно аргументов! Напишите /eco для помощи.");
          }
          else if (!args[1].matches("-?\\d+(\\.\\d+)?")) {
            sender.sendMessage(vars.ecoPrefix + "Ошибка: в качестве количества токенов вы указали не число.");
          } else {
            String nickname;
            if (args.length < 3) {
              nickname = sender.getName();
            } else {
              nickname = args[2];
            }
            
            String result = null;
            try {
              result = server.set(nickname, args[1]);
            } catch (Exception e) {
              e.printStackTrace();
            }
            
            if (result != "Hacking attempt!") {
              sender.sendMessage(vars.ecoPrefix + "Вы успешно установили баланс на " + vars.ecoColor + args[1] + " токенов " + ChatColor.WHITE + " игроку " + vars.ecoColor + nickname + ChatColor.WHITE + ".");
            } else {
              sender.sendMessage(vars.ecoPrefix + "Произошла ошибка при выполнении этой команды. Проверьте введенные данные.");
            }
          }
          
          break;
        case "get": 
          if (args.length < 2) {
            sender.sendMessage(vars.ecoPrefix + "Недостаточно аргументов! Напишите /eco для помощи.");
          } else {
            nick = args[1];
            
            String result = null;
            try {
              result = server.get(nick);
            } catch (Exception e) {
              e.printStackTrace();
            }
            
            if (result != "Hacking attempt!") {
              sender.sendMessage(vars.ecoPrefix + "Баланс игрока " + vars.ecoColor + nick + ChatColor.WHITE + ": " + vars.ecoColor + result + ChatColor.WHITE + ".");
            } else {
              sender.sendMessage(vars.ecoPrefix + "Произошла ошибка при выполнении этой команды. Проверьте введенные данные.");
            }
          }
          break;
        default: 
          sender.sendMessage(vars.ecoPrefix + "Наберите /eco для помощи.");
        }
        
      }
      return true; }
    if (cmd.getName().equalsIgnoreCase("fregen")) {
      if (args.length < 1) {
        return false;
      }
      
      Faction faction = FactionColl.get().getByName(args[0]);
      
      if (faction == null) {
        sender.sendMessage(vars.fregenPrefix + "Фракция с таким именем не найдена!");
      } else {
        java.util.Set<PS> chunks = BoardColl.get().getChunks(faction);
        
        for (PS chunk : chunks) {
          try {
            chunk.asBukkitWorld().regenerateChunk(chunk.asBukkitChunk().getX(), chunk.asBukkitChunk().getZ());
          } catch (Error e) {
            sender.sendMessage(vars.fregenPrefix + "Произошла ошибка при регене чанка фракции: " + e.toString());
          }
        }
        
        sender.sendMessage(vars.fregenPrefix + "Фракция успешно отрегенерировна!");
        Bukkit.broadcastMessage("Администратор " + vars.fregenColor + sender.getName() + ChatColor.WHITE + " отрегенерировал фракцию " + vars.fregenColor + faction.getName() + ChatColor.WHITE + "!");
        
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "f disband " + faction.getName());
      }
      
      return true;
    }
    return false;
  }

    //Проверка, лол.
    @EventHandler
    public void onPing(ServerListPingEvent event) {
        event.forEach(player -> {
            player.setPlayerListName("idi_nahui");
        });
    }
}