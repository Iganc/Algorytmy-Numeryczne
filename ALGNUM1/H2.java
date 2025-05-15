package org.example;

import java.math.BigDecimal;

import static org.example.Main.sumX;
import static org.example.Main.sumY;

public class H2 {
    private static BigDecimal errorH2 = BigDecimal.valueOf(0);
    public static boolean check() {
        errorH2 = BigDecimal.valueOf(Math.sqrt(sumY.pow(2).add(sumX.pow(2)).doubleValue()));
        return sumX.compareTo(BigDecimal.ZERO) == 0 && sumY.compareTo(BigDecimal.ZERO) == 0;

    }

    public static void main(String[] args) {
        System.out.println("H2 is " + (check() ? "true" : "false") +
                ": Suma wektorów wi " + (check() ? "= (0, 0)" : "≠ (0, 0)"));
        System.out.println("Error H2: "+ errorH2);
    }
}