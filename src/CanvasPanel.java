import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.util.List;

/**
 * The drawing surface and input handler.
 *
 *  - Canvas surface (this JPanel).
 *  - Left-click places a control point into {@link AppState}.
 *  - Each control point is rendered as a small filled dot.
 *  - Enter fires the "start animation" event via {@link AnimationController}.
 *  - Repaints on every state change.
 *
 * The animated Chaikin curve is supplied by the {@link AnimationController}
 * and drawn in {@link #paintComponent}.
 */
@SuppressWarnings("serial")  // Swing component is never serialized in this app
public final class CanvasPanel extends JPanel {

    private static final int POINT_RADIUS = 5;   // filled dot
    private static final int DRAG_PICK_RADIUS = 10;  // click tolerance for picking a point to drag

    private static final String HINT_TEXT =
            "Left click: add/drag  -  C: clear  -  Enter: animate  -  Esc: quit";
    private static final String EMPTY_ENTER_MESSAGE =
            "Add at least one point before starting.";
    private static final int MESSAGE_DURATION_MS = 2000;

    private final AppState state;
    private AnimationController controller = AnimationController.NONE;

    // Reminder — shown for a few seconds when Enter is pressed
    // with no points, then fades out after a short delay.
    private boolean showReminder = false;
    private javax.swing.Timer reminderTimer;

    // Index of the control point currently being dragged, or -1 if none.
    private int dragIndex = -1;

    public CanvasPanel(AppState state) {
        this.state = state;
        setBackground(Color.BLACK);
        setFocusable(true);
        installMouse();
        installKeys();
    }

    /** Injects the animation controller that drives the Chaikin curve. */
    public void setController(AnimationController controller) {
        this.controller = (controller == null) ? AnimationController.NONE : controller;
    }

    // ---- mouse input -------------------------------------------------------

    private void installMouse() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) {
                    return;
                }

                int hit = hitTest(e.getX(), e.getY());
                if (hit >= 0) {
                    dragIndex = hit;
                } else {
                    state.addControlPoint(e.getX(), e.getY());
                    clearReminder();
                    // Fold the new point into a running animation (no second
                    // Enter needed); no-op when not animating.
                    controller.onControlPointsChanged();
                    repaint();
                }
                requestFocusInWindow();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    dragIndex = -1;
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragIndex < 0) {
                    return;
                }

                state.moveControlPoint(dragIndex, e.getX(), e.getY());
                controller.onControlPointsChanged();
                repaint();
            }
        });
    }

    private int hitTest(int x, int y) {
        double hitRadiusSq = (double) DRAG_PICK_RADIUS * DRAG_PICK_RADIUS;

        for (int i = state.controlPoints().size() - 1; i >= 0; i--) {
            Point p = state.controlPoints().get(i);
            double dx = x - p.x();
            double dy = y - p.y();
            if (dx * dx + dy * dy <= hitRadiusSq) {
                return i;
            }
        }

        return -1;
    }

    // ---- keyboard input via key bindings -----------------------------------

    private void installKeys() {
        bind("ENTER", "startAnimation", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { onEnter(); }
        });
        bind("ESCAPE", "quit", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { onEscape(); }
        });
        bind("C", "clear", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { onClear(); }
        });
    }

    private void bind(String key, String name, AbstractAction action) {
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), name);
        getActionMap().put(name, action);
    }

    private void onEnter() {
        if (!state.hasPoints()) {
            // No points: show the reminder. No crash, drawing still allowed.
            startReminder();
            repaint();
            return;
        }
        controller.start();  // handoff to the animation engine
    }

    /** Show the reminder for MESSAGE_DURATION_MS, then auto-hide. */
    private void startReminder() {
        showReminder = true;
        if (reminderTimer != null) {
            reminderTimer.stop();
        }
        reminderTimer = new javax.swing.Timer(MESSAGE_DURATION_MS, e -> clearReminder());
        reminderTimer.setRepeats(false);
        reminderTimer.start();
    }

    private void clearReminder() {
        if (reminderTimer != null) {
            reminderTimer.stop();
        }
        if (showReminder) {
            showReminder = false;
            repaint();
        }
    }

    private void onEscape() {
        // Close the window cleanly. Dispose so the JVM exits without errors.
        java.awt.Window w = javax.swing.SwingUtilities.getWindowAncestor(this);
        if (w != null) {
            w.dispose();
        }
    }

    private void onClear() {
        // Clear canvas without restarting the program.
        controller.stop();
        state.clear();
        dragIndex = -1;
        clearReminder();
        repaint();
    }

    // ---- rendering ---------------------------------------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw the current Chaikin step's curve, supplied by the controller.
            List<Point> curve = controller.getCurrentPoints();

            if (curve.size() >= 2) {

                g2.setColor(Color.WHITE);
                g2.setStroke(new java.awt.BasicStroke(2f));

                for (int i = 0; i < curve.size() - 1; i++) {

                    Point p1 = curve.get(i);
                    Point p2 = curve.get(i + 1);

                    g2.drawLine(
                            (int) p1.x(),
                            (int) p1.y(),
                            (int) p2.x(),
                            (int) p2.y()
                    );
                }
            }

            drawControlPoints(g2);
            drawHud(g2);
        } finally {
            g2.dispose();
        }
    }

    private void drawControlPoints(Graphics2D g2) {
        g2.setColor(Color.BLUE);
        for (Point p : state.controlPoints()) {
            int x = (int) Math.round(p.x());
            int y = (int) Math.round(p.y());

            // filled dot — no surrounding ring
            g2.fillOval(x - POINT_RADIUS, y - POINT_RADIUS, POINT_RADIUS * 2, POINT_RADIUS * 2);
        }
    }

    /** On-screen HUD: control hint, optional reminder, and step counter. */
    private void drawHud(Graphics2D g2) {
        // Control hint, top-left.
        g2.setFont(g2.getFont().deriveFont(java.awt.Font.PLAIN, 18f));
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawString(HINT_TEXT, 10, 22);

        // Empty-Enter reminder, below the hint.
        if (showReminder) {
            g2.setFont(g2.getFont().deriveFont(java.awt.Font.PLAIN, 24f));
            g2.setColor(Color.RED);
            g2.drawString(EMPTY_ENTER_MESSAGE, 10, 50);
        }

        // Step counter, bottom-right.
        g2.setFont(g2.getFont().deriveFont(java.awt.Font.PLAIN, 24f));
        g2.setColor(Color.WHITE);
        String stepText = state.step() == 0
                ? "Input"
                : "Step: " + state.step() + "/" + AppState.MAX_STEP;
        int textWidth = g2.getFontMetrics().stringWidth(stepText);
        g2.drawString(stepText, getWidth() - textWidth - 20, getHeight() - 20);
    }
}
