package main.com.domain.Models;

public class Voucher {
    private int id;
    private String code;
    private String description;
    private int discount;
    private String startDate;
    private String endDate;

    public Voucher() {};
    public Voucher(int id, String code, String description, int discount, String startDate, String endDate) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.discount = discount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getId() {return id;}
    public String getCode() { return code; }
    public String getDescription() { return description; }
    public int getDiscount() { return discount; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }

    public void setId(int id){ this.id = id;}
    public void setCode(String code) { this.code = code; }
    public void setDescription(String description) { this.description = description; }
    public void setDiscount(int discount) { this.discount = discount; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
}
