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

| # | Milestone | Depends on | Target |
|---|-----------|------------|--------|
| M1 | Skeleton runs: window opens, `Escape` closes it | — | Day 1 |
| M2 | User can place points; circles render | M1 | Day 2 |
| M3 | Chaikin engine produces correct step data | M1 | Day 3 |
| M4 | `Enter` animates through 7 steps and restarts | M2, M3 | Day 4 |
| M5 | Edge cases (0/1/2 points) handled per audit | M4 | Day 5 |
| M6 | Bonus: clear screen, real-time drag | M5 | Day 6 |

---

## Dev A — Canvas & Input

| ID | Task | Priority | Status | Notes |
|----|------|----------|--------|-------|
| A1 | Set up project skeleton + `Main` entry point | P0 | 🔲 | Coordinate structure with Dev C |
| A2 | Open a window / canvas surface | P0 | 🔲 | Pick lib (Swing/JavaFX/other) — decide with team |
| A3 | Capture left-click, store as control point | P0 | 🔲 | Expose points to shared state (Dev C) |
| A4 | Render each control point as a small circle | P0 | 🔲 | Audit: "small circle around the control points" |
| A5 | Wire `Escape` → close window cleanly | P0 | 🔲 | No errors on exit |
| A6 | Wire `Enter` → fire "start animation" event | P0 | 🔲 | Handoff to Dev B's engine |
| A7 | Redraw loop / repaint on state change | P0 | 🔲 | Shared with Dev B animation |

## Dev B — Algorithm & Animation

| ID | Task | Priority | Status | Notes |
|----|------|----------|--------|-------|
| B1 | Implement Chaikin's algorithm (one iteration) | P0 | 🔲 | Pure function: points → refined points |
| B2 | Generate all 7 step results from control points | P0 | 🔲 | Precompute or step-on-demand |
| B3 | Animation loop cycling steps 0→7 then restart | P0 | 🔲 | Audit: "complete 7 steps before restarting" |
| B4 | Edge case: 1 point → show point, no cycling | P0 | 🔲 | Audit item |
| B5 | Edge case: 2 points → straight line, no cycling | P0 | 🔲 | Audit item |
| B6 | Edge case: 0 points + `Enter` → no-op | P0 | 🔲 | Must not crash; can still draw after |
| B7 | Render intermediate curves each frame | P0 | 🔲 | With Dev A's repaint loop |
| B8 | Unit tests for Chaikin correctness | P1 | 🔲 | Verify against known outputs |

## Dev C — App Shell, State & Bonus

| ID | Task | Priority | Status | Notes |
|----|------|----------|--------|-------|
| C1 | Define shared app state (points, mode, step) | P0 | 🔲 | Contract for A & B |
| C2 | State machine: IDLE ↔ ANIMATING | P0 | 🔲 | Enter starts, restart resets |
| C3 | Integrate input (A) + engine (B) end-to-end | P0 | 🔲 | Owns the wiring |
| C4 | Ensure clean compile, zero warnings | P0 | 🔲 | Audit gate |
| C5 | Bonus: "reminder" message on empty `Enter` | P2 | 🔲 | +bonus |
| C6 | Bonus: clear screen → place new points | P2 | 🔲 | +bonus |
| C7 | Bonus: real-time drag of control points | P2 | 🔲 | +bonus, regenerates curve |
| C8 | README (build + run instructions) | P1 | 🔲 | How to `java Main` |
| C9 | Final walkthrough of every audit question | P0 | 🔲 | Sign-off before submission |

---

## Audit checklist (final gate)

Every box must pass before submission — see [audit.md](./audit.md).

- [ ] Program compiles & runs without warnings
- [ ] Left-click sets control points
- [ ] Small circle drawn around each control point
- [ ] 3+ points + `Enter` → animation starts
- [ ] `Escape` exits without errors
- [ ] 1 point → only that point shown, nothing cycles
- [ ] 2 points → straight line drawn
- [ ] 3+ points → animation completes 7 steps, then restarts
- [ ] `Enter` with no points → no crash, can still draw after
- [ ] +Reminder message on empty `Enter`
- [ ] +Clear screen without restarting
- [ ] +Real-time point dragging

---

## Open decisions (resolve early, together)

1. **Library** — Swing, JavaFX, or something else? (audit says free choice; entry must stay `java Main`)
2. **Step model** — precompute all 7 steps up front vs. compute one step per frame.
3. **Shared-state contract** — how A's input and B's engine talk (C owns this).
