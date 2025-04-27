package org.example.models;

import java.awt.datatransfer.FlavorEvent;

public class Heading {
    private final int level;
    private final String text;

    public Heading(int level, String text) {
        this.level = level;
        this.text = text;
    }

    public int getLevel() {
        return level;
    }

    public String getText() {
        return text;
    }
}
