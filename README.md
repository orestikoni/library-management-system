# Library Management System

This is a simple desktop Library Management System written in Java (JavaFX). It provides a small GUI for library administrators and users to manage books, categories, reservations, reviews and suggestions.

## Repository layout

- src/ — Java source files (JavaFX UI and application logic)
- bin/ — (optional) compiled classes if present
- *.dat / *.ser files — example serialized data files used by the application (books.dat, users.ser, reservations.ser, etc.)
- .project, .classpath, .settings/ — Eclipse project files (this repository was developed with Eclipse)

## Requirements

- Java 8 (with built-in JavaFX) or
- Java 11+ with OpenJFX installed and available on the module path/classpath.
- An IDE such as Eclipse or IntelliJ IDEA is recommended for easy import and running.

## How to run

Option A — Run in an IDE (recommended)

1. Import the project into your IDE as an existing Java project (Eclipse: File -> Import -> Existing Projects into Workspace).
2. Ensure the project uses a compatible JDK and that JavaFX is available:
   - For JDK 8, JavaFX is included.
   - For JDK 11+, add OpenJFX as a library/module (follow your IDE instructions).
3. Run the main class. The application entry point is LoginMenu in package `library`.

Option B — Command line (JDK 8)

1. From the repository root, compile the sources:

   mkdir -p out
   javac -d out $(find src -name "*.java")

2. Run the application (use the fully-qualified main class name):

   java -cp out library.LoginMenu

Option C — Command line (JDK 11+ with OpenJFX)

1. Install OpenJFX and note the path to its lib folder.
2. Compile the sources:

   mkdir -p out
   javac --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -d out $(find src -name "*.java")

3. Run the application:

   java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -cp out library.LoginMenu

Replace `/path/to/javafx/lib` with the actual path to the JavaFX SDK lib directory on your machine.

## Data files

The repository includes several serialized data files (books.dat, users.ser, borrowedBooks.dat, etc.) that the application reads/writes to. If you delete these files the application will recreate them (empty) on first run, but existing data may be needed to see the example content.

If you want to start fresh, remove or move the `.dat` / `.ser` files before starting the app.

## Notes

- The project uses JavaFX for the UI. If you have issues launching the application, check your Java/JavaFX versions and IDE configuration.
- This project appears to have been developed as an Eclipse project — importing it into Eclipse is the fastest way to get started.

## Contributing

Contributions are welcome. Open an issue or submit a pull request with clear description of changes.

## License

Specify a license by adding a LICENSE file. If you want a suggestion, consider MIT or Apache-2.0.
