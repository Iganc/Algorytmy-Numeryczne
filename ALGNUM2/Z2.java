import java.util.*;

public class Z2 {
    private final int N;
    private final double h;
    private final double L;
    private final double g;
    private final double H;

    public Z2(int N, double h, double L, double g, double H) {
        this.N = N;
        this.h = h;
        this.L = L;
        this.g = g;
        this.H = H;
    }

    public SystemData buildSystem(double t) {
        double domainLength = L;
        double T = 2 * Math.PI / Math.sqrt(g * 2 * Math.PI / L);
        double k = 2 * Math.PI / L;
        double omega = 2 * Math.PI / T;
        double dx = domainLength / N;
        double dz = h / N;
        double amplitude = g * H / (2 * omega);

        List<int[]> gridPoints = new ArrayList<>();
        Map<String, Integer> indexMap = new HashMap<>();
        int idx = 0;

        // Generate grid
        for (int i = 0; i <= N; i++) {
            double x = i * dx;
            for (int j = 0; j <= N; j++) {
                gridPoints.add(new int[]{i, j});
                indexMap.put(i + "," + j, idx++);
            }
        }

        Z3 matrix = new Z3(gridPoints.size());
        double[] rhs = new double[gridPoints.size()];
        Arrays.fill(rhs, 0.0);

        // Fill matrix and RHS
        for (int i = 0; i <= N; i++) {
            for (int j = 0; j <= N; j++) {
                int row = indexMap.get(i + "," + j);
                double x = i * dx;
                double z = -j * dz;

                handleBoundaryConditions(matrix, rhs, i, j, row, x, z,
                        k, h, omega, t, dx, dz, amplitude, indexMap);

                if (j > 0 && j < N && i > 0 && i < N) {
                    handleInteriorPoints(matrix, row, i, j, dx, dz, indexMap);
                }
            }
        }

        return new SystemData(matrix, rhs, gridPoints, indexMap);
    }

    private void handleBoundaryConditions(Z3 matrix, double[] rhs,
                                          int i, int j, int row, double x, double z, double k, double h,
                                          double omega, double t, double dx, double dz, double amplitude,
                                          Map<String, Integer> indexMap) {

        // Surface boundary (z = 0)
        if (j == 0) {
            double kh = k * h;
            double tanh_kh = Math.tanh(kh);
            matrix.set(row, row, omega*omega + g/dz * tanh_kh);
            int belowIdx = indexMap.get(i + "," + (j+1));
            matrix.set(row, belowIdx, -g/dz * tanh_kh);

            if (i > 0 && i < N) {
                rhs[row] = amplitude * Math.sin(k * x - omega * t);
            }
        }
        // Bottom boundary (z = -h)
        else if (j == N) {
            matrix.set(row, row, 1.0);
            int aboveIdx = indexMap.get(i + "," + (j-1));
            matrix.set(row, aboveIdx, -1.0);
        }
        // Left/right boundaries
        else if (i == 0 || i == N) {
            matrix.set(row, row, 1.0);
            double coshTerm = Math.cosh(k * (z + h)) / Math.cosh(k * h);
            rhs[row] = amplitude * coshTerm * Math.sin(k * x - omega * t);
        }
    }

    private void handleInteriorPoints(Z3 matrix, int row,
                                      int i, int j, double dx, double dz, Map<String, Integer> indexMap) {

        matrix.set(row, row, -2.0/(dx*dx) - 2.0/(dz*dz));

        // x-direction
        int leftIdx = indexMap.get((i-1) + "," + j);
        int rightIdx = indexMap.get((i+1) + "," + j);
        matrix.set(row, leftIdx, 1.0/(dx*dx));
        matrix.set(row, rightIdx, 1.0/(dx*dx));

        // z-direction
        int aboveIdx = indexMap.get(i + "," + (j-1));
        int belowIdx = indexMap.get(i + "," + (j+1));
        matrix.set(row, aboveIdx, 1.0/(dz*dz));
        matrix.set(row, belowIdx, 1.0/(dz*dz));
    }

    public record SystemData(
            Z3 matrix,
            double[] rhs,
            List<int[]> gridPoints,
            Map<String, Integer> indexMap
    ) {}
}