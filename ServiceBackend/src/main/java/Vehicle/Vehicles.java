package Vehicle;

public class Vehicles {

    private int id;
    private String model;
    private String plateNumber;
    private int capacity;
    private Integer driverId; // Optional
    private String driverName; // Optional (for display)
    private String type;
    private String status;

    // ✅ Default Constructor
    public Vehicles() {
    }

    // ✅ Full Constructor with driver
    public Vehicles(int id, String model, String plateNumber, int capacity, Integer driverId, String type, String status, String driverName) {
        this.id = id;
        this.model = model;
        this.plateNumber = plateNumber;
        this.capacity = capacity;
        this.driverId = driverId;
        this.type = type;
        this.status = status;
        this.driverName = driverName;
    }

    // ✅ Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
