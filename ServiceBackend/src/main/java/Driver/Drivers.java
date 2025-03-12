package Driver;

import com.google.gson.annotations.SerializedName;

public class Drivers {
    private int id;

    @SerializedName("dName")
    private String dName;

    @SerializedName("phone")
    private String phone;

    @SerializedName("license_number")
    private String licenseNumber;

    @SerializedName("nic")
    private String nic;

    @SerializedName("dstatus")
    private String dstatus;

    // Constructors
    public Drivers() {}

    public Drivers(int id, String dName, String phone, String licenseNumber, String nic, String dstatus) {
        this.id = id;
        this.dName = dName;
        this.phone = phone;
        this.licenseNumber = licenseNumber;
        this.nic = nic;
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

    public String getDstatus() {
        return dstatus;
    }

    public void setDstatus(String dstatus) {
        this.dstatus = dstatus;
    }
}