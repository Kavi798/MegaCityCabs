package Vehicle;

public class Vehicles {

    private int id;
    private String model;
    private String plateNumber;
    private int capacity;
    private String type;
    private String status;

    // Constructors
    public Vehicles() {
    }

    public Vehicles(int id, String model, String plateNumber, int capacity, String type, String status) {
        this.id = id;
        this.model = model;
        this.plateNumber = plateNumber;
        this.capacity = capacity;
        this.type = type;
        this.status = status;
    }

    // Getters and Setters
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
