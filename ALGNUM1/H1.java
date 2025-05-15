package org.example;

import static org.example.Main.wierzcholki;

public class H1 {
    public static boolean check() {
        if (wierzcholki == null || wierzcholki.length == 0) return false;
        wierzcholek vn = wierzcholki[wierzcholki.length - 1];
        return vn.getX() == 1 && vn.getY() == 0;
    }

    public static void main(String[] args) {
        wierzcholek vn = wierzcholki[wierzcholki.length - 1];
        System.out.println("\nH1 is " + (check() ? "true" : "false") +
                ": vn is " + (check() ? "equal to v0 (1, 0)" : "not equal to v0 (1, 0)") +
                ", vn.getX() = " + vn.getX() + ", vn.getY() = " + vn.getY());
    }
}