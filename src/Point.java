/**
 * A 2D point. Coordinates are doubles so that later subdivision steps
 * (Chaikin's algorithm, owned by Dev B) can produce fractional positions.
 */
public final class Point {
    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double x() { return x; }
    public double y() { return y; }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Point(" + x + ", " + y + ")";
    }
}
