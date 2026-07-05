import java.util.List;

public class ChaikinAnimator {

    private final int maxStep;
    private List<List<Point>> steps;
    private int currentStep;

    public ChaikinAnimator(int maxStep) {
        this.maxStep = maxStep;
        currentStep = 0;
    }

    /**
     * Initializes the animation by generating all Chaikin steps.
     * Dev C: Call this method when the animation starts.
     */
    public void start(List<Point> controlPoints) {
        steps = Chaikin.generateSteps(controlPoints, maxStep);
        currentStep = 0;
    }

    /**
     * Returns the points of the current animation frame.
     */
    public List<Point> getCurrentPoints() {
        if (steps == null || steps.isEmpty()) {
            return List.of();
        }

        return steps.get(currentStep);
    }

    /**
     * Advances to the next animation frame.
     * After the final step, the animation restarts from step 0.
     */
    public void nextStep() {
        if (steps == null || steps.isEmpty()) {
            return;
        }

        currentStep++;

        if (currentStep >= steps.size()) {
            currentStep = 0;
        }
    }

    /**
     * Returns the current animation step.
     */
    public int getCurrentStep() {
        return currentStep;
    }

    /**
     * Returns true if the animation has been initialized.
     */
    public boolean isRunning() {
        return steps != null && !steps.isEmpty();
    }

    /**
     * Recomputes all steps from updated control points while keeping
     * the current step index (used during real-time drag).
     */
    public void regenerate(List<Point> controlPoints) {
        if (steps == null) {
            return;
        }

        int step = currentStep;
        steps = Chaikin.generateSteps(controlPoints, maxStep);
        currentStep = Math.min(step, steps.size() - 1);
    }

    /**
     * Stops the animation.
     * Dev C: Can be called when resetting the application state.
     */
    public void reset() {
        steps = null;
        currentStep = 0;
    }
}