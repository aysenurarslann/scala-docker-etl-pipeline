package pipeline

import scala.io.Source
import java.sql.{Connection, DriverManager, PreparedStatement, Date}
import java.time.LocalDate
import scala.util.{Try, Using, Success, Failure}

object S3ToRedshiftETL {

  // Veritabanı bağlantısını yönetmek için bir yardımcı fonksiyon
  def getConnection(): Try[Connection] = Try {
    // Ortam değişkenlerinden veritabanı bilgilerini al
    val url = sys.env("REDSHIFT_URL")
    val user = sys.env("REDSHIFT_USER")
    val password = sys.env("REDSHIFT_PASSWORD")

    // JDBC sürücüsünü yükle (PostgreSQL için)
    Class.forName("org.postgresql.Driver")

    DriverManager.getConnection(url, user, password)
  }

  def main(args: Array[String]): Unit = {
    // Lokaldeki CSV dosyasını oku. Docker içinde bu dosya /app/your-data.csv olacak.
    val localFilePath = "sales_data.csv"
    println(s"Lokal CSV dosyası okunuyor: $localFilePath")

    val lines = Using(Source.fromFile(localFilePath)) { source =>
      source.getLines().toList
    }.getOrElse {
      println(s"Hata: $localFilePath dosyası okunamadı.")
      sys.exit(1) // Dosya okunamadıysa programı sonlandır
      List.empty[String]
    }

    // Başlık satırını atla ve boş satırları filtrele
    val dataLines = lines.drop(1).filter(_.nonEmpty)
    println(s"${dataLines.size} veri satırı bulundu.")

    // Veritabanı bağlantısı kur ve verileri yükle
    getConnection() match {
      case Failure(exception) =>
        println("Veritabanı bağlantısı kurulamadı.")
        exception.printStackTrace()
        sys.exit(1)

      case Success(conn) =>
        Using.Manager { use =>
          val stmt = use(conn.createStatement())
          // Yeni veri setine uygun tablo oluştur.
          val createTableSql = """
            CREATE TABLE IF NOT EXISTS sales (
              order_id INT PRIMARY KEY,
              product_name TEXT,
              category TEXT,
              price NUMERIC(10, 2),
              order_date DATE
            )"""
          stmt.executeUpdate(createTableSql)
          println("Tablo oluşturuldu veya zaten mevcut.")

          val insertSql = "INSERT INTO sales (order_id, product_name, category, price, order_date) VALUES (?, ?, ?, ?, ?) ON CONFLICT (order_id) DO NOTHING"
          val pstmt = use(conn.prepareStatement(insertSql))

          dataLines.foreach { line =>
            val result = Try {
              val cols = line.split(",", -1).map(_.trim) // -1 limiti boş sondaki sütunları korur
              // Veri dönüşümü ve doğrulama
              val orderId = cols(0).toInt
              val productName = cols(1)
              val category = cols(2)
              val price = if (cols(3).isEmpty) None else Some(cols(3).toDouble)
              val orderDate = if (cols(4).isEmpty) None else Some(LocalDate.parse(cols(4)))
              (orderId, productName, category, price, orderDate)
            }

            result match {
              case Success((id, name, cat, Some(price), Some(date))) =>
                pstmt.setInt(1, id)
                pstmt.setString(2, name)
                pstmt.setString(3, cat)
                pstmt.setDouble(4, price)
                pstmt.setDate(5, Date.valueOf(date))
                pstmt.addBatch() // Sadece tam ve doğru verileri ekle
              case _ => println(s"Hatalı veya eksik veri içeren satır atlandı: $line")
            }
          }
          val insertedRows = pstmt.executeBatch().sum
          println(s"$insertedRows satır veritabanına başarıyla yazıldı.")
        }
    }
  }
}