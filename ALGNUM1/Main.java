package org.example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static int n = 10;
    public static double pi = Math.PI;
    public static BigDecimal theta = BigDecimal.valueOf((2 * pi) / n);
    public static BigDecimal sumX = BigDecimal.valueOf(0);
    public static BigDecimal sumY = BigDecimal.valueOf(0);
    public static List<BigDecimal[]> wiList = new ArrayList<>();
    public static wierzcholek[] wierzcholki;

    public static void setN(int newN) {
        n = newN;
        theta = BigDecimal.valueOf((2 * Math.PI) / n);
    }

    public static void main(String[] args) {
        System.out.println("Start programu");
        wierzcholki = new wierzcholek[n + 1];
        wierzcholki[0] = new wierzcholek(1, 0, "v0");

        BigDecimal[] w0 = {BigDecimal.valueOf(Math.cos(theta.doubleValue()) - 1), BigDecimal.valueOf(Math.sin(theta.doubleValue()))};


        sumX = BigDecimal.valueOf(0.0);
        sumY = BigDecimal.valueOf(0.0);

        for (int i = 1; i <= n; i++) {
            BigDecimal[] wi = rotateVector(w0, (i - 1) * theta.doubleValue());
            wiList.add(wi);
            sumX = sumX.add(wi[0]);
            sumY = sumY.add(wi[1]);

            double viX = wierzcholki[i - 1].getX() + wi[0].doubleValue();
            double viY = wierzcholki[i - 1].getY() + wi[1].doubleValue();
            wierzcholki[i] = new wierzcholek(viX, viY, "v" + i);
        }
        System.out.println("n = " + n);

        H1.main(args);
        H2.main(args);
        Hipoteza3.main(args);

        System.out.println("wierzchołek n " + n + ", x: " + wierzcholki[n].getX() + " y: " + wierzcholki[n].getY());
    }

    public static BigDecimal[] rotateVector(BigDecimal[] vector, double angle) {
        BigDecimal cosTheta = BigDecimal.valueOf(Math.cos(angle));
        BigDecimal sinTheta = BigDecimal.valueOf(Math.sin(angle));
        BigDecimal x = vector[0].multiply(cosTheta).subtract(vector[1].multiply(sinTheta));
        BigDecimal y = vector[0].multiply(sinTheta).add(vector[1].multiply(cosTheta));
        return new BigDecimal[]{x, y};
    }
}