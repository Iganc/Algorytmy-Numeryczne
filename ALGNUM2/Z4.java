import org.apache.commons.math3.linear.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Z4 {
    public static void compare(Z3 customMatrix, double[] rhs) {
        long start = System.nanoTime();

        // Custom solver
        Z1 customSolver = new Z1();
        Set<Integer> dirichletRows = findDirichletRows(customMatrix);
        double[] customSolution = customSolver.sparseGauss(customMatrix, rhs, dirichletRows);

        long customTime = System.nanoTime() - start;

        // Library solver
        start = System.nanoTime();
        RealMatrix libMatrix = convertToApacheMatrix(customMatrix);
        RealVector libSolution = solveWithApache(libMatrix, rhs);
        long libTime = System.nanoTime() - start;

        printComparison(customSolution, libSolution, customTime, libTime);
    }

    private static RealMatrix convertToApacheMatrix(Z3 customMatrix) {
        int size = customMatrix.getSize();
        RealMatrix matrix = new OpenMapRealMatrix(size, size);

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                double value = customMatrix.get(row, col);
                if (value != 0) matrix.setEntry(row, col, value);
            }
        }
        return matrix;
    }

    private static RealVector solveWithApache(RealMatrix matrix, double[] rhs) {
        DecompositionSolver solver = new LUDecomposition(matrix).getSolver();
        return solver.solve(new ArrayRealVector(rhs));
    }

    private static Set<Integer> findDirichletRows(Z3 matrix) {
        Set<Integer> dirichletRows = new HashSet<>();
        for (int row = 0; row < matrix.getSize(); row++) {
            if (matrix.get(row, row) == 1.0 && countNonZeros(matrix, row) == 1) {
                dirichletRows.add(row);
            }
        }
        return dirichletRows;
    }

    private static int countNonZeros(Z3 matrix, int row) {
        int count = 0;
        for (int col = 0; col < matrix.getSize(); col++) {
            if (matrix.get(row, col) != 0) count++;
        }
        return count;
    }

    private static void printComparison(double[] custom, RealVector lib,
                                        long customTime, long libTime) {
        System.out.println("\n=== Porównanie solverów ===");
        System.out.printf("Czas własnej implementacji: %.2f ms\n", customTime/1e6);
        System.out.printf("Czas biblioteki Apache: %.2f ms\n", libTime/1e6);
        System.out.printf("Przewaga wydajności: %.2fx\n", (double)libTime/customTime);

        double maxError = 0;
        for (int i = 0; i < custom.length; i++) {
            maxError = Math.max(maxError, Math.abs(custom[i] - lib.getEntry(i)));
        }
        System.out.printf("Maksymalny błąd: %.6e\n", maxError);
    }

    public static void compareAnalytical(double[] solution, List<int[]> gridPoints, Map<String, Integer> indexMap,
                                         double dx, double dz, double h, double L, double g, double amplitude) {
        double k = 2 * Math.PI / L;
        double T = 2 * Math.PI / Math.sqrt(g * 2 * Math.PI / L);
        double omega = 2 * Math.PI / T;
        double t = 0.0;

        double targetZ = 0;
        int targetJ = (int)Math.round(-targetZ / dz);

        System.out.println("\nPotential φ at depth z = " + targetZ + "m");
        System.out.println("   x      |  Numerical   |  Analytical   |   Error");
        System.out.println("---------------------------------------------------");

        int N = (int)Math.sqrt(gridPoints.size()) - 1;
        for (int i = 0; i <= N; i++) {
            double x = i * dx;
            int index = indexMap.get(i + "," + targetJ);
            double numerical = solution[index];

            // Analytical solution: φ = A * cosh(k(z+h))/cosh(kh) * sin(kx-ωt)
            double coshTerm = Math.cosh(k * (targetZ + h)) / Math.cosh(k * h);
            double analytical = amplitude * coshTerm * Math.sin(k * x - omega * t);

            double error = Math.abs(numerical - analytical);

            System.out.printf("%8.3f | %12.6f | %12.6f | %9.2e\n", x, numerical, analytical, error);
        }
    }
}