package main.com.domain.Models;

public class RoomType {
    private int id;
    private int villa;
    private String name;
    private int quantity;
    private int capacity;
    private int price;
    private String bed_size;
    private boolean has_desk;
    private boolean has_ac;
    private boolean has_tv;
    private boolean has_wifi;
    private boolean has_shower;
    private boolean has_hotwater;
    private boolean has_fridge;

    public RoomType() {}

    public RoomType(int id, int villa, String name, int quantity, int capacity, int price,
                    String bed_size, boolean has_desk, boolean has_ac, boolean has_tv,
                    boolean has_wifi, boolean has_shower, boolean has_hotwater, boolean has_fridge) {
        this.id = id;
        this.villa = villa;
        this.name = name;
        this.quantity = quantity;
        this.capacity = capacity;
        this.price = price;
        this.bed_size = bed_size;
        this.has_desk = has_desk;
        this.has_ac = has_ac;
        this.has_tv = has_tv;
        this.has_wifi = has_wifi;
        this.has_shower = has_shower;
        this.has_hotwater = has_hotwater;
        this.has_fridge = has_fridge;
    }

    // Getter dan setter
    public int getId() { return id; }
    public int getVilla() { return villa; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public int getCapacity() { return capacity; }
    public int getPrice() { return price; }
    public String getBed_size() { return bed_size; }
    public boolean isHas_desk() { return has_desk; }
    public boolean isHas_ac() { return has_ac; }
    public boolean isHas_tv() { return has_tv; }
    public boolean isHas_wifi() { return has_wifi; }
    public boolean isHas_shower() { return has_shower; }
    public boolean isHas_hotwater() { return has_hotwater; }
    public boolean isHas_fridge() { return has_fridge; }

    public void setId(int id) { this.id = id; }
    public void setVilla(int villa) { this.villa = villa; }
    public void setName(String name) { this.name = name; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setPrice(int price) { this.price = price; }
    public void setBed_size(String bed_size) { this.bed_size = bed_size; }
    public void setHas_desk(boolean has_desk) { this.has_desk = has_desk; }
    public void setHas_ac(boolean has_ac) { this.has_ac = has_ac; }
    public void setHas_tv(boolean has_tv) { this.has_tv = has_tv; }
    public void setHas_wifi(boolean has_wifi) { this.has_wifi = has_wifi; }
    public void setHas_shower(boolean has_shower) { this.has_shower = has_shower; }
    public void setHas_hotwater(boolean has_hotwater) { this.has_hotwater = has_hotwater; }
    public void setHas_fridge(boolean has_fridge) { this.has_fridge = has_fridge; }
}
