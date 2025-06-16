package main.com.domain.Models;

public class Villa {
    public String id;
    public String nama;
    public int jumlahKamar;

    public Villa() {} // Penting untuk deserialisasi Jackson
    public Villa(String id, String nama, int jumlahKamar) {
        this.id = id;
        this.nama = nama;
        this.jumlahKamar = jumlahKamar;
    }

    // Getter (Jackson menggunakan ini untuk serialisasi)
    public String getId() { return id; }
    public String getNama() { return nama; }
    public int getJumlahKamar() { return jumlahKamar; }

    // Setter (Jackson menggunakan ini untuk deserialisasi)
    public void setId(String id) { this.id = id; }
    public void setNama(String nama) { this.nama = nama; }
    public void setJumlahKamar(int jumlahKamar) { this.jumlahKamar = jumlahKamar; }
}