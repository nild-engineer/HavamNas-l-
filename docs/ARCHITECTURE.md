# Havam Nasıl? — Proje Yapısı

## Veri akışı

Kullanıcı olayı → UI callback → ViewModel → Repository → Retrofit API → yanıt modeli → mapper → ekran modeli → StateFlow → Compose.

## Katmanlar

- `model`: Şehir, hava tahmini ve hava durumu gibi ortak veri modelleri.
- `data/remote`: Retrofit servis tanımları ve API istemcisi.
- `data/remote/dto`: Sunucunun JSON yanıtlarını karşılayan sınıflar.
- `data/mapper`: JSON yanıtlarını ekranda kullanılan modellere dönüştürme; eksik değer kontrolü.
- `data/repository`: Şehir arama ve hava tahmini veri erişimi.
- `data/local`: Cihazda saklanan son şehirler.
- `domain/planning`: Takvim için dört kademeli planlama kuralları.
- `domain/recommendation`: Kıyafet ve eşya önerileri.
- `domain/time`: Şehrin yerel saatine göre saatlik tahmin seçimi.
- `domain/history`: Son aramaları sıralama, tekilleştirme ve silme kuralları.
- `ui/city`: Şehir ekranı ve ViewModel'ler.
- `ui/city/component`: Giriş ekranının tekrar kullanılabilir görsel parçaları.
- `ui/forecast`: Hava tahmini, takvim, sayfa geçişi ve ViewModel.
- `ui/forecast/component`: Tahmin kartları ve bölüm içerikleri.
- `ui/navigation`: Şehir arama ile tahmin ekranı arasındaki geçişler.
- `ui/theme`: Ortak font, renkler ve tema.
- `ui/component`: Ortak çizimler ve yükleme bileşenleri.
- `examples`: Kamp sırasında yapılan sayaç ve yerel şehir listesi alıştırmaları. Aktif uygulama akışından ayrıdır.

## Sorumluluklar

Compose ekranları veriyi gösterir ve olayları callback ile iletir. ViewModel ekran verisini ve ağ isteğinin yaşam döngüsünü yönetir. Repository veri kaynağına erişir. İş kuralları Android'den bağımsız domain sınıflarında bulunur. MainActivity ViewModel'leri oluşturur, state'i gözlemler ve olayları ilgili ViewModel'e iletir.

Bu proje kamp seviyesinde anlaşılır bir katmanlı mimari kullanır; gereksiz sayıda interface veya bağımlılık enjeksiyonu altyapısı eklenmemiştir.

## Geri tuşu

- Arama metni varsa: klavye/odak kapatılır, arama temizlenir ve ana görünüm açılır.
- Takvim sayfasındaysa: önce hava sayfasına dönülür.
- Tahmin ekranındaysa: şehir ekranına dönülür.
- Zaten ana ekrandaysa: Android'in standart geri davranışı geçerlidir.

## Tipografi

Manrope Regular, Medium, SemiBold ve Bold ağırlıkları uygulamaya gömülüdür. Türkçe karakterler kontrol edilmiştir. Lisans `app/src/main/res/raw/manrope_license.txt` dosyasında bulunur. Metinler Android'in kullanıcı yazı boyutunu izler; emoji karakterlerinde sistemin emoji yazı tipi kullanılır.
