package ru.allformine.afmcp.notify;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

class Util {
    static byte[] getEmptyString() {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            out.writeUTF("");
        } catch(Exception e) {

        }

        return b.toByteArray();
    }
}