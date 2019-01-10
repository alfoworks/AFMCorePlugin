package ru.allformine.afmcp.notify;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class Util {
    byte[] getEmptyString() {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            out.writeUTF("");
        } catch(Exception e) {

        }

        return b.toByteArray();
    }
}
