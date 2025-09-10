# Lokal Veri İşleme Hattı (ETL) Projesi

Bu proje, bir CSV dosyasından veri okuyan, Scala kullanarak basit bir **ETL (Extract, Transform, Load)** işlemi gerçekleştiren ve sonucu bir PostgreSQL veritabanına yazan temel bir veri mühendisliği uygulamasını simüle eder.

Tüm uygulama ve veritabanı servisleri, **Docker** ve **Docker Compose** kullanılarak konteynerize edilmiştir. Bu sayede proje, herhangi bir bağımlılık kurmaya gerek kalmadan tek bir komutla çalıştırılabilir.

## Projenin Amacı

Bu projenin temel amacı, modern veri mühendisliğinde kullanılan temel konseptleri göstermektir:
- **Extract (Veri Çekme):** Lokal bir CSV dosyasından (`sales_data.csv`) satış verilerini okur.
- **Transform (Veri Dönüştürme):** Scala ile veriyi işler, eksik veya hatalı satırları filtreler.
- **Load (Veri Yükleme):** Temizlenmiş veriyi bir PostgreSQL veritabanındaki `sales` tablosuna yükler.
- **Konteynerleştirme:** Uygulama ve veritabanını Docker ile paketleyerek taşınabilir ve izole bir ortam oluşturur.
- **Orkestrasyon:** `docker-compose` ile çoklu servisleri (uygulama + veritabanı) tek bir komutla yönetir.

## Gereksinimler
- Docker
- Docker Compose (Genellikle Docker Desktop ile birlikte gelir)


```
