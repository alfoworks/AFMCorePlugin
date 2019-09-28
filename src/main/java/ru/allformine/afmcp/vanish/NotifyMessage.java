package ru.allformine.afmcp.vanish;

public enum NotifyMessage {
    JOIN("%s незаметно вошёл на сервер"),
    QUIT("%s вышел с сервера"),

    VANISH_SWITCH_ON("%s теперь в ванише"),
    VANISH_SWITCH_OFF("%s теперь не в ванише");

    private String message;

    NotifyMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
