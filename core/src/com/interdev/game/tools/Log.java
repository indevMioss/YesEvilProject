package com.interdev.game.tools;

public class Log {

    public enum tag {
        A(true),
        B(false),
        C(true),
        D(true),
        E(true);

        boolean on;

        tag(boolean on) {
            this.on = on;
        }
    }

    public static void log(String text, tag label) {
        if (!label.on) return;
        System.out.println(text);
    }

}


