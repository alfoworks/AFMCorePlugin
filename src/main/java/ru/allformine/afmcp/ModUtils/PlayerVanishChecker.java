package ru.allformine.afmcp.ModUtils;

import org.kitteh.vanish.VanishManager;
import ru.allformine.afmcp.AFMCorePlugin;
import ru.allformine.afmcp.References;

public class PlayerVanishChecker {
    public boolean CheckVanish(String name) {
        if(References.vmng != null) {
            return References.vmng.isVanished(name);
        } else {
            return false; //В случае, если нет VanishManager'а мы разрешаем добавлять игрока
        }
    }
}
