import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Jaikin — Chaikin's algorithm step-by-step animation.
 *
 * Entry point + window setup.
 * Run with:  javac *.java && java Main
 */
public final class Main {

    private static final int WIDTH  = 900;
    private static final int HEIGHT = 640;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::launch);
    }

    private static void launch() {
        AppState state = new AppState();
        CanvasPanel canvas = new CanvasPanel(state);

        // Wire the Chaikin animation controller into the canvas.
        canvas.setController(new ChaikinAnimationController(state, canvas));

        JFrame frame = new JFrame("Jaikin — Chaikin's Algorithm");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(canvas);
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        canvas.requestFocusInWindow();
    }
}
