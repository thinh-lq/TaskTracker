# Task Tracker CLI

A simple command-line task tracker built with Java. Tasks are stored locally in a JSON file and can be created, updated, deleted, listed, and marked by status.

## Features

* Add a new task
* Update a task
* Delete a task
* Mark a task as `in-progress`
* Mark a task as `done`
* List all tasks
* List tasks by status:

  * `todo`
  * `in-progress`
  * `done`
* Store tasks in a local `task.json` file
* Automatically track `createdAt` and `updatedAt`

## Requirements

* Java JDK 8 or later
* Command Line / PowerShell

No external libraries are required.

## Project Structure

```text
TaskTracker/
├── src/
│   └── TaskTracker.java
├── task.json
├── .gitignore
└── README.md
```

The project contains three main classes:

* `TaskTracker` — handles the CLI and application entry point.
* `Helper` — handles task operations and JSON file operations.
* `Task` — represents a task and its properties.

## Task Properties

Each task contains:

```text
id
description
status
createdAt
updatedAt
```

The available statuses are:

```text
todo
in-progress
done
```

## Compile

From the project root directory:

```bash
javac TaskTracker.java
```

## Usage

Compile the project:

```bash
javac TaskTracker.java
```

Run the application using:

```text
java TaskTracker <command> [arguments]
```

### Commands

**Add a task**

```text
java TaskTracker add <description>
```

Example:

```bash
java TaskTracker add "Learn Java"
```

**Update a task**

```text
java TaskTracker update <id> <description>
```

Example:

```bash
java TaskTracker update 1 "Learn Java and OOP"
```

**Delete a task**

```text
java TaskTracker delete <id>
```

Example:

```bash
java TaskTracker delete 1
```

**Mark a task as in-progress**

```text
java TaskTracker mark-in-progress <id>
```

Example:

```bash
java TaskTracker mark-in-progress 1
```

**Mark a task as done**

```text
java TaskTracker mark-done <id>
```

Example:

```bash
java TaskTracker mark-done 1
```

**List tasks**

```text
java TaskTracker list [status]
```

If no `[status]` is specified, all tasks are listed.

Where `[status]` can be:

```text
todo
in-progress
done
```


Examples:

```bash
java TaskTracker list
java TaskTracker list todo
java TaskTracker list in-progress
java TaskTracker list done
```


## Data Storage

Tasks are stored in `task.json` in the project directory.

Example:

```json
[
    {
        "id": 1,
        "description": "Learn Java and OOP",
        "status": "done",
        "createdAt": "2026-09-15T10:30:00",
        "updatedAt": "2026-09-16T14:20:00"
    }
]
```

The application automatically creates `task.json` if it does not exist.

## Technologies

* Java
* Java NIO (`java.nio.file`)
* Java Time API (`java.time`)
* JSON file storage
* Command-line interface

## Project Reference

This project was built as a practice project based on the [Task Tracker CLI](https://roadmap.sh/projects/task-tracker) project from roadmap.sh.
