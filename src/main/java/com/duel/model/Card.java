package com.duel.model;

public class Card {
    private String name;
    private int atk;
    private int def;
    private String imageUrl;

    public Card(String name, int atk, int def, String imageUrl) {
        this.name = name;
        this.atk = atk;
        this.def = def;
        this.imageUrl = imageUrl;
    }

    public String getName() { return name; }
    public int getAtk() { return atk; }
    public int getDef() { return def; }
    public String getImageUrl() { return imageUrl; }

    @Override
    public String toString() {
        return name + " (ATK: " + atk + ", DEF: " + def + ")";
    }
}
