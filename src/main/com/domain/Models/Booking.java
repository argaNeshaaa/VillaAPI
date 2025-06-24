package main.com.domain.Models;

public class Booking {
    private int id;
    private int customerId;
    private int roomTypeId;
    private String checkinDate;
    private String checkoutDate;
    private int price;
    private int voucherId;
    private int finalPrice;
    private String paymentStatus;
    private boolean hasCheckedin;
    private boolean hasCheckedout;

    public Booking() {}

    public int getId() { return id; }
    public int getCustomerId() { return customerId; }
    public int getRoomTypeId() { return roomTypeId; }
    public String getCheckinDate() { return checkinDate; }
    public String getCheckoutDate() { return checkoutDate; }
    public int getPrice() { return price; }
    public int getVoucherId() { return voucherId; }
    public int getFinalPrice() { return finalPrice; }
    public String getPaymentStatus() { return paymentStatus; }
    public boolean isHasCheckedin() { return hasCheckedin; }
    public boolean isHasCheckedout() { return hasCheckedout; }

    public void setId(int id) { this.id = id; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public void setRoomTypeId(int roomTypeId) { this.roomTypeId = roomTypeId; }
    public void setCheckinDate(String checkinDate) { this.checkinDate = checkinDate; }
    public void setCheckoutDate(String checkoutDate) { this.checkoutDate = checkoutDate; }
    public void setPrice(int price) { this.price = price; }
    public void setVoucherId(int voucherId) { this.voucherId = voucherId; }
    public void setFinalPrice(int finalPrice) { this.finalPrice = finalPrice; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public void setHasCheckedin(boolean hasCheckedin) { this.hasCheckedin = hasCheckedin; }
    public void setHasCheckedout(boolean hasCheckedout) { this.hasCheckedout = hasCheckedout; }
}
