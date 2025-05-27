import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

public class Z3comparator {

    public static void main(String[] args) {
        int N = 15;

        Z1 z1 = new Z1(N);
        Z1.BoundaryFunction boundaryFunction = (x, y) -> x*x - y*y;
        z1.setBoundaryValues(boundaryFunction);

        Z2 z2 = new Z2(z1);
        Z3 myZ3 = new Z3(z1, z2);

        double[] xKnots = myZ3.getXKnots();
        double[] xValues = myZ3.getXValues();
        double[] yKnots = myZ3.getYKnots();
        double[] yValues = myZ3.getYValues();

        SplineInterpolator interpolator = new SplineInterpolator();
        PolynomialSplineFunction xSpline = interpolator.interpolate(xKnots, xValues);
        PolynomialSplineFunction ySpline = interpolator.interpolate(yKnots, yValues);

        System.out.println("Comparing custom vs. Apache Commons Math implementation");
        System.out.println("\nComparison along x-axis:");
        for (double x = -0.9; x <= 0.9; x += 0.1) {
            double customResult = myZ3.evaluateX(x);
            double libraryResult = xSpline.value(x);
            System.out.printf("x = %.2f, Custom: %.6f, Library: %.6f, Diff: %.9f\n",
                    x, customResult, libraryResult, Math.abs(customResult - libraryResult));
        }

        System.out.println("\nComparison along y-axis:");
        for (double y = -0.9; y <= 0.9; y += 0.1) {
            double customResult = myZ3.evaluateY(y);
            double libraryResult = ySpline.value(y);
            System.out.printf("y = %.2f, Custom: %.6f, Library: %.6f, Diff: %.9f\n",
                    y, customResult, libraryResult, Math.abs(customResult - libraryResult));
        }
    }
}