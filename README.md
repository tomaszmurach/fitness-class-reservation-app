# Fitness Class Reservation App

A Java Swing desktop application for browsing fitness-class sessions and creating member reservations. Application data is persisted locally through JPA and Hibernate using an embedded H2 database.

## Overview

The application guides a user through selecting a fitness-class session, selecting a club member, reviewing the reservation details, and submitting the reservation. The completed reservation is validated against business rules and saved to the local database.

The supporting domain model covers a wider fitness-club context than the workflow currently exposed through the GUI, including memberships, trainers, employees, facilities, and payments.

## Features

- Browse fitness-class sessions and filter them by name, date, or status.
- Display reservations associated with a selected session.
- Browse and filter club members by member number, surname, or active-pass status.
- Complete a multi-step session, member, review, and confirmation workflow.
- Validate session capacity and membership-plan eligibility.
- Prevent duplicate active reservations for the same member and session.
- Persist reservations within a JPA transaction.
- Initialize local sample data when the database is empty.

## Tech Stack

- Java 23
- Swing / AWT
- Maven
- Jakarta Persistence / JPA
- Hibernate ORM
- H2 Database
- Jakarta Validation / Hibernate Validator

## Architecture

The code is organized into GUI, service, repository, model, and persistence utility layers. During reservation creation, the Swing GUI delegates the use case to the reservation service. The service validates the business rules, invokes the domain model to create the reservation, and uses the repository layer to persist it in a transaction.

## Domain Model

The persisted model demonstrates two `JOINED` inheritance hierarchies: one for people and their member, trainer, and employee roles, and one for active, suspended, and cancelled membership passes. It also uses bidirectional JPA relationships and value collections.

A reservation is modeled as an association entity between a club member and a fitness-class session. Sessions connect class definitions with trainers, rooms, and planning employees, while rooms belong to club branches. Capacity is derived from the session limit and its active reservations. The model additionally represents membership plans, payments, enums and statuses, validation rules, and lifecycle transitions.

These domain concepts support the reservation use case and sample data; not every entity has a complete management workflow in the GUI.

## Reservation Workflow

```text
Select session
    -> Select member
    -> Review reservation
    -> Validate membership, session status, capacity, and duplicates
    -> Persist reservation
    -> Display success or error result
```

Reservations require a qualifying active membership plan, a scheduled session with available capacity, and no existing active reservation for the same member and session.

## Project Structure

```text
src/main/java/mas/s31219/
|-- gui/          Swing window, panels, navigation, and presentation logic
|-- model/        JPA entities, relationships, statuses, and domain rules
|-- repository/   Generic and reservation-focused JPA data access
|-- service/      Reservation orchestration and sample-data initialization
`-- util/         JPA EntityManagerFactory utility

src/main/resources/
`-- META-INF/persistence.xml   JPA and embedded H2 configuration
```

## Running the Project

Prerequisites:

- JDK 23
- Maven
- A graphical desktop environment

The expected Maven build command is:

```shell
mvn clean package
```

To run the application, import the Maven project into IntelliJ IDEA or another Java IDE and run:

```text
mas.s31219.Main
```

The current Maven configuration does not produce an executable JAR. H2 database files are created locally under `data/`, Hibernate handles schema creation and updates, and the application initializes sample data when appropriate. No external database server is required.

## Scope

This university desktop project is centered on the end-to-end fitness-class reservation workflow. Its domain model includes additional fitness-club concepts, while the GUI intentionally does not provide complete administrative CRUD workflows for every entity.
