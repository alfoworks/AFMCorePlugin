package ru.allformine.afmcp;

import java.util.Arrays;

class Util {
    static byte[] trim(byte[] bytes) {
        int i = bytes.length;
        while (i >= 0 && bytes[i] == 0) {
            --i;
        }

        return Arrays.copyOf(bytes, i);
    }
}
