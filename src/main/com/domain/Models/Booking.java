package main.com.domain.Models;

public class Booking {
    private int id;
    private int customer;         
    private int room_type;        
    private String checkin_date;  
    private String checkout_date; 
    private int price;
    private int voucher;          
    private int final_price;      
    private String payment_status;
    private boolean has_checkedin;
    private boolean has_checkedout;

    public Booking() {}

    public int getId() { return id; }
    public int getCustomer() { return customer; }
    public int getRoom_type() { return room_type; }
    public String getCheckin_date() { return checkin_date; }
    public String getCheckout_date() { return checkout_date; }
    public int getPrice() { return price; }
    public int getVoucher() { return voucher; }
    public int getFinal_price() { return final_price; }
    public String getPayment_status() { return payment_status; }
    public boolean isHas_checkedin() { return has_checkedin; }
    public boolean isHas_checkedout() { return has_checkedout; }

    public void setId(int id) { this.id = id; }
    public void setCustomer(int customer) { this.customer = customer; }
    public void setRoom_type(int room_type) { this.room_type = room_type; }
    public void setCheckin_date(String checkin_date) { this.checkin_date = checkin_date; }
    public void setCheckout_date(String checkout_date) { this.checkout_date = checkout_date; }
    public void setPrice(int price) { this.price = price; }
    public void setVoucher(int voucher) { this.voucher = voucher; }
    public void setFinal_price(int final_price) { this.final_price = final_price; }
    public void setPayment_status(String payment_status) { this.payment_status = payment_status; }
    public void setHas_checkedin(boolean has_checkedin) { this.has_checkedin = has_checkedin; }
    public void setHas_checkedout(boolean has_checkedout) { this.has_checkedout = has_checkedout; }
}
