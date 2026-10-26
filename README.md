# Havam Nasıl?

Kamp kapsamında Kotlin ve Jetpack Compose ile geliştirilen Android hava durumu uygulaması.

## APK indir

[Havam Nasıl? — Android kurulum dosyası](apk/Havam-Nasil.apk?raw=true)

## Özellikler

- Şehir arama, son aranan şehirler ve geçmişten silme.
- Anlık hava durumu, saatlik ve günlük tahminler.
- Verisi bulunan günler için dört renkli planlama takvimi ve gerekçeli öneriler.
- Hava durumuna göre yanına alınabilecekler ve kıyafet önerileri.
- Hava durumuna uygun temalar ve animasyonlu yükleme görünümü.
- Yükleniyor, boş sonuç, hata ve tekrar deneme durumları.

## Mimari

UI → ViewModel / StateFlow → Repository → Retrofit → Open-Meteo.

`ui/` ekranları ve durum yönetimini, `domain/` öneri ve planlama kurallarını, `data/` API ve yerel veri erişimini, `model/` ortak veri modellerini içerir.

[Ayrıntılı proje mimarisi](docs/ARCHITECTURE.md)

## Çalıştırma

Android Studio ile proje klasörünü açın. Android SDK yolunu kendi bilgisayarınıza göre ayarlayın ve Gradle eşitlemesini tamamlayın. Minimum Android sürümü API 26’dır. Open-Meteo için API anahtarı gerekmez.

```sh
./gradlew assembleDebug
./gradlew testDebugUnitTest
```

Son doğrulama: 37 birim testi ve son teslim için 2 arayüz kontrolü başarılı. APK geliştirme imzasıyla imzalanmıştır.

## Referans

[Kamp referans projesi](https://github.com/tc-yusufburakdemir/weather-app). Hava durumu verileri [Open-Meteo](https://open-meteo.com/) servisinden alınır. Planlama önerileri tahmin verilerine dayalıdır; resmi meteorolojik uyarı yerine geçmez.
