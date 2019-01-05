package ru.allformine.afmcp.modutils;

import ru.allformine.afmcp.References;

public class PlayerVanishChecker {
    public boolean CheckVanish(String name) {
        if (References.vmng != null) {
            return References.vmng.isVanished(name);
        } else {
            return false; //В случае, если нет VanishManager'а мы разрешаем добавлять игрока
        }
    }
}
