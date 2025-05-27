import java.util.Arrays;

public class Z2 {
    private double[] solution;
    private int systemSize;

    public Z2(Z1 z1) {
        double[][] A = z1.getA();
        double[] b = z1.getB();
        systemSize = z1.getSystemSize();

        solution = solveGauss(A, b);
    }

    public double[] getSolution() {
        return solution;
    }

    private double[] solveGauss(double[][] A, double[] b) {
        double[][] matrix = deepCopyMatrix(A);
        double[] rhs = Arrays.copyOf(b, b.length);

        for (int pivot = 0; pivot < systemSize; pivot++) {
            int maxRow = findPivotRow(matrix, pivot);
            if (maxRow != pivot) {
                swapRows(matrix, pivot, maxRow);
                double temp = rhs[pivot];
                rhs[pivot] = rhs[maxRow];
                rhs[maxRow] = temp;
            }

            double diag = matrix[pivot][pivot];
            if (Math.abs(diag) < 1e-12) continue;

            for (int row = pivot + 1; row < systemSize; row++) {
                double factor = matrix[row][pivot] / diag;
                if (Math.abs(factor) < 1e-12) continue;

                rhs[row] -= factor * rhs[pivot];
                for (int col = pivot; col < systemSize; col++) {
                    double val = matrix[row][col] - factor * matrix[pivot][col];
                    if (Math.abs(val) < 1e-12) val = 0.0;
                    matrix[row][col] = val;
                }
            }
        }

        double[] solution = new double[systemSize];
        for (int row = systemSize - 1; row >= 0; row--) {
            double sum = 0.0;
            for (int col = row + 1; col < systemSize; col++) {
                sum += matrix[row][col] * solution[col];
            }
            solution[row] = (rhs[row] - sum) / matrix[row][row];
        }

        return solution;
    }

    private int findPivotRow(double[][] matrix, int pivot) {
        int maxRow = pivot;
        double maxVal = Math.abs(matrix[pivot][pivot]);

        for (int row = pivot + 1; row < systemSize; row++) {
            double absVal = Math.abs(matrix[row][pivot]);
            if (absVal > maxVal) {
                maxVal = absVal;
                maxRow = row;
            }
        }

        return maxRow;
    }

    private void swapRows(double[][] matrix, int row1, int row2) {
        double[] temp = matrix[row1];
        matrix[row1] = matrix[row2];
        matrix[row2] = temp;
    }

    private double[][] deepCopyMatrix(double[][] original) {
        double[][] copy = new double[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = Arrays.copyOf(original[i], original[i].length);
        }
        return copy;
    }
}
