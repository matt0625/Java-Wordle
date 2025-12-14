# Java Wordle & Entropy-Based Solver

This project is a high-performance implementation of the Wordle game built in Java. It features a custom graphical user interface and a sophisticated AI solver that utilizes Information Theory to suggest mathematically optimal moves.

## Project Overview

The application provides a "vanilla" Wordle experience where players attempt to identify a hidden five-letter word within six attempts. To assist players, the project includes an integrated solver. Rather than providing simple suggestions, this solver identifies the "best" possible guess to narrow down the pool of potential secret words as efficiently as possible.

## Technical Approach: Entropy vs. Frequency Analysis

The intelligence of the solver is based on **Information Entropy** (Shannon Entropy) rather than a basic letter frequency heuristic.

* **Frequency Analysis:** This common approach ranks words based on how frequently their letters appear in the remaining word pool. While useful, it often fails to provide enough "clarification" when many words share similar common letters.
* **Entropy-Based Analysis:** This approach treats Wordle as an information optimization problem. For every potential guess in the dictionary, the algorithm simulates every possible secret word remaining and calculates how much "information" (measured in bits) that guess provides on average.

The solver prioritizes words that distribute the remaining candidates into the most even "buckets" of feedback, ensuring that no matter what colors the game returns, the search space is minimized.

### The Entropy Formula
The solver calculates the entropy ($H$) of a candidate word by evaluating the probability ($P$) of every possible feedback pattern $i$:

$$H = \sum_{i=1}^{n} P_i \log_2\left(\frac{1}{P_i}\right)$$



## Software Engineering Practices

A major focus of this project was the application of rigorous software engineering principles to ensure the code is scalable and maintainable:

* **Model-View-Controller (MVC) Separation:** The game logic (`WordleGame`), the solver service (`WordleSolver`), and the interface (`GUI`) are strictly decoupled. This allows the solver's mathematical engine to be updated or tested independently of the user interface.
* **Concurrency & Performance:** Calculating entropy for a dictionary of ~12,000 words against a candidate pool of ~2,300 involves approximately 24 million simulations. To handle this, I utilized Java's **Parallel Streams** to distribute the workload across all available CPU cores.
* **Thread Management:** To prevent the GUI from freezing during heavy calculations, the solver is executed via a `SwingWorker` thread. This ensures the interface remains responsive while the AI "thinks."
* **Unit Testing:** I implemented a suite of JUnit tests focusing on:
    * **Filtering Logic:** Ensuring that green, yellow, and grey constraints—especially with duplicate letters—correctly prune the candidate list.
    * **Simulator Accuracy:** Verifying the base-3 encoding of Wordle feedback patterns (mapping to indices 0–242).
    * **Determinism:** Ensuring parallelized calculations remain consistent across runs.

## Key Features

* **Vanilla Gameplay:** Standard Wordle rules including input validation and color-coded feedback.
* **Intelligent Hint System:** A "Get Hint" button that invokes the entropy engine to recommend the most informative next guess.
* **Optimized Startup:** The solver uses a hardcoded optimal first move ("CRANE") to provide an instantaneous first hint, skipping the initial heavy calculation while still being mathematically sound.
* **Robust Duplicate Logic:** Accurate handling of complex letter-matching scenarios (e.g., when a guess contains more instances of a letter than the target word).

## How to Run

### Prerequisites
* Java Development Kit (JDK) 17 or higher.
* `wordles.json` and `nonwordles.json` files must be present in the project root directory.

### Build and Run
1.  Clone the repository.
2.  Compile the source files:
    ```bash
    javac -d bin src/org/example/*.java
    ```
3.  Launch the application:
    ```bash
    java -cp bin org.example.GUI
    ```

*Note: If you are contributing to this project and encounter a "rejected push" error, it is likely because the remote contains work you do not have locally. Use `git pull --rebase` to synchronize before pushing your changes.*
