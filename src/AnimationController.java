/**
 * Seam for Dev B. Dev A's canvas fires these events; Dev B implements the
 * Chaikin step engine + animation loop behind this interface (tasks B1-B7),
 * and Dev C wires a concrete implementation into Main (task C3).
 *
 * A no-op default implementation ({@link #NONE}) is provided so the
 * input/canvas layer is fully runnable on its own.
 */
public interface AnimationController {

    /** Called when the user presses Enter with at least one control point. */
    void start();

    /** Called when the animation should stop / reset (e.g. on clear). */
    void stop();

    /** No-op controller so Dev A's layer runs before Dev B plugs in. */
    AnimationController NONE = new AnimationController() {
        @Override public void start() {
            System.out.println("[stub] animation start requested — awaiting Dev B engine");
        }
        @Override public void stop() { }
    };
}
