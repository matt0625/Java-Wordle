# Java Wordle

A robust implementation of the popular Wordle game, built using **Java** and **Swing**. 

While fully playable, the primary objective of this project was to demonstrate software engineering **best practices**, **clean architecture**, and comprehensive **unit testing**.

## 🎯 Project Goals
* **Best Practices:** Focus on modular design, clean code principles, and separation of concerns.
* **Unit Testing:** High test coverage using JUnit to ensure game logic reliability.
* **GUI:** Responsive and intuitive user interface built with the Java Swing library.

## 🛠 Tech Stack
* **Language:** Java (JDK 17+)
* **UI Framework:** Swing (AWT)
* **Build Tool:** Maven
* **Testing:** JUnit 5 (Jupiter)

## ✨ Features
* Classic Wordle mechanics (6 attempts, color-coded feedback).
* Random word selection from a curated dictionary.
* Input validation (checks for valid words and character limits).
* Visual feedback system:
    * 🟩 **Green:** Correct letter, correct spot.
    * 🟨 **Yellow:** Correct letter, wrong spot.
    * ⬜ **Grey:** Letter not in word.

## 🧪 Testing
This project emphasizes reliability through testing. Key game logic (such as word validation and color-coding algorithms) is covered by unit tests.

To run the tests via the terminal:
```bash
mvn test
