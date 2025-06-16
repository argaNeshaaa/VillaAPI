package main.com.domain.Models;

public class Customer {
    public String id;
    public String nama;
    public String email;

    public Customer() {}
    public Customer(String id, String nama, String email) {
        this.id = id;
        this.nama = nama;
        this.email = email;
    }

    public String getId() { return id; }
    public String getNama() { return nama; }
    public String getEmail() { return email; }

    public void setId(String id) { this.id = id; }
    public void setNama(String nama) { this.nama = nama; }
    public void setEmail(String email) { this.email = email; }
}
