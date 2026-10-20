# ⚡ QuizForge — JDBC Quiz Application

<p align="center">
  <strong>A lightweight, interactive quiz platform powered by Core Java, JDBC & SQLite.</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-17%2B-orange?logo=openjdk" alt="Java">
  <img src="https://img.shields.io/badge/JDBC-Database%20Connectivity-blue" alt="JDBC">
  <img src="https://img.shields.io/badge/SQLite-Database-003B57?logo=sqlite&logoColor=white" alt="SQLite">
  <img src="https://img.shields.io/badge/Frontend-HTML%20%7C%20CSS%20%7C%20JS-E34F26?logo=html5&logoColor=white" alt="Frontend">
  <img src="https://img.shields.io/badge/Build-Java%20CLI-success" alt="Build">
</p>

## 🚀 Overview

QuizForge is a browser-based quiz application that demonstrates how **Java connects to a relational database using JDBC**, executes SQL queries, and serves dynamic quiz data through a lightweight HTTP server.

## ✨ Key Features

- 🎯 Interactive quiz with timer, progress tracking and score review
- 🔌 JDBC-based database connectivity using `DriverManager`
- 🗄️ SQLite integration for persistent question storage
- 🔄 SQL CRUD operations — Create, Read, Update and Delete
- ⚙️ Java backend APIs for fetching questions and evaluating answers
- 🖥️ Responsive, dark-themed frontend built with HTML, CSS and JavaScript

## 🛠️ Tech Stack

| Layer | Technologies |
|---|---|
| Backend | Core Java, JDBC, `HttpServer` |
| Database | SQLite, SQL |
| Frontend | HTML5, CSS3, JavaScript |
| Connectivity | SQLite JDBC Driver |

## ⚡ Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/kanaseshravani07-sudo/Quiz_App_Java.git
cd Quiz_App_Java
```

### 2. Check prerequisites

- Java JDK installed
- SQLite JDBC JAR available in the `lib/` folder
- Project database available at `database/quiz.db`

Verify Java:

```bash
java -version
javac -version
```

### 3. Compile the application

Run from the project root in Windows CMD:

```bat
javac --add-modules jdk.httpserver -cp "src;lib\sqlite-jdbc-3.53.4.0.jar" -d src src\QuizServer.java
```

### 4. Launch QuizForge

```bat
java --add-modules jdk.httpserver -cp "src;lib\sqlite-jdbc-3.53.4.0.jar" QuizServer
```

Open **http://localhost:8080** in your browser.

> Keep the terminal running while using the application. If port `8080` is already occupied, stop the other server or configure an available port.

## 🗃️ Database & SQL CRUD

Open `database/quiz.db` using [DB Browser for SQLite](https://sqlitebrowser.org/), then use the **Execute SQL** tab.

```sql
-- CREATE: Add a question
INSERT INTO questions
(category, question_text, option_a, option_b,
 option_c, option_d, correct_option)
VALUES
('Java Basics', 'Which keyword supports inheritance?',
 'this', 'extends', 'super', 'static', 'B');

-- READ: Retrieve questions
SELECT * FROM questions;

-- UPDATE: Modify a question
UPDATE questions
SET question_text = 'Which keyword enables inheritance?'
WHERE id = 1;

-- DELETE: Remove a question
DELETE FROM questions WHERE id = 1;
```

*Use an existing question ID for UPDATE/DELETE. Save changes in DB Browser when prompted.*

## 🧠 What This Project Demonstrates

`DriverManager` • `Connection` • `PreparedStatement` • SQL CRUD • Exception Handling • HTTP Request Handling • JSON • Frontend–Backend Integration

## 👨‍💻 Author

**Shravani Kanase**

*Building practical Java applications and strengthening backend development fundamentals.*

---

<p align="center">
  <i>Learn by building. Connect with code. Query with confidence.</i>
</p>
