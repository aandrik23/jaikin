/**
 * Seam for Dev B. Dev A's canvas fires these events; Dev B implements the
 * Chaikin step engine + animation loop behind this interface (tasks B1-B7),
 * and Dev C wires a concrete implementation into Main (task C3).
 *
 * A no-op default implementation ({@link #NONE}) is provided so the
 * input/canvas layer is fully runnable on its own.
 */
import java.util.List;
public interface AnimationController {

    void start();

    void stop();

    // Dev B: current curve to render
    List<Point> getCurrentPoints();

    /** Called when control points move (bonus drag). Default no-op. */
    default void onControlPointsChanged() { }

    AnimationController NONE = new AnimationController() {

        @Override
        public void start() {
            System.out.println("[stub] animation start requested — awaiting Dev B engine");
        }

        @Override
        public void stop() {

        }

        @Override
        public List<Point> getCurrentPoints() {
            return List.of();
        }
    };
}
