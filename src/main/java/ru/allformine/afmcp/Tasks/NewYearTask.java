package ru.allformine.afmcp.Tasks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import ru.allformine.afmcp.Notify;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class NewYearTask extends BukkitRunnable {
    public void run() {
        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int month = localDate.getMonthValue();
        int day = localDate.getDayOfMonth();
        int minute = Calendar.getInstance().get(Calendar.MINUTE);

        if(month == 1 && day == 1 && minute == 0) {
            Random random = new Random();
            Notify.NotifyAll(ChatColor.YELLOW+"С Новым Годом!");

            for(Player p : Bukkit.getOnlinePlayers()) {
                Firework fw = (Firework) p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
                FireworkMeta fwm = fw.getFireworkMeta();

                FireworkEffect effect = FireworkEffect.builder().flicker(true).trail(true).withColor(Color.FUCHSIA).build();

                fwm.addEffect(effect);
                fwm.setPower(4);

                fw.setFireworkMeta(fwm);

                //================================//
                String[] kits = new String[]{"ny1", "ny2"};
                String kit = kits[random.nextInt(kits.length)];
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "kitgive "+p.getDisplayName()+" "+kit+" 1");
                p.sendMessage(ChatColor.YELLOW+"Санта "+ChatColor.WHITE+"> Держи свой подарок!");
            }

            this.cancel();
        }
    }
}
