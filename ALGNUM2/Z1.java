import java.util.*;

class Z1 {
    public double[] sparseGauss(Z3 matrix, double[] rhs, Set<Integer> dirichletRows) {
        int n = rhs.length;
        double[] solution = Arrays.copyOf(rhs, n);

        for (int pivot = 0; pivot < n; pivot++) {
            if (dirichletRows.contains(pivot)) continue;

            int maxRow = findPivotRow(matrix, pivot, dirichletRows);
            if (maxRow != pivot) {
                swapRows(matrix, pivot, maxRow);
                double temp = solution[pivot];
                solution[pivot] = solution[maxRow];
                solution[maxRow] = temp;
            }

            double diag = matrix.get(pivot, pivot);
            if (Math.abs(diag) < 1e-12) continue;

            for (int row = pivot + 1; row < n; row++) {
                if (dirichletRows.contains(row)) continue;

                double factor = matrix.get(row, pivot) / diag;
                if (Math.abs(factor) < 1e-12) continue;

                solution[row] -= factor * solution[pivot];
                for (int col = pivot; col < n; col++) {
                    double val = matrix.get(row, col) - factor * matrix.get(pivot, col);
                    if (Math.abs(val) < 1e-12) val = 0.0;
                    matrix.set(row, col, val);
                }
            }
        }

        for (int row = n - 1; row >= 0; row--) {
            if (dirichletRows.contains(row)) continue;

            double sum = 0.0;
            for (int col = row + 1; col < n; col++) {
                sum += matrix.get(row, col) * solution[col];
            }
            solution[row] = (solution[row] - sum) / matrix.get(row, row);
        }

        return solution;
    }

    private int findPivotRow(Z3 matrix, int col, Set<Integer> dirichletRows) {
        int maxRow = col;
        double maxVal = Math.abs(matrix.get(col, col));

        for (int row = col + 1; row < matrix.getSize(); row++) {
            if (dirichletRows.contains(row)) continue;

            double currentVal = Math.abs(matrix.get(row, col));
            if (currentVal > maxVal) {
                maxVal = currentVal;
                maxRow = row;
            }
        }
        return maxRow;
    }

    private void swapRows(Z3 matrix, int row1, int row2) {
        Map<Integer, Double> r1 = matrix.data.getOrDefault(row1, new HashMap<>());
        Map<Integer, Double> r2 = matrix.data.getOrDefault(row2, new HashMap<>());
        matrix.data.put(row1, r2);
        matrix.data.put(row2, r1);
    }

    public static Set<Integer> findDirichletRows(Map<String, Integer> indexMap, int N) {
        Set<Integer> rows = new HashSet<>();
        for (int i = 0; i <= N; i++) {
            for (int j = 0; j <= N; j++) {
                if (i == 0 || i == N) {
                    rows.add(indexMap.get(i + "," + j));
                }
            }
        }
        return rows;
    }
}