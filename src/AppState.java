import java.util.ArrayList;
import java.util.List;

/**
 * Shared application state — the contract between input handling,
 * the Chaikin engine / animation, and the app shell.
 *
 * Holds the control points, the high-level mode (IDLE <-> ANIMATING),
 * and the current Chaikin step being displayed.
 */
public final class AppState {

    /** Highest Chaikin step shown before the animation loops back to the input. */
    public static final int MAX_STEP = 7;

    /** High-level app mode. */
    public enum Mode { IDLE, ANIMATING }

    private final List<Point> controlPoints = new ArrayList<>();
    private Mode mode = Mode.IDLE;

    /** Current Chaikin step being displayed (0..7). Written by the animation loop. */
    private int step = 0;

    public List<Point> controlPoints() {
        return controlPoints;
    }

    public void addControlPoint(double x, double y) {
        controlPoints.add(new Point(x, y));
    }

    public void moveControlPoint(int index, double x, double y) {
        controlPoints.get(index).set(x, y);
    }

    public void clear() {
        controlPoints.clear();
        mode = Mode.IDLE;
        step = 0;
    }

    public boolean hasPoints() {
        return !controlPoints.isEmpty();
    }

    public Mode mode() { return mode; }
    public void setMode(Mode mode) { this.mode = mode; }

    public int step() { return step; }
    public void setStep(int step) { this.step = step; }
}
