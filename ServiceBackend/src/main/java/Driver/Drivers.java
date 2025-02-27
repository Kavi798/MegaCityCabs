package Driver;

public class Drivers {

    private int id;
    private String dName;
    private String phone;
    private String licenseNumber;
    private String nic;
    private int vehicleId;
    private String dstatus;

    // Constructors
    public Drivers() {
    }

    public Drivers(int id, String dName, String phone, String licenseNumber, String nic, int vehicleId, String dstatus) {
        this.id = id;
        this.dName = dName;
        this.phone = phone;
        this.licenseNumber = licenseNumber;
        this.nic = nic;
        this.vehicleId = vehicleId;
        this.dstatus = dstatus;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getdName() {
        return dName;
    }

    public void setdName(String dName) {
        this.dName = dName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getDstatus() {
        return dstatus;
    }

    public void setDstatus(String dstatus) {
        this.dstatus = dstatus;
    }
}