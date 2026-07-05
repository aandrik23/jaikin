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
 * Dev A — the drawing surface and input handler.
 *
 *  A2  Canvas surface (this JPanel).
 *  A3  Left-click places a control point into {@link AppState}.
 *  A4  Each control point is rendered as a small ringed circle.
 *  A6  Enter fires the "start animation" event via {@link AnimationController}.
 *  A7  Repaints on every state change.
 *
 * Rendering of the animated Chaikin curve itself is Dev B's job (task B7);
 * a hook is marked below where that drawing should slot in.
 */
@SuppressWarnings("serial")  // Swing component is never serialized in this app
public final class CanvasPanel extends JPanel {

    private static final int POINT_RADIUS = 4;   // filled dot
    private static final int RING_RADIUS  = 8;    // circle "around" the point

    private final AppState state;
    private AnimationController controller = AnimationController.NONE;

    // Bonus reminder (task C5) — shown when Enter is pressed with no points.
    private boolean showReminder = false;

    // Bonus drag (task C7)
    private int dragIndex = -1;

    public CanvasPanel(AppState state) {
        this.state = state;
        setBackground(Color.BLACK);
        setFocusable(true);
        installMouse();
        installKeys();
    }

    /** Dev C injects the real controller here (task C3). */
    public void setController(AnimationController controller) {
        this.controller = (controller == null) ? AnimationController.NONE : controller;
    }

    // ---- A3: mouse input ---------------------------------------------------

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
                    showReminder = false;
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
        double hitRadius = RING_RADIUS + 4;
        double hitRadiusSq = hitRadius * hitRadius;

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

    // ---- A5/A6: keyboard input via key bindings ----------------------------

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
            // A6 empty case + bonus reminder (C5). No crash, drawing still allowed.
            showReminder = true;
            repaint();
            return;
        }
        controller.start();  // handoff to Dev B's engine
    }

    private void onEscape() {
        // A5 — close the window cleanly. Dispose so the JVM exits without errors.
        java.awt.Window w = javax.swing.SwingUtilities.getWindowAncestor(this);
        if (w != null) {
            w.dispose();
        }
    }

    private void onClear() {
        // Bonus C6 — clear canvas without restarting the program.
        controller.stop();
        state.clear();
        dragIndex = -1;
        showReminder = false;
        repaint();
    }

    // ---- A4/A7: rendering --------------------------------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // === Dev B hook (task B7) ===========================================
            // Draw the current Chaikin step's curve here, reading state.step().
            // Left intentionally empty in Dev A's layer.
            // ====================================================================
            List<Point> curve = controller.getCurrentPoints();

            if (curve.size() >= 2) {

                g2.setColor(Color.GREEN);

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

            if (showReminder) {
                drawReminder(g2);
            }
        } finally {
            g2.dispose();
        }
    }

    private void drawControlPoints(Graphics2D g2) {
        for (Point p : state.controlPoints()) {
            int x = (int) Math.round(p.x());
            int y = (int) Math.round(p.y());

            // filled dot
            g2.setColor(Color.WHITE);
            g2.fillOval(x - POINT_RADIUS, y - POINT_RADIUS, POINT_RADIUS * 2, POINT_RADIUS * 2);

            // ring "around" the point (audit: small circle around each point)
            g2.setColor(Color.WHITE);
            g2.drawOval(x - RING_RADIUS, y - RING_RADIUS, RING_RADIUS * 2, RING_RADIUS * 2);
        }
    }

    private void drawReminder(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.drawString("Draw at least one point (left-click), then press Enter.", 12, 20);
    }
}
