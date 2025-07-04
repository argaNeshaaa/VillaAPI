# 🏡 Villa Booking API

Villa Booking API adalah backend aplikasi pemesanan villa yang dibangun menggunakan Java dan SQLite. Proyek ini menyediakan endpoint RESTful untuk mengelola data villa, tipe kamar, pelanggan, pemesanan, dan ulasan.

## ✨ Fitur Utama

- CRUD Villa dan Tipe Kamar
- Pemesanan oleh pelanggan
- Menampilkan daftar villa yang tersedia berdasarkan tanggal check-in dan check-out
- Ulasan pelanggan terhadap villa yang mereka pesan
- Validasi kepemilikan booking sebelum membuat ulasan
- Mendukung API Key untuk autentikasi (opsional)

## 🛠 Teknologi yang Digunakan

- Java
- SQLite (Database)
- Postman (Testing API)



## 🔐 Autentikasi & API Key

Akses endpoint dilindungi dan memerlukan **API Key**. API Key dapat dilihat pada Class Main dan juga pada Format Header di bawah, API Key ini harus dikirim melalui **header** HTTP pada Postman sebagai berikut:

### 📥 API Key
Authorization: Bearer 
```http
API_KEY_LIVE_prod_v2_xyz123ABCDEF456GHIJKL7890MNOPQRSTUV
```

## 🚀 Cara Menjalankan Proyek