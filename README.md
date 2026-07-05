# Jaikin

Step-by-step animation of [Chaikin's corner-cutting algorithm](https://www.cs.unc.edu/~dm/UNC/COMP258/LECTURES/Chaikins-Algorithm.pdf) on a canvas.

## Requirements

- Java 11 or newer (uses `List.of()`)

## Build & Run

```bash
cd src
javac *.java
java Main
```

## Tests

```bash
cd src
javac *.java
java ChaikinTest
```

Expected output: `All N tests passed.`

## Controls

| Key / Mouse | Action |
|-------------|--------|
| **Left-click** (empty area) | Place a control point |
| **Left-click + drag** (on a point) | Move a control point in real time |
| **Enter** | Start Chaikin animation (3+ points), show line (2 points), or show reminder (0 points) |
| **C** | Clear all points and reset |
| **Escape** | Close the window |

## Behaviour

- **0 points + Enter** — nothing happens; a reminder message is shown. You can still place points.
- **1 point + Enter** — only the point is shown; no animation.
- **2 points + Enter** — a straight line is drawn between them; no animation.
- **3+ points + Enter** — animation cycles through steps 0–7, then restarts.
