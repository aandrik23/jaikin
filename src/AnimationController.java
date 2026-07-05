import java.util.List;

/**
 * Seam between the canvas and the animation engine. The canvas fires these
 * events; a concrete implementation runs the Chaikin step engine + animation
 * loop behind this interface and is wired into the app in Main.
 *
 * A no-op default implementation ({@link #NONE}) is provided so the
 * input/canvas layer is fully runnable on its own.
 */
public interface AnimationController {

    void start();

    void stop();

    // Current curve to render.
    List<Point> getCurrentPoints();

    /** Called when control points move (bonus drag). Default no-op. */
    default void onControlPointsChanged() { }

    AnimationController NONE = new AnimationController() {

        @Override
        public void start() {
            System.out.println("[stub] animation start requested — no engine wired in");
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
