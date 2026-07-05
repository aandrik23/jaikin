import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class ChaikinAnimationController implements AnimationController {

    private static final int MAX_STEP = AppState.MAX_STEP;
    private static final int FRAME_DELAY = 600;   // ms per step

    private final AppState state;
    private final CanvasPanel canvas;
    private final ChaikinAnimator animator;

    private Timer timer;

    public ChaikinAnimationController(AppState state, CanvasPanel canvas) {
        this.state = state;
        this.canvas = canvas;
        this.animator = new ChaikinAnimator(MAX_STEP);
    }

    @Override
    public void start() {

        // No points -> do nothing.
        if (!state.hasPoints()) {
            return;
        }

        // A single control point: just show the point, no animation.
        if (state.controlPoints().size() < 2) {
            stop();
            return;
        }

        // Two or more points enter the running state. With exactly two points
        // the curve stays a straight line (step advance is gated below on
        // having 3+ points), so adding a third point later starts animating
        // on its own without pressing Enter again.
        animator.start(state.controlPoints());

        state.setMode(AppState.Mode.ANIMATING);
        state.setStep(0);

        // Show step 0 (the straight polyline through the control points)
        // immediately, before the timer advances to the first refinement.
        canvas.repaint();

        if (timer != null) {
            timer.stop();
        }

        timer = new Timer(FRAME_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Fewer than 3 points: hold on step 0 (a straight line),
                // matching the Rust engine's early return.
                if (state.controlPoints().size() < 3) {
                    return;
                }

                animator.nextStep();

                state.setStep(animator.getCurrentStep());

                canvas.repaint();
            }
        });

        timer.start();
    }

    @Override
    public void stop() {

        if (timer != null) {
            timer.stop();
        }

        animator.reset();

        state.setMode(AppState.Mode.IDLE);
        state.setStep(0);

        canvas.repaint();
    }

    @Override
    public java.util.List<Point> getCurrentPoints() {
        return animator.getCurrentPoints();
    }

    @Override
    public void onControlPointsChanged() {
        if (state.mode() == AppState.Mode.ANIMATING && animator.isRunning()) {
            animator.regenerate(state.controlPoints());
            state.setStep(animator.getCurrentStep());
            canvas.repaint();
        }
    }
}