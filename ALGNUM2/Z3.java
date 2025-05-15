import java.util.*;

public class Z3 {
    private final int size;
    final Map<Integer, Map<Integer, Double>> data;

    public Z3(int size) {
        this.size = size;
        this.data = new HashMap<>();
    }

    public void set(int row, int col, double value) {
        data.computeIfAbsent(row, k -> new HashMap<>()).put(col, value);
    }

    public double get(int row, int col) {
        return data.getOrDefault(row, Collections.emptyMap()).getOrDefault(col, 0.0);
    }

    public int getSize() { return size; }
}