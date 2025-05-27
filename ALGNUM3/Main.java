import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        int N = 15;

        // Z1: Build the linear system
        System.out.println("Z1 - Building linear system with N = " + N);
        Z1 z1 = new Z1(N);

        // Define a boundary function (example: f(x,y) = x^2 - y^2 satisfies Laplace equation)
        Z1.BoundaryFunction boundaryFunction = (x, y) -> x*x - y*y;
        z1.setBoundaryValues(boundaryFunction);
        z1.printLinearSystem();

        // Z2
        System.out.println("Z2 - Solving linear system of size " + z1.getSystemSize());
        Z2 z2 = new Z2(z1);
        double[] solution = z2.getSolution();

        // Z3
        System.out.println("Z3 - Performing cubic spline interpolation along axes");
        Z3 z3 = new Z3(z1, z2);

        // Test interpolation at specific points
        System.out.println("\nInterpolated values along x-axis (y=0):");
        for (double x = -0.9; x <= 0.9; x += 0.1) {
            System.out.printf("x = %.2f, z = %.6f\n", x, z3.evaluateX(x));
        }

        System.out.println("\nInterpolated values along y-axis (x=0):");
        for (double y = -0.9; y <= 0.9; y += 0.1) {
            System.out.printf("y = %.2f, z = %.6f\n", y, z3.evaluateY(y));
        }

        double[] xPoints = new double[N+1];
        double[] xValues = new double[N+1];
        double[] yPoints = new double[N+1];
        double[] yValues = new double[N+1];

        xPoints = z3.getXKnots();
        xValues = z3.getXValues();
        yPoints = z3.getYKnots();
        yValues = z3.getYValues();

        System.out.println("\nSolution values along x-axis (y=0):");
        for (int i = 0; i <= N; i++) {
            System.out.printf("x = %.4f, z = %.6f\n", xPoints[i], xValues[i]);
        }

        System.out.println("\nSolution values along y-axis (x=0):");
        for (int i = 0; i <= N; i++) {
            System.out.printf("y = %.4f, z = %.6f\n", yPoints[i], yValues[i]);
        }

        System.out.println("\nTesting with alternative boundary functions:");
        testWithFunction(z1, "f(x,y) = x^2-y^2", (x, y) -> x*x - y*y);
        testWithFunction(z1, "f(x,y) = e^x * sin(y)", (x, y) -> Math.exp(x) * Math.sin(y));
        testWithFunction(z1, "f(x,y) = x*(x²-3y²)/(x²+y²)²", (x, y) -> {
            double r2 = x*x + y*y;
            if (r2 < 1e-6) return 0.0;
            double denominator = r2 * r2;
            double result = x * (x*x - 3*y*y) / denominator;

            return result;
        });
    }


    private static void testWithFunction(Z1 z1, String name, Z1.BoundaryFunction function) {
        System.out.println("\nTesting with " + name);

        double step = z1.getH();
        int N = z1.getN();
        double minVal = -1.0 + step/2;


        List<Point> points = new ArrayList<>();

        for (int i = 0; i <= N; i++) {
            double x = minVal + i * step;
            for (int j = 0; j <= N; j++) {
                double y = minVal + j * step;
                double distSq = x*x + y*y;

                if (distSq <= 1.0 + 1e-10) {
                    double value = function.value(x, y);
                    points.add(new Point(x, y, value));
                }
            }
        }

        points.sort(Comparator.comparing(Point::getX).thenComparing(Point::getY));
        for (Point p : points) {
            System.out.printf("%.8f\t%.8f\t%.8f\n", p.x, p.y, p.z);
        }
    }

}