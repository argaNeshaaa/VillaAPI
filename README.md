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
``` Authorization: Bearer http
API_KEY_LIVE_prod_v2_xyz123ABCDEF456GHIJKL7890MNOPQRSTUV
```

## 🚀 Cara Menjalankan Proyek
Dalam menjalankan proyek ini, kami menggunakan postman untuk mempermudah test.

### GET

Menampilkan daftar semua villa
![get villas](https://github.com/user-attachments/assets/53e8e218-bf5f-4f5d-a2bf-f5b703ebb33d)

Menampilkan detail satu villa berdasarkan ID
![get villas(id)](https://github.com/user-attachments/assets/462ba0cd-6278-4981-863e-519de9f6a781)

Menampilkan semua kamar dari villa tertentu
![get villas(id)rooms](https://github.com/user-attachments/assets/956c1c94-0af5-4139-8627-b091772d83ce)

Menampilkan semua booking yang dilakukan pada villa tersebut
![get villas(id)bookings](https://github.com/user-attachments/assets/cdbeb52b-459a-47dd-8ff6-26c62e797ed3)

Menampilkan semua review untuk villa tertentu
![get villas(id)review](https://github.com/user-attachments/assets/d717fe53-f535-4e82-87a5-0644d00a017f)

Mencari villa yang tersedia di antara tanggal check-in dan check-out
![get villas cek in   cek out](https://github.com/user-attachments/assets/bc80ad00-736b-4cff-aecc-6cc46b55280d)

Menampilkan daftar semua customer
![get customers](https://github.com/user-attachments/assets/0dcbd9e5-e1e9-47a3-8895-e9fbdadf8a65)

Menampilkan detail seorang customer berdasarkan ID
![get customers(id)](https://github.com/user-attachments/assets/ebb75454-0121-4234-aced-c17ff8714970)

Menampilkan daftar booking yang telah dilakukan oleh customer
![get customers(id)bookings](https://github.com/user-attachments/assets/40c28640-0047-4fa0-ace4-1dcbd71eae45)

Menampilkan review-review yang pernah dibuat oleh customer
![get customers(id)reviews](https://github.com/user-attachments/assets/a867d217-9dba-4591-a511-51ffd7bbf8bc)

Menampilkan semua voucher yang tersedia
![get vouchers](https://github.com/user-attachments/assets/3aed7ee6-43e8-4ca3-8792-a28bc7790505)

Menampilkan detail voucher berdasarkan ID
![get voucher(id)](https://github.com/user-attachments/assets/4c4fcc22-4b43-4317-8571-fbd5c09e67ce)

### POST

Menambahkan data villa baru
![post villas](https://github.com/user-attachments/assets/6cba02a2-46ba-4d0a-9e5b-4725f786aa3e)

Menambahkan tipe kamar pada villa
![post villas(id)rooms](https://github.com/user-attachments/assets/1ce6fce4-a84a-4950-807e-c8a18674ab04)

Menambahkan customer baru (registrasi)
![post customers](https://github.com/user-attachments/assets/2e3d8923-6818-43bf-b7f2-5a1485065242)

Customer membuat pemesanan villa (booking)
![post customers(id)booking](https://github.com/user-attachments/assets/5fefd700-0a3e-4cc3-b9f4-f217af5356c7)

Customer menambahkan review untuk villa yang sudah pernah dipesan
![post customers(id)bookings(id)review](https://github.com/user-attachments/assets/5b77880c-3e9b-47ea-8f9f-7435bdf16682)

Menambahkan voucher baru
![post vouchers](https://github.com/user-attachments/assets/6e3706e7-15d5-44d5-bb10-f2c641d74d2a)

### PUT
Mengubah/memperbarui data villa tertentu
![put villas(id)](https://github.com/user-attachments/assets/8f90da80-d940-476f-a129-aa6f9f673d3e)

Mengubah informasi kamar tertentu dari sebuah villa
![put villas(id)rooms(id)](https://github.com/user-attachments/assets/ea161c32-ba3f-4e0f-ada3-130fcc8057f4)

Mengubah/memperbarui data customer
![put customers(id)](https://github.com/user-attachments/assets/45017b70-9e61-49ef-ae28-d6ab2f7d83ba)

Mengubah data voucher
![put vouchers(1)](https://github.com/user-attachments/assets/aa911154-7586-4ab1-af05-d16d03084acd)

### DELETE

Menghapus kamar dari sebuah villa
![delete villas(id)rooms(id)](https://github.com/user-attachments/assets/1242b75c-0629-4d39-a41b-2e269a21f608)

Menghapus data suatu villa berdasarkan ID
![delete villas(id)](https://github.com/user-attachments/assets/1519b1c5-983d-4e5d-bac0-a9783143c957)

Menghapus voucher berdasarkan ID
![delete vouchers(id)](https://github.com/user-attachments/assets/1beaabf2-1de9-4247-a00f-b1e748d3d5a2)
