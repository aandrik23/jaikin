import java.util.ArrayList;
import java.util.List;

/**
 * Shared application state — the contract between input (Dev A),
 * the Chaikin engine / animation (Dev B), and the app shell (Dev C).
 *
 * NOTE (Dev C): this is a minimal holder created so Dev A's canvas/input
 * layer is runnable. Expand this into the full IDLE <-> ANIMATING state
 * machine (tasks C1/C2) and let the animation loop read `step` from here.
 */
public final class AppState {

    /** High-level app mode. Dev C owns transitions between these. */
    public enum Mode { IDLE, ANIMATING }

    private final List<Point> controlPoints = new ArrayList<>();
    private Mode mode = Mode.IDLE;

    /** Current Chaikin step being displayed (0..7). Written by Dev B's loop. */
    private int step = 0;

    public List<Point> controlPoints() {
        return controlPoints;
    }

    public void addControlPoint(double x, double y) {
        controlPoints.add(new Point(x, y));
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
