import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class ChaikinAnimationController implements AnimationController {

    private static final int MAX_STEP = 7;
    private static final int FRAME_DELAY = 500;

    private final AppState state;
    private final CanvasPanel canvas;
    private final ChaikinAnimator animator;

    private Timer timer;
    private boolean twoPointLineActive;

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

        // One or two control points:
        // Dev A draws them directly.
        // No animation should start.
        if (state.controlPoints().size() < 3) {
            twoPointLineActive = state.controlPoints().size() == 2;
            state.setMode(AppState.Mode.IDLE);
            state.setStep(0);
            canvas.repaint();
            return;
        }

        twoPointLineActive = false;
        animator.start(state.controlPoints());

        state.setMode(AppState.Mode.ANIMATING);
        state.setStep(0);

        if (timer != null) {
            timer.stop();
        }

        timer = new Timer(FRAME_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                animator.nextStep();

                state.setStep(animator.getCurrentStep());

                // Dev C:
                // The application state can be synchronized here if more
                // shared animation data is introduced later.

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
        twoPointLineActive = false;

        state.setMode(AppState.Mode.IDLE);
        state.setStep(0);

        // Dev C:
        // Reset any additional shared application state here.

        canvas.repaint();
    }

    @Override
    public java.util.List<Point> getCurrentPoints() {
        // Two control points after Enter: draw a straight line (no animation).
        if (twoPointLineActive && state.controlPoints().size() == 2) {
            return state.controlPoints();
        }

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