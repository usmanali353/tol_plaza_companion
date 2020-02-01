package fyp.toll_tax_collection;

public class License {
    public String getLicense_holder_name() {
        return license_holder_name;
    }

    public void setLicense_holder_name(String license_holder_name) {
        this.license_holder_name = license_holder_name;
    }

    public String getLicense_holder_cnic() {
        return license_holder_cnic;
    }

    public void setLicense_holder_cnic(String license_holder_cnic) {
        this.license_holder_cnic = license_holder_cnic;
    }

    public String getLicense_holder_city() {
        return license_holder_city;
    }

    public void setLicense_holder_city(String license_holder_city) {
        this.license_holder_city = license_holder_city;
    }

    public String getLicense_number() {
        return license_number;
    }

    public void setLicense_number(String license_number) {
        this.license_number = license_number;
    }

    public String getLicense_type() {
        return license_type;
    }

    public void setLicense_type(String license_type) {
        this.license_type = license_type;
    }

    public License(String license_holder_name, String license_holder_cnic, String license_holder_city, String license_number, String license_type,String validity_date,String province) {
        this.license_holder_name = license_holder_name;
        this.license_holder_cnic = license_holder_cnic;
        this.license_holder_city = license_holder_city;
        this.license_number = license_number;
        this.license_type = license_type;
        this.province=province;
        this.Validity_date=validity_date;
    }

    String license_holder_name;
    String license_holder_cnic;
    String license_holder_city;
    String license_number;
    String license_type;
    String Validity_date;

    public String getValidity_date() {
        return Validity_date;
    }

    public void setValidity_date(String validity_date) {
        Validity_date = validity_date;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    String province;

    public License() {

    }
}
