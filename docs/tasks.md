# Jaikin — Task Board

> Chaikin's algorithm step-by-step animation on a canvas.
> Team of 3. Entry point: `java Main`. See [instructions.md](./instructions.md) and [audit.md](./audit.md).

## Legend

| Status | Meaning |
|--------|---------|
| 🔲 To Do | Not started |
| 🟡 In Progress | Being worked on |
| 🔵 In Review | PR open / awaiting review |
| ✅ Done | Merged & verified against audit |
| ⛔ Blocked | Waiting on a dependency |

**Priority:** P0 = must-have (audit fails without it) · P1 = expected · P2 = bonus

---

## Roles

- **Dev A — Canvas & Input** — window, rendering surface, mouse/keyboard events, drawing control points.
- **Dev B — Algorithm & Animation** — Chaikin's algorithm, step engine, animation loop, edge cases (0/1/2 points).
- **Dev C — App Shell, State & Bonus** — main entry, app state machine, glue, clear/drag bonus features, docs & audit pass.

> Everyone: keep `Main` runnable at all times. No warnings on compile/run (audit item).

---

## Milestones

| # | Milestone | Depends on | Target | Status |
|---|-----------|------------|--------|--------|
| M1 | Skeleton runs: window opens, `Escape` closes it | — | Day 1 | ✅ |
| M2 | User can place points; circles render | M1 | Day 2 | ✅ |
| M3 | Chaikin engine produces correct step data | M1 | Day 3 | ✅ |
| M4 | `Enter` animates through 7 steps and restarts | M2, M3 | Day 4 | ✅ |
| M5 | Edge cases (0/1/2 points) handled per audit | M4 | Day 5 | ✅ |
| M6 | Bonus: clear screen, real-time drag | M5 | Day 6 | ✅ |

---

## Dev A — Canvas & Input

| ID | Task | Priority | Status | Notes |
|----|------|----------|--------|-------|
| A1 | Set up project skeleton + `Main` entry point | P0 | ✅ | `src/`, Swing chosen; `javac *.java && java Main` |
| A2 | Open a window / canvas surface | P0 | ✅ | `CanvasPanel` (JPanel), 900×640 |
| A3 | Capture left-click, store as control point | P0 | ✅ | Writes to `AppState.controlPoints()` |
| A4 | Render each control point as a small circle | P0 | ✅ | Filled dot + ring around each point |
| A5 | Wire `Escape` → close window cleanly | P0 | ✅ | Key binding disposes window; JVM exits clean |
| A6 | Wire `Enter` → fire "start animation" event | P0 | ✅ | Calls `AnimationController.start()` |
| A7 | Redraw loop / repaint on state change | P0 | ✅ | `repaint()` on each point; curve drawn via controller |

## Dev B — Algorithm & Animation

| ID | Task | Priority | Status | Notes |
|----|------|----------|--------|-------|
| B1 | Implement Chaikin's algorithm (one iteration) | P0 | ✅ | `Chaikin.refineOnce()` |
| B2 | Generate all 7 step results from control points | P0 | ✅ | `Chaikin.generateSteps()` |
| B3 | Animation loop cycling steps 0→7 then restart | P0 | ✅ | `ChaikinAnimator` + `javax.swing.Timer` |
| B4 | Edge case: 1 point → show point, no cycling | P0 | ✅ | Early return in `ChaikinAnimationController.start()` |
| B5 | Edge case: 2 points → straight line, no cycling | P0 | ✅ | `twoPointLineActive` flag after Enter |
| B6 | Edge case: 0 points + `Enter` → no-op | P0 | ✅ | No crash; reminder shown |
| B7 | Render intermediate curves each frame | P0 | ✅ | `CanvasPanel` draws `getCurrentPoints()` as green polyline |
| B8 | Unit tests for Chaikin correctness | P1 | ✅ | `ChaikinTest` — run with `java ChaikinTest` |

## Dev C — App Shell, State & Bonus

| ID | Task | Priority | Status | Notes |
|----|------|----------|--------|-------|
| C1 | Define shared app state (points, mode, step) | P0 | ✅ | `AppState` |
| C2 | State machine: IDLE ↔ ANIMATING | P0 | ✅ | `AppState.Mode` + controller transitions |
| C3 | Integrate input (A) + engine (B) end-to-end | P0 | ✅ | Wired in `Main` |
| C4 | Ensure clean compile, zero warnings | P0 | ✅ | `javac -Xlint:all *.java` clean |
| C5 | Bonus: "reminder" message on empty `Enter` | P2 | ✅ | Shown in `CanvasPanel` |
| C6 | Bonus: clear screen → place new points | P2 | ✅ | `C` key clears state + stops animation |
| C7 | Bonus: real-time drag of control points | P2 | ✅ | Click-drag on points; regenerates curve while animating |
| C8 | README (build + run instructions) | P1 | ✅ | Root `README.md` |
| C9 | Final walkthrough of every audit question | P0 | ✅ | [audit-walkthrough.md](./audit-walkthrough.md) |

---

## Audit checklist (final gate)

Every box must pass before submission — see [audit.md](./audit.md).

- [x] Program compiles & runs without warnings
- [x] Left-click sets control points
- [x] Small circle drawn around each control point
- [x] 3+ points + `Enter` → animation starts
- [x] `Escape` exits without errors
- [x] 1 point → only that point shown, nothing cycles
- [x] 2 points → straight line drawn
- [x] 3+ points → animation completes 7 steps, then restarts
- [x] `Enter` with no points → no crash, can still draw after
- [x] +Reminder message on empty `Enter`
- [x] +Clear screen without restarting (`C` key)
- [x] +Real-time point dragging

---

## Open decisions (resolved)

1. **Library** — Swing (`javax.swing`)
2. **Step model** — precompute all 7 steps up front (`Chaikin.generateSteps`)
3. **Shared-state contract** — `AppState` + `AnimationController` interface
