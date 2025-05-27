import java.util.Arrays;

public class Z3 {
    private double[] xKnots;
    private double[] xValues;
    private double[] yKnots;
    private double[] yValues;

    private double[][] xSplineCoefficients;  // [a, b, c, d] for each interval on x-axis
    private double[][] ySplineCoefficients;  // [a, b, c, d] for each interval on y-axis

    public Z3(Z1 z1, Z2 z2) {
        int N = z1.getN();
        double[] solution = z2.getSolution();

        xKnots = new double[N+1];
        xValues = new double[N+1];
        yKnots = new double[N+1];
        yValues = new double[N+1];

        extractAxisValues(z1, solution, xKnots, xValues, yKnots, yValues);

        xSplineCoefficients = computeCubicSpline(xKnots, xValues);
        ySplineCoefficients = computeCubicSpline(yKnots, yValues);
    }

    private void extractAxisValues(Z1 z1, double[] solution,
                                  double[] xPoints, double[] xValues,
                                  double[] yPoints, double[] yValues) {
        int N = z1.getN();
        double h = z1.getH();

        for (int i = 0; i <= N; i++) {
            xPoints[i] = -1.0 + i * h;
            yPoints[i] = -1.0 + i * h;
        }

        for (int i = 0; i <= N; i++) {
            double x = xPoints[i];
            double y = 0.0;
            double distSq = x*x + y*y;

            if (distSq < 1.0) {
                int idx = getIndex(z1, i, N/2);
                xValues[i] = (idx >= 0) ? solution[idx] : 0.0;
            } else {
                xValues[i] = evaluateBoundaryCondition(x, y);
            }

            x = 0.0;
            y = yPoints[i];
            distSq = x*x + y*y;

            if (distSq < 1.0) {
                int idx = getIndex(z1, N/2, i);
                yValues[i] = (idx >= 0) ? solution[idx] : 0.0;
            } else {
                yValues[i] = evaluateBoundaryCondition(x, y);
            }
        }
    }

    private double evaluateBoundaryCondition(double x, double y) {
        return x*x - y*y;
    }

    private int getIndex(Z1 z1, int i, int j) {
        int count = 0;
        int N = z1.getN();
        double h = z1.getH();

        for (int ii = 0; ii <= N; ii++) {
            for (int jj = 0; jj <= N; jj++) {
                double xx = -1.0 + ii * h;
                double yy = -1.0 + jj * h;
                if (xx*xx + yy*yy < 1.0) {
                    if (ii == i && jj == j) {
                        return count;
                    }
                    count++;
                }
            }
        }
        return -1;
    }

    private double[][] computeCubicSpline(double[] x, double[] y) {
        int n = x.length - 1;
        double[][] coefficients = new double[n][4];

        if (n <= 0) return coefficients;

        double[] h = new double[n];
        for (int i = 0; i < n; i++) {
            h[i] = x[i+1] - x[i];
        }

        double[] mu = new double[n];
        double[] z = new double[n+1];

        for (int i = 1; i < n; i++) {
            double alpha = h[i-1] / (h[i-1] + h[i]);
            double beta = 1 - alpha;
            double g = 3 * (alpha * (y[i+1] - y[i]) / h[i] + beta * (y[i] - y[i-1]) / h[i-1]);
            mu[i] = beta / (2 - mu[i-1] * alpha);
            z[i] = (g - alpha * z[i-1]) / (2 - mu[i-1] * alpha);
        }

        for (int j = n-1; j >= 0; j--) {
            z[j] = z[j] - mu[j] * z[j+1];
        }

        for (int i = 0; i < n; i++) {
            double hi = h[i];
            double zi = z[i];
            double ziplus1 = z[i+1];

            coefficients[i][0] = y[i];

            coefficients[i][1] = (y[i+1] - y[i])/hi - hi*(2*zi + ziplus1)/6;

            coefficients[i][2] = zi/2;

            coefficients[i][3] = (ziplus1 - zi)/(6*hi);
        }

        return coefficients;
    }

    private double[] solveTridiagonal(double[] a, double[] b, double[] c, double[] d) {
        int n = d.length;
        double[] x = new double[n];

        for (int i = 1; i < n; i++) {
            double m = a[i-1] / b[i-1];
            b[i] -= m * c[i-1];
            d[i] -= m * d[i-1];
        }

        x[n-1] = d[n-1] / b[n-1];
        for (int i = n-2; i >= 0; i--) {
            x[i] = (d[i] - c[i] * x[i+1]) / b[i];
        }

        return x;
    }

    public double evaluateX(double x) {
        if (x <= xKnots[0]) return xValues[0];
        if (x >= xKnots[xKnots.length-1]) return xValues[xValues.length-1];

        int i = 0;
        while (i < xKnots.length - 2 && x > xKnots[i+1]) i++;

        double dx = x - xKnots[i];
        return xSplineCoefficients[i][0] +
               xSplineCoefficients[i][1] * dx +
               xSplineCoefficients[i][2] * dx * dx +
               xSplineCoefficients[i][3] * dx * dx * dx;
    }

    public double evaluateY(double y) {
        int i = 0;
        while (i < yKnots.length - 2 && y > yKnots[i+1]) i++;

        double dy = y - yKnots[i];
        return ySplineCoefficients[i][0] +
               ySplineCoefficients[i][1] * dy +
               ySplineCoefficients[i][2] * dy * dy +
               ySplineCoefficients[i][3] * dy * dy * dy;
    }

    public double[] getXKnots() { return xKnots; }
    public double[] getXValues() { return xValues; }
    public double[] getYKnots() { return yKnots; }
    public double[] getYValues() { return yValues; }
    public double[][] getXSplineCoefficients() { return xSplineCoefficients; }
    public double[][] getYSplineCoefficients() { return ySplineCoefficients; }
}