package rattclub.eCommerce.Model;

public class AdminOrders {
    private String name, phone, address,
            city, date, time, state, totalPrice;

    public AdminOrders() { }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public AdminOrders(String name, String phone, String address, String city, String date, String time, String state, String totalPrice) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.date = date;
        this.time = time;
        this.state = state;
        this.totalPrice = totalPrice;


    }
}
