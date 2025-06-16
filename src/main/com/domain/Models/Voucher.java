package main.com.domain.Models;

public class Voucher {
    public String kode;
    public String diskon;

    public Voucher() {}
    public Voucher(String kode, String diskon) {
        this.kode = kode;
        this.diskon = diskon;
    }

    public String getKode() { return kode; }
    public String getDiskon() { return diskon; }

    public void setKode(String kode) { this.kode = kode; }
    public void setDiskon(String diskon) { this.diskon = diskon; }
}