import java.util.ArrayList;
import java.util.List;

public final class Chaikin {

    /**
     * Performs one Chaikin subdivision iteration.
     * This class is independent from the UI and application state;
     * it is purely the algorithm engine.
     */
    public static List<Point> refineOnce(List<Point> points) {
        List<Point> refined = new ArrayList<>();

        // Edge cases: keep the original geometry.
        if (points == null || points.size() < 3) {
            if (points != null) {
                refined.addAll(points);
            }
            return refined;
        }

        // Keep first endpoint.
        refined.add(points.get(0));

        // Generate the new Q and R points.
        for (int i = 0; i < points.size() - 1; i++) {
            Point p0 = points.get(i);
            Point p1 = points.get(i + 1);

            double qx = 0.75 * p0.x() + 0.25 * p1.x();
            double qy = 0.75 * p0.y() + 0.25 * p1.y();

            double rx = 0.25 * p0.x() + 0.75 * p1.x();
            double ry = 0.25 * p0.y() + 0.75 * p1.y();

            refined.add(new Point(qx, qy));
            refined.add(new Point(rx, ry));
        }

        // Keep last endpoint.
        refined.add(points.get(points.size() - 1));

        return refined;
    }

    /**
     * Generates all Chaikin steps from step 0 to maxStep.
     * The animation controller can consume this list directly.
     */
    public static List<List<Point>> generateSteps(List<Point> controlPoints, int maxStep) {
        List<List<Point>> steps = new ArrayList<>();

        List<Point> current = copyPoints(controlPoints);
        steps.add(current);

        for (int step = 1; step <= maxStep; step++) {
            current = refineOnce(current);
            steps.add(current);
        }

        return steps;
    }

    private static List<Point> copyPoints(List<Point> points) {
        List<Point> copy = new ArrayList<>();

        if (points == null) {
            return copy;
        }

        for (Point point : points) {
            copy.add(new Point(point.x(), point.y()));
        }

        return copy;
    }
}