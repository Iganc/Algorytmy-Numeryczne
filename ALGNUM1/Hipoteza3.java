package org.example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.example.Main.sumX;
import static org.example.Main.sumY;

public class Hipoteza3 {
        private static BigDecimal originalSum = BigDecimal.valueOf(0);
        private static BigDecimal h3Sum = BigDecimal.valueOf(0);
        private static BigDecimal SxSum = BigDecimal.valueOf(0);
        private static BigDecimal SySum = BigDecimal.valueOf(0);
        private static BigDecimal errorH3 = BigDecimal.valueOf(0);
        public static boolean check(){
            List<BigDecimal> xPos = new ArrayList<>();
            List<BigDecimal> xNeg = new ArrayList<>();
            List<BigDecimal> yPos = new ArrayList<>();
            List<BigDecimal> yNeg = new ArrayList<>();

            for (BigDecimal[] wi : Main.wiList) {
                if(wi[0].compareTo(BigDecimal.ZERO) >= 0) {
                    xPos.add(wi[0]);
                }
                else {
                    xNeg.add(wi[0]);
                }

                if(wi[1].compareTo(BigDecimal.ZERO) >= 0) {
                    yPos.add(wi[1]);
                }
                else {
                    yNeg.add(wi[1]);
                }
            }

            Collections.sort(xPos);
            Collections.sort(xNeg);

            Collections.sort(yPos);
            Collections.sort(yNeg);

            BigDecimal SxPos = BigDecimal.valueOf(0);
            BigDecimal SxNeg = BigDecimal.valueOf(0);
            for(int i = 0; i < xPos.size(); i++){
                SxPos = SxPos.add(xPos.get(i));
            }
            for(int i = xNeg.size() - 1; i >= 0; i--){
                SxNeg = SxNeg.add(xNeg.get(i));
            }
            BigDecimal SyPos = BigDecimal.valueOf(0);
            BigDecimal SyNeg = BigDecimal.valueOf(0);
            for(int i = 0; i < yPos.size(); i++){
                SyPos = SyPos.add(yPos.get(i));
            }
            for(int i = yNeg.size() - 1; i >= 0; i--){
                SyNeg = SyNeg.add(yNeg.get(i));
            }

            SxSum = SxPos.add(SxNeg);
            SySum = SyPos.add(SyNeg);
            originalSum = sumX.add(sumY);
            h3Sum = SxSum.add(SySum);
            errorH3 = BigDecimal.valueOf(Math.sqrt(SxSum.pow(2).add(SySum.pow(2)).doubleValue()));
            return h3Sum.compareTo(originalSum) <= 0;
        }
    public static void main(String[] args) {
        System.out.println("H3 is " + (check() ? "true" : "false") +
                ": Sorted sum is " + (check() ? "closer to zero." : "not closer to zero." + " original sum:" + originalSum + " h3sum:" + h3Sum)
        );
//        System.out.println("SxSum: " + SxSum);
//        System.out.println("SySum: " + SySum);
        System.out.println("errorH3: " + errorH3);
    }
}
