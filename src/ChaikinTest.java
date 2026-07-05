import java.util.ArrayList;
import java.util.List;

/**
 * Lightweight unit tests — no external test framework.
 * Run:  javac *.java && java ChaikinTest
 */
public final class ChaikinTest {

    private static int passed = 0;

    public static void main(String[] args) {
        testRefineOnceNull();
        testRefineOnceEmpty();
        testRefineOnceOnePoint();
        testRefineOnceTwoPoints();
        testRefineOnceThreePointPolyline();
        testRefineOncePreservesEndpoints();
        testGenerateStepsCount();
        testGenerateStepsStepZeroIsCopy();
        testGenerateStepsPointGrowth();
        testAnimatorCyclesAfterStepSeven();
        testAnimatorRegenerateKeepsStep();
        testAnimatorReset();

        System.out.println("All " + passed + " tests passed.");
    }

    // ---- refineOnce --------------------------------------------------------

    private static void testRefineOnceNull() {
        assertSize(Chaikin.refineOnce(null), 0, "null input");
    }

    private static void testRefineOnceEmpty() {
        assertSize(Chaikin.refineOnce(List.of()), 0, "empty input");
    }

    private static void testRefineOnceOnePoint() {
        List<Point> input = List.of(new Point(5, 10));
        List<Point> out = Chaikin.refineOnce(input);
        assertSize(out, 1, "one point");
        assertPoint(out.get(0), 5, 10, "one point unchanged");
    }

    private static void testRefineOnceTwoPoints() {
        List<Point> input = List.of(new Point(0, 0), new Point(10, 0));
        List<Point> out = Chaikin.refineOnce(input);
        assertSize(out, 2, "two points");
        assertPoint(out.get(0), 0, 0, "two-point start");
        assertPoint(out.get(1), 10, 0, "two-point end");
    }

    private static void testRefineOnceThreePointPolyline() {
        // (0,0) — (4,0) — (4,4)
        List<Point> input = List.of(
                new Point(0, 0),
                new Point(4, 0),
                new Point(4, 4)
        );

        List<Point> out = Chaikin.refineOnce(input);
        assertSize(out, 6, "three-point refinement");

        assertPoint(out.get(0), 0, 0, "endpoint p0");
        assertPoint(out.get(1), 1, 0, "Q on (0,0)-(4,0)");
        assertPoint(out.get(2), 3, 0, "R on (0,0)-(4,0)");
        assertPoint(out.get(3), 4, 1, "Q on (4,0)-(4,4)");
        assertPoint(out.get(4), 4, 3, "R on (4,0)-(4,4)");
        assertPoint(out.get(5), 4, 4, "endpoint p2");
    }

    private static void testRefineOncePreservesEndpoints() {
        List<Point> input = List.of(
                new Point(-2, 3),
                new Point(1, 5),
                new Point(8, -1),
                new Point(10, 10)
        );

        List<Point> out = Chaikin.refineOnce(input);

        assertPoint(out.get(0), -2, 3, "first endpoint preserved");
        assertPoint(out.get(out.size() - 1), 10, 10, "last endpoint preserved");
        assertSize(out, 8, "four-point refinement size"); // 1 + 3*2 + 1
    }

    // ---- generateSteps -----------------------------------------------------

    private static void testGenerateStepsCount() {
        List<Point> input = List.of(
                new Point(0, 0),
                new Point(100, 0),
                new Point(100, 100)
        );

        List<List<Point>> steps = Chaikin.generateSteps(input, 7);
        assertEquals(steps.size(), 8, "steps 0..7 inclusive");
    }

    private static void testGenerateStepsStepZeroIsCopy() {
        List<Point> input = List.of(
                new Point(1, 2),
                new Point(3, 4),
                new Point(5, 6)
        );

        List<List<Point>> steps = Chaikin.generateSteps(input, 7);
        List<Point> step0 = steps.get(0);

        assertSize(step0, 3, "step 0 size");
        for (int i = 0; i < input.size(); i++) {
            assertPoint(step0.get(i), input.get(i).x(), input.get(i).y(), "step 0 copy");
        }

        // Mutating step 0 must not affect the original list objects in a shared way
        step0.get(0).set(99, 99);
        assertPoint(input.get(0), 1, 2, "step 0 is a deep copy");
    }

    private static void testGenerateStepsPointGrowth() {
        List<Point> input = List.of(
                new Point(0, 0),
                new Point(4, 0),
                new Point(4, 4)
        );

        List<List<Point>> steps = Chaikin.generateSteps(input, 3);
        assertSize(steps.get(0), 3, "step 0");
        assertSize(steps.get(1), 6, "step 1");
        assertSize(steps.get(2), 12, "step 2"); // refineOnce doubles point count
        assertSize(steps.get(3), 24, "step 3");
    }

    // ---- ChaikinAnimator ---------------------------------------------------

    private static void testAnimatorCyclesAfterStepSeven() {
        ChaikinAnimator animator = new ChaikinAnimator(7);
        List<Point> input = List.of(
                new Point(0, 0),
                new Point(50, 0),
                new Point(50, 50)
        );

        animator.start(input);
        assertTrue(animator.isRunning(), "animator running after start");
        assertEquals(animator.getCurrentStep(), 0, "starts at step 0");

        for (int i = 0; i < 7; i++) {
            animator.nextStep();
        }

        assertEquals(animator.getCurrentStep(), 7, "reaches step 7");
        animator.nextStep();
        assertEquals(animator.getCurrentStep(), 0, "wraps to step 0 after step 7");
    }

    private static void testAnimatorRegenerateKeepsStep() {
        ChaikinAnimator animator = new ChaikinAnimator(7);
        List<Point> input = new ArrayList<>(List.of(
                new Point(0, 0),
                new Point(40, 0),
                new Point(40, 40)
        ));

        animator.start(input);
        for (int i = 0; i < 3; i++) {
            animator.nextStep();
        }
        assertEquals(animator.getCurrentStep(), 3, "at step 3 before regenerate");

        input.get(1).set(80, 0);
        animator.regenerate(input);

        assertEquals(animator.getCurrentStep(), 3, "step index preserved after regenerate");
        assertTrue(animator.getCurrentPoints().size() > 0, "regenerated curve has points");
    }

    private static void testAnimatorReset() {
        ChaikinAnimator animator = new ChaikinAnimator(7);
        animator.start(List.of(new Point(0, 0), new Point(1, 1), new Point(2, 0)));
        animator.reset();

        assertFalse(animator.isRunning(), "not running after reset");
        assertSize(animator.getCurrentPoints(), 0, "no points after reset");
        assertEquals(animator.getCurrentStep(), 0, "step 0 after reset");
    }

    // ---- assertions --------------------------------------------------------

    private static void assertSize(List<?> list, int expected, String label) {
        int actual = list == null ? -1 : list.size();
        assertEquals(actual, expected, label + " size");
    }

    private static void assertPoint(Point p, double x, double y, String label) {
        assertEquals(p.x(), x, label + " x");
        assertEquals(p.y(), y, label + " y");
    }

    private static void assertEquals(double actual, double expected, String label) {
        if (Math.abs(actual - expected) > 1e-9) {
            fail(label + ": expected " + expected + " but was " + actual);
        }
        pass(label);
    }

    private static void assertEquals(int actual, int expected, String label) {
        if (actual != expected) {
            fail(label + ": expected " + expected + " but was " + actual);
        }
        pass(label);
    }

    private static void assertTrue(boolean condition, String label) {
        if (!condition) {
            fail(label);
        }
        pass(label);
    }

    private static void assertFalse(boolean condition, String label) {
        assertTrue(!condition, label);
    }

    private static void pass(String label) {
        passed++;
    }

    private static void fail(String message) {
        throw new AssertionError(message);
    }
}
