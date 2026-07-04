import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Jaikin — Chaikin's algorithm step-by-step animation.
 *
 * Dev A (task A1): entry point + window setup.
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

        // Dev C wires the real Chaikin animation controller here (task C3):
        canvas.setController(new ChaikinAnimationController(state, canvas));
        // Until then the no-op stub keeps the app runnable.

        JFrame frame = new JFrame("Jaikin — Chaikin's Algorithm");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(canvas);
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        canvas.requestFocusInWindow();
    }
}
