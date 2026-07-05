# Audit Walkthrough (C9)

Sign-off for every item in [audit.md](./audit.md). Automated checks run via `java ChaikinTest`; UI items verified manually.

## Functional

### Execute the program using the command `java Main`

**Does the program compile and run without any warnings?**

- `cd src && javac -Xlint:all *.java` — no warnings
- `java Main` — window opens, no console errors

### Set one or more control points by left-clicking on the canvas

**Does the program allow you to mark these control points?**

- Left-click on empty canvas area → `AppState.addControlPoint()` stores coordinates
- Each click adds a white filled dot with a ring (`CanvasPanel.drawControlPoints`)

### Check if the program draws a small circle around the control points

**Does the program draw a small circle around the control points?**

- `RING_RADIUS = 8` white outline drawn around each point (audit: circle for identification)

### Set three or more control points and press `Enter`

**Does Chaikin's algorithm start the animation?**

- `ChaikinAnimationController.start()` generates 7 steps and starts `javax.swing.Timer`
- Green polyline updates every 500 ms through steps 0→7

### Press `Escape` to exit

**Does the program exit without any errors?**

- `CanvasPanel.onEscape()` disposes the `JFrame`; JVM exits cleanly

### One control point + `Enter`

**Is only the selected control point visible, and nothing else changes?**

- Controller returns early for `< 3` points; no timer started
- Only the control-point circle is drawn; no green curve

### Two control points + `Enter`

**Is only a straight line drawn between the two control points?**

- `twoPointLineActive` flag set on Enter
- `getCurrentPoints()` returns the two control points; green line segment drawn between them
- No step cycling

### Three or more control points + `Enter`

**Does the animation complete 7 steps before restarting?**

- `Chaikin.generateSteps(points, 7)` produces steps 0–7 (8 frames)
- `ChaikinAnimator.nextStep()` wraps from step 7 back to step 0
- Verified in `ChaikinTest.testAnimatorCyclesAfterStepSeven`

### Three or more points, animate, then `Escape`

**Does the program exit without any errors?**

- Same clean dispose path as above; no exception from running timer

### `Enter` with no points selected

**Does the program continue without any errors?**

- `onEnter()` sets `showReminder = true` and returns; no crash

### Place points after empty `Enter` without restart

**Does the program allow placement of points without needing to restart?**

- Left-click still calls `addControlPoint()`; reminder clears on first click

## Bonus Features

### Reminder message on empty `Enter`

**+Is a message displayed reminding you to add points?**

- `CanvasPanel.drawReminder()` — *"Draw at least one point (left-click), then press Enter."*

### Clear screen without restart

**+Can you clear the screen and add new control points?**

- Press **`C`** → `controller.stop()`, `state.clear()`, repaint
- Canvas is empty; new points can be placed immediately

### Real-time point dragging

**+Is it possible to drag control points and observe a new curve?**

- Click on existing point (hit test within ring radius) → drag updates position
- During animation, `onControlPointsChanged()` calls `animator.regenerate()` for live curve update
- With 2 points after Enter, the straight line follows the drag

## Automated test suite

```bash
cd src
javac *.java
java ChaikinTest
```

Covers: Chaikin `refineOnce` edge cases, known 3-point output, endpoint preservation, `generateSteps` structure, animator cycle/regenerate/reset.
