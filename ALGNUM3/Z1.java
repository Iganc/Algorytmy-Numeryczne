public class Z1 {
    private int N;
    private double h;
    private double[][] A;
    private double[] b;
    private int numInteriorPoints;
    private int[][] indexMap;
    private int[][] gridPoints;
    public Z1(int N) {
        this.N = N;
        this.h = 2.0 / N;
        buildLinearSystem();
    }

    private void buildLinearSystem() {
        countInteriorPointsAndCreateMapping();
        A = new double[numInteriorPoints][numInteriorPoints];
        b = new double[numInteriorPoints];
        fillLinearSystem();
    }

    private void countInteriorPointsAndCreateMapping() {
        int gridSize = N + 1;
        indexMap = new int[gridSize][gridSize];

        numInteriorPoints = 0;
        for (int i = 0; i <= N; i++) {
            double x = -1.0 + i * h;
            for (int j = 0; j <= N; j++) {
                double y = -1.0 + j * h;
                if (x*x + y*y < 1.0) {
                    indexMap[i][j] = numInteriorPoints++;
                } else {
                    indexMap[i][j] = -1;
                }
            }
        }

        gridPoints = new int[numInteriorPoints][2];
        for (int i = 0; i <= N; i++) {
            for (int j = 0; j <= N; j++) {
                if (indexMap[i][j] != -1) {
                    gridPoints[indexMap[i][j]][0] = i;
                    gridPoints[indexMap[i][j]][1] = j;
                }
            }
        }
    }

    private void fillLinearSystem() {
        for (int idx = 0; idx < numInteriorPoints; idx++) {
            int i = gridPoints[idx][0];
            int j = gridPoints[idx][1];

            A[idx][idx] = -4.0;

            int[][] neighbors = {{i, j+1}, {i+1, j}, {i, j-1}, {i-1, j}};

            for (int k = 0; k < 4; k++) {
                int ni = neighbors[k][0];
                int nj = neighbors[k][1];

                if (ni >= 0 && ni <= N && nj >= 0 && nj <= N) {
                    double nx = -1.0 + ni * h;
                    double ny = -1.0 + nj * h;
                    double distSq = nx*nx + ny*ny;

                    if (distSq < 1.0) {
                        int nidx = indexMap[ni][nj];
                        A[idx][nidx] = 1.0;
                    } else if (distSq <= 1.0 + 1e-10) {
                        handleBoundaryNeighbor(idx, i, j, ni, nj, k);
                    }
                }
            }
        }
    }

    private void handleBoundaryNeighbor(int idx, int i, int j, int ni, int nj, int direction) {
        double xi = -1.0 + i * h;
        double yj = -1.0 + j * h;

        double dx = (ni - i) * h;
        double dy = (nj - j) * h;

        double a = dx*dx + dy*dy;
        double b = 2*(xi*dx + yj*dy);
        double c = xi*xi + yj*yj - 1;
        double t = (-b + Math.sqrt(b*b - 4*a*c))/(2*a);

        double h_prime = t * h;

        if (h_prime > 1e-10) {
            double beta = h / h_prime;
            A[idx][idx] -= (1.0 - 1.0/beta);
        }
    }

    public void setBoundaryValues(BoundaryFunction boundaryFunction) {
        int boundaryNeighborsFound = 0;

        for (int idx = 0; idx < numInteriorPoints; idx++) {
            int i = gridPoints[idx][0];
            int j = gridPoints[idx][1];
            double xi = -1.0 + i * h;
            double yj = -1.0 + j * h;

            int[][] neighbors = {{i, j+1}, {i+1, j}, {i, j-1}, {i-1, j}};

            for (int k = 0; k < 4; k++) {
                int ni = neighbors[k][0];
                int nj = neighbors[k][1];

                if (ni >= 0 && ni <= N && nj >= 0 && nj <= N) {
                    double nx = -1.0 + ni * h;
                    double ny = -1.0 + nj * h;
                    double distSq = nx*nx + ny*ny;

                    if (distSq >= 1.0) {
                        double dx = (ni - i) * h;
                        double dy = (nj - j) * h;

                        double a = dx*dx + dy*dy;
                        double bVar = 2*(xi*dx + yj*dy);
                        double c = xi*xi + yj*yj - 1;
                        double t = (-bVar + Math.sqrt(bVar*bVar - 4*a*c))/(2*a);

                        double bx = xi + t * dx;
                        double by = yj + t * dy;

                        double h_prime = t * h;

                        double boundaryValue = boundaryFunction.value(bx, by);
                        double beta = h / h_prime;
                        b[idx] += boundaryValue / beta;
                        boundaryNeighborsFound++;
                    }
                }
            }
        }
        System.out.println("Found " + boundaryNeighborsFound + " neighbors near boundary");
    }
    public interface BoundaryFunction {
        double value(double x, double y);
    }

    public double[][] getA() {
        return A;
    }

    public double[] getB() {
        return b;
    }

    public int getSystemSize() {
        return numInteriorPoints;
    }

    public int getN() {
        return N;
    }

    public double getH() {
        return h;
    }

    public void printLinearSystem() {
        System.out.println("Linear system statistics for N = " + N + ":");
        System.out.println("- System size: " + numInteriorPoints + " equations");
        System.out.println("- Grid spacing: h = " + h);
        System.out.println("- Number of interior points: " + numInteriorPoints);

        int nonZeros = 0;
        for (int i = 0; i < numInteriorPoints; i++) {
            for (int j = 0; j < numInteriorPoints; j++) {
                if (Math.abs(A[i][j]) > 1e-10) nonZeros++;
            }
        }
        System.out.println("- Number of non-zero coefficients: " + nonZeros);

        int sampleSize = Math.min(10, numInteriorPoints);
        System.out.println("\nSample of coefficient matrix A (first " + sampleSize + " rows):");
        for (int i = 0; i < sampleSize; i++) {
            for (int j = 0; j < sampleSize; j++) {
                System.out.printf("%8.4f ", A[i][j]);
            }
            System.out.println(" ...");
        }
        System.out.println("...");

        System.out.println("\nSample of right-hand side vector b (first " + sampleSize + " elements):");
        for (int i = 0; i < sampleSize; i++) {
            System.out.printf("%8.4f\n", b[i]);
        }
        System.out.println("...");
    }
}