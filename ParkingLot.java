
import java.util.*;
import java.time.*;

/**
 * Parking Lot Management System
 * This program simulates a parking lot with multiple floors and spots,
 * supporting different vehicle types (Bike, Car, Truck) with appropriate spot requirements.
 * Vehicles can be parked and unparked using tickets.
 *
 * UML Class Diagram:
 *
 * +-------------------+     +-------------------+
 * |   VehicleType     |     |    SpotType       |
 * |-------------------|     |-------------------|
 * | BIKE(SMALL)       |     | SMALL             |
 * | CAR(MEDIUM)       |     | MEDIUM            |
 * | TRUCK(LARGE)      |     | LARGE             |
 * +-------------------+     +-------------------+
 *
 * +-------------------+        +-------------------+
 * |     Vehicle       |        |  VehiclFactory    |
 * |-------------------|        |-------------------|
 * | - type: VehicleType|       | + createVehicle() |
 * | - licensePlate: String|    +-------------------+
 * |-------------------|                |
 * | + getType()       |                | uses
 * | + getLicensePlate()|               v
 * +-------------------+         +----------------------+
 *           ^                   |   ParkingLot         |
 *           |                   |-------------------   |
 *           |                   | - instance: ParkingLot|
 *     +-----+------------+      | - floors: List<ParkingFloor>|
 *     |        |         |      | - activeTickets: Map<String, ParkingTicket>|
 * +---+---+ +---+---+ +--+--+   |----------------------|
 * | Bike  | | Car  | | Truck|   | + getInstance()      |
 * +-------+ +------+ +------+   | + parkVehicle()      |
 *                               | + unparkVehicle()    |
 *                               +----------------------+
 *                                      | *
 *                                      | composition
 *                                      v
 * +-------------------+     +-------------------+
 * |  ParkingFloor     |     |   ParkingSpot     |
 * |-------------------|     |-------------------|
 * | - floorNumber: int|     | - spotId: String  |
 * | - spots: List<ParkingSpot>| | - spotType: SpotType|
 * |-------------------|     | - isOccupied: boolean|
 * | + getFloorNumber()|     | - parkedVehicle: Vehicle|
 * | + getSpots()      |     |-------------------|
 * | + findAvailableSpot()|   | + canFitVehicle() |
 * +-------------------+     | + parkVehicle()   |
 *           | *             | + removeVehicle() |
 *           | composition    +-------------------+
 *           v
 * +-------------------+
 * |  ParkingTicket    |
 * |-------------------|
 * | - ticketId: String|
 * | - vehicle: Vehicle|
 * | - spot: ParkingSpot|
 * | - entryTime: LocalDateTime|
 * | - exitTime: LocalDateTime |
 * |-------------------|
 * | + getTicketId()   |
 * | + getVehicle()    |
 * | + getSpot()       |
 * | + getEntryTime()  |
 * | + getExitTime()   |
 * | + setExitTime()   |
 * +-------------------+
 *
 * Relationships:
 * - Inheritance: Bike, Car, Truck extend Vehicle
 * - Association: ParkingSpot has-a Vehicle (parkedVehicle)
 * - Composition: ParkingFloor has-many ParkingSpot (spots list)
 * - Association: ParkingTicket has-a Vehicle and has-a ParkingSpot
 * - Composition: ParkingLot has-many ParkingFloor (floors list) and has-many ParkingTicket (activeTickets map)
 * - Dependency: ParkingLot depends on VehiclFactory for vehicle creation
 */

/**
 * Enum representing different types of vehicles, each mapped to a minimum spot type they require.
 */
enum VehicleType { 
    BIKE(SpotType.SMALL), 
    CAR(SpotType.MEDIUM), 
    TRUCK(SpotType.LARGE);
    private final SpotType minSpotType;
    VehicleType(SpotType minSpotType) {
        this.minSpotType = minSpotType;
    }
    public SpotType getMinSpotType() {
        return minSpotType;
    }
}

/**
 * Enum representing the sizes of parking spots.
 */
enum SpotType    { SMALL, MEDIUM, LARGE }

/**
 * Abstract base class for all vehicles in the parking system.
 * Each vehicle has a type and license plate.
 */
abstract class Vehicle{
    private VehicleType type;
    private String licensePlate;
    
    public Vehicle(VehicleType type, String licensePlate) {
        this.type = type;
        this.licensePlate = licensePlate;
    }
    public VehicleType getType() {
        return type;
    }
    public String getLicensePlate() {
        return licensePlate;
    }
}

/**
 * Represents a Bike vehicle.
 */
class Bike extends Vehicle {
    public Bike(String licensePlate) {
        super(VehicleType.BIKE, licensePlate);
    }
}

/**
 * Represents a Car vehicle.
 */
class Car extends Vehicle {
    public Car(String licensePlate) {
        super(VehicleType.CAR, licensePlate);
    }
}

/**
 * Represents a Truck vehicle.
 */
class Truck extends Vehicle{
    public Truck(String licensePlate){
        super(VehicleType.TRUCK, licensePlate);
    }
}

/**
 * Factory class for creating Vehicle instances based on VehicleType.
 */
class VehiclFactory {
    public static Vehicle createVehicle(VehicleType type, String licensePlate) {
        switch (type) {
            case BIKE:
                return new Bike(licensePlate);
            case CAR:
                return new Car(licensePlate);
            case TRUCK:
                return new Truck(licensePlate);
            default:
                throw new IllegalArgumentException("Invalid vehicle type");
        }
    }
}

/**
 * Represents a parking spot with an ID, type, and occupancy status.
 * Handles parking and removing vehicles.
 */
class ParkingSpot{
    private String spotId;
    private SpotType spotType;
    private boolean isOccupied;
    private Vehicle parkedVehicle;
    
    /**
     * Constructor for ParkingSpot.
     * @param spotId Unique identifier for the spot.
     * @param spotType The size type of the spot.
     */
    public ParkingSpot(String spotId, SpotType spotType) {
        this.spotId = spotId;
        this.spotType = spotType;
        this.isOccupied = false;

    }
    
    public String getSpotId() {
        return spotId;
    }
    public SpotType getSpotType() {
        return spotType;
    }
    public boolean isOccupied() {
        return isOccupied;
    }
    public Vehicle getParkedVehicle() {
        return parkedVehicle;
    }
    
    /**
     * Checks if the given vehicle can fit in this spot based on spot type compatibility.
     * @param vehicle The vehicle to check.
     * @return true if the vehicle can fit, false otherwise.
     */
    public boolean canFitVehicle(Vehicle vehicle) {
        return vehicle.getType().getMinSpotType().ordinal() <= this.spotType.ordinal();
    }
    
    /**
     * Parks the given vehicle in this spot if possible.
     * @param vehicle The vehicle to park.
     * @throws IllegalStateException if the spot is occupied or vehicle cannot fit.
     */
    public void parkVehicle(Vehicle vehicle) {
        if (canFitVehicle(vehicle) && !isOccupied) {
            this.parkedVehicle = vehicle;
            this.isOccupied = true;
        } else {
            throw new IllegalStateException("Cannot park vehicle in this spot");
        }
    }
    
    /**
     * Removes the parked vehicle from this spot.
     */
    public void removeVehicle() {
        this.parkedVehicle = null;
        this.isOccupied = false;
    }


}

/**
 * Represents a floor in the parking lot, containing multiple parking spots.
 */
class ParkingFloor{
    private int floorNumber;
    private List<ParkingSpot> spots;
    
    /**
     * Constructor for ParkingFloor.
     * @param floorNumber The number of the floor.
     * @param spots List of parking spots on this floor.
     */
    public ParkingFloor(int floorNumber, List<ParkingSpot> spots) {
        this.floorNumber = floorNumber;
        this.spots = spots;
    }
    
    public int getFloorNumber() {
        return floorNumber;
    }
    public List<ParkingSpot> getSpots() {
        return spots;
    }
    
    /**
     * Finds an available spot on this floor that can accommodate the given vehicle.
     * @param v The vehicle to find a spot for.
     * @return The available ParkingSpot, or null if none found.
     */
    public ParkingSpot findAvailableSpot(Vehicle v) {
        for (ParkingSpot spot : spots) {
            if (!spot.isOccupied() && spot.canFitVehicle(v)) {
                return spot;
            }
        }
        return null; // No available spot found
    }

}

class ParkingTicket {
    private String ticketId;
    private Vehicle vehicle;
    private ParkingSpot spot;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    
    /**
     * Constructor for ParkingTicket.
     * Generates a unique ticket ID and sets entry time to current time.
     * @param vehicle The parked vehicle.
     * @param spot The parking spot.
     */
    public ParkingTicket(Vehicle vehicle, ParkingSpot spot) {
        this.ticketId = UUID.randomUUID().toString();
        this.vehicle = vehicle;
        this.spot = spot;
        this.entryTime = LocalDateTime.now();
        this.exitTime = null; // exitTime starts as null
    }
    
    public String getTicketId() {
        return ticketId;
    }
    
    public Vehicle getVehicle() {
        return vehicle;
    }
    public ParkingSpot getSpot() {
        return spot;
    }
    public LocalDateTime getEntryTime() {
        return entryTime;
    }
    public LocalDateTime getExitTime() {
        return exitTime;
    }
    
    /**
     * Sets the exit time when the vehicle leaves.
     * @param exitTime The time the vehicle exited.
     */
    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }
}

public class ParkingLot{
    private static ParkingLot instance;
    private List<ParkingFloor> ParkingFloor;
    private Map<String, ParkingTicket> activeTickets; // Map of ticketId to ParkingTicket
    
    /**
     * Singleton getInstance method to ensure only one ParkingLot instance.
     * @return The singleton ParkingLot instance.
     */
    public static synchronized ParkingLot getInstance() {
        if (instance == null) {
            instance = new ParkingLot();
        }
        return instance;
    }
    
    /**
     * Private constructor for singleton pattern.
     */
    private ParkingLot() {
    this.ParkingFloor = new ArrayList<>();
    this.activeTickets = new HashMap<>();
}

    /**
     * Parks a vehicle in the first available suitable spot across all floors.
     * Thread-safe due to synchronization.
     * @param vehicle The vehicle to park.
     * @return The ParkingTicket for the parked vehicle.
     * @throws IllegalStateException if no suitable spot is available.
     */
    public synchronized ParkingTicket parkVehicle(Vehicle vehicle) {
        for (ParkingFloor floor : ParkingFloor) {
            ParkingSpot spot = floor.findAvailableSpot(vehicle);
            if (spot != null) {
                spot.parkVehicle(vehicle);
                ParkingTicket ticket = new ParkingTicket(vehicle, spot);
                activeTickets.put(ticket.getTicketId(), ticket);
                return ticket;
            }
        }
        throw new IllegalStateException("No available parking spots for this vehicle");
    }
    
    /**
     * Unparks a vehicle using its ticket ID.
     * @param ticketId The ID of the ticket.
     * @throws IllegalArgumentException if the ticket ID is invalid.
     */
    public void unparkVehicle(String ticketId) {
        ParkingTicket ticket = activeTickets.get(ticketId);
        if (ticket != null) {
            ParkingSpot spot = ticket.getSpot();
            spot.removeVehicle();
            ticket.setExitTime(LocalDateTime.now());
            activeTickets.remove(ticketId);
        } else {
            throw new IllegalArgumentException("Invalid ticket ID");
        }
    }
    
    public List<ParkingFloor> getParkingFloors() {
        return ParkingFloor;
    }
    public Map<String, ParkingTicket> getActiveTickets() {
        return activeTickets;
    }
    
    /**
     * Main method to demonstrate the parking lot functionality.
     * Creates floors and spots, parks vehicles, and unparks one.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        ParkingLot parkingLot = ParkingLot.getInstance();
        // Create some parking floors and spots
        List<ParkingSpot> floor1Spots = Arrays.asList(
            new ParkingSpot("F1S1", SpotType.SMALL),
            new ParkingSpot("F1S2", SpotType.MEDIUM),
            new ParkingSpot("F1S3", SpotType.LARGE)
        );
        List<ParkingSpot> floor2Spots = Arrays.asList(
            new ParkingSpot("F2S1", SpotType.SMALL),
            new ParkingSpot("F2S2", SpotType.MEDIUM),
            new ParkingSpot("F2S3", SpotType.LARGE)
        );
        parkingLot.getParkingFloors().add(new ParkingFloor(1, floor1Spots));
        parkingLot.getParkingFloors().add(new ParkingFloor(2, floor2Spots));

        // Park some vehicles
        Vehicle bike = VehiclFactory.createVehicle(VehicleType.BIKE, "BIKE123");
        Vehicle car = VehiclFactory.createVehicle(VehicleType.CAR, "CAR456");
        Vehicle truck = VehiclFactory.createVehicle(VehicleType.TRUCK, "TRUCK789");
        // Vehicle another = VehiclFactory.createVehicle(null, "another123");

        ParkingTicket bikeTicket = parkingLot.parkVehicle(bike);
        System.out.println("Parked bike with ticket ID: " + bikeTicket.getTicketId());

        ParkingTicket carTicket = parkingLot.parkVehicle(car);
        System.out.println("Parked car with ticket ID: " + carTicket.getTicketId());

        ParkingTicket truckTicket = parkingLot.parkVehicle(truck);
        System.out.println("Parked truck with ticket ID: " + truckTicket.getTicketId());
        // ParkingTicket anotherTicket = parkingLot.parkVehicle(another);
        // System.out.println("Parked another with ticket ID: " + anotherTicket.getTicketId());

        // Unpark a vehicle
        parkingLot.unparkVehicle(carTicket.getTicketId());
        System.out.println("Unparked car with ticket ID: " + carTicket.getTicketId());
    }
}




