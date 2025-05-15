package org.example;

public class wierzcholek {
    private double x;
    private double y;
    private String name;

    public wierzcholek(double x, double y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getName() {
        return name;
    }
}