import java.util.Set;

public class Main {
    public static void main(String[] args) {
        int N = 80;
        double h = 10.0, L = 40.0, g = 9.81, H = 0.1;
        double dx = L / N;
        double dz = h / N;

        // Z2 - Budowa układu równań
        Z2 builder = new Z2(N, h, L, g, H);
        Z2.SystemData system = builder.buildSystem(0.0);

        // Z1 - Rozwiązanie
        Z1 solver = new Z1();
        Set<Integer> dirichletRows = Z1.findDirichletRows(system.indexMap(), N);
        double[] solution = solver.sparseGauss(system.matrix(), system.rhs(), dirichletRows);

        // Z4 - Porównanie
        Z4.compare(system.matrix(), system.rhs());
        Z4.compareAnalytical(solution, system.gridPoints(), system.indexMap(), dx, dz, h, L, g, H);
    }

}