package fyp.toll_tax_collection;

public class Vehicle {
    public Vehicle(String owner_name, String car_name, String car_number, String car_engine_number, String cnic, String city,String vehicle_type,String province,String registration_date,String owner_phone) {
        this.owner_name = owner_name;
        this.vehicle_name = car_name;
        this.vehicle_number = car_number;
        this.vehicle_engine_number = car_engine_number;
        this.cnic = cnic;
        this.city = city;
        this.phone=owner_phone;
        this.vehicle_type=vehicle_type;
        this.registration_date=registration_date;
        this.province=province;
    }
    String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    String owner_name;
    String vehicle_name;
    String vehicle_number;
    String vehicle_engine_number;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getRegistration_date() {
        return registration_date;
    }

    public void setRegistration_date(String registration_date) {
        this.registration_date = registration_date;
    }

    String province,registration_date;
    public String getVehicle_name() {
        return vehicle_name;
    }

    public void setVehicle_name(String vehicle_name) {
        this.vehicle_name = vehicle_name;
    }

    public String getVehicle_number() {
        return vehicle_number;
    }

    public void setVehicle_number(String vehicle_number) {
        this.vehicle_number = vehicle_number;
    }

    public String getVehicle_engine_number() {
        return vehicle_engine_number;
    }

    public void setVehicle_engine_number(String vehicle_engine_number) {
        this.vehicle_engine_number = vehicle_engine_number;
    }

    String cnic;
    String city;

    public String getVehicle_type() {
        return vehicle_type;
    }

    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }

    String vehicle_type;

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getCnic() {
        return cnic;
    }

    public void setCnic(String cnic) {
        this.cnic = cnic;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
