# ParkingLot

## What this repo is
- A Java implementation of a parking lot management system with multi-floor support.
- Main file: `ParkingLot.java` - a complete simulation supporting different vehicle types (Bike, Car, Truck) with appropriate spot requirements.

## Big-picture intent (what ParkingLot.java implements)
- **ParkingLot** is a singleton class managing multiple floors of parking spots.
- Supports parking/unparking vehicles using tickets with entry/exit times.
- Vehicle types (Bike/Car/Truck) require minimum spot sizes (Small/Medium/Large).
- Thread-safe parking operations via synchronization.
- Demonstrates object-oriented design: inheritance (Vehicle hierarchy), composition (Lot→Floor→Spot), factory pattern (VehiclFactory).

## Project conventions & patterns
- This repo uses raw Java source files (no Maven/Gradle). Compilation is done via `javac`.
- Public classes live in matching `.java` files.
- Enums for type safety (VehicleType, SpotType).
- Singleton pattern for ParkingLot instance.
- Factory pattern for vehicle creation.
- Comprehensive JavaDoc comments throughout.
- Avoid adding external libraries; rely on JDK APIs only.

## Build / run (developer workflow)
1. Compile: `javac ParkingLot.java`
2. Run: `java ParkingLot`

> Note: There are no automated tests or CI config in this repo currently.

## When editing
- ParkingLot.java is feature-complete; focus on extensions like billing, GUI, or persistence.
- Maintain existing patterns: singleton, factory, composition.
- Update main() for new demonstrations.
- Keep JavaDoc comments current when modifying methods/classes.

---

