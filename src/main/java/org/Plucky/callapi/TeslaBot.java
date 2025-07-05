package org.Plucky.callapi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TeslaBot {
    
    private static final Random random = new Random();
    private static long lastRequestTime = 0;
    private static final long MIN_REQUEST_INTERVAL = 3000; // 3 saniye minimum bekleme
    
    public static class CarInfo {
        public String model;
        public String price;
        public String vin;
        public String orderLink;
        
        public CarInfo(String model, String price, String vin, String orderLink) {
            this.model = model;
            this.price = price;
            this.vin = vin;
            this.orderLink = orderLink;
        }
    }
    
    private static void playVoiceAlert(String message) {
        try {
            // macOS say komutu ile sesli uyarı - Yelda (Türkçe ses)
            ProcessBuilder pb = new ProcessBuilder("say", "-v", "Yelda", message);
            Process process = pb.start();
            process.waitFor();
        } catch (Exception e) {
            System.out.println("Sesli uyarı çalıştırılamadı: " + e.getMessage());
        }
    }
    
    private static void enforceRateLimit() {
        long currentTime = System.currentTimeMillis();
        long timeSinceLastRequest = currentTime - lastRequestTime;
        
        if (timeSinceLastRequest < MIN_REQUEST_INTERVAL) {
            try {
                long sleepTime = MIN_REQUEST_INTERVAL - timeSinceLastRequest;
                System.out.println("⏳ Rate limiting: " + sleepTime + "ms bekleniyor...");
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        lastRequestTime = System.currentTimeMillis();
    }
    
    public static List<CarInfo> checkStealthGreyModelY() throws Exception {
        enforceRateLimit();
        
        List<CarInfo> cars = new ArrayList<>();
        
        // Tesla API endpoint'leri
        String[] apiUrls = {
            "https://www.tesla.com/tr_tr/my/inventory/api/v4/inventory-results?query=%7B%22query%22%3A%7B%22model%22%3A%22my%22%2C%22condition%22%3A%22new%22%2C%22options%22%3A%7B%7D%2C%22arrangeby%22%3A%22Price%22%2C%22order%22%3A%22asc%22%2C%22market%22%3A%22TR%22%2C%22language%22%3A%22tr%22%7D%2C%22offset%22%3A0%2C%22count%22%3A24%7D"
        };
        
        Exception lastException = null;
        
        for (String apiUrl : apiUrls) {
            try {
                System.out.println("🔍 API kontrol ediliyor...");
                cars = fetchFromApi(apiUrl);
                if (!cars.isEmpty()) {
                    System.out.println("✅ Başarılı! " + cars.size() + " Stealth Grey Model Y bulundu.");
                    return cars;
                }
            } catch (Exception e) {
                lastException = e;
                System.out.println("❌ Bu endpoint başarısız: " + e.getMessage());
                Thread.sleep(2000 + random.nextInt(3000));
            }
        }
        
        if (lastException != null) {
            throw lastException;
        }
        
        return cars;
    }
    
    private static List<CarInfo> fetchFromApi(String apiUrl) throws Exception {
        List<CarInfo> cars = new ArrayList<>();
        
        URL url = java.net.URI.create(apiUrl).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        
        // Gerçekçi browser headers
        conn.setRequestProperty("Accept", "application/json, text/plain, */*");
        conn.setRequestProperty("Accept-Language", "tr-TR,tr;q=0.9,en-US;q=0.8,en;q=0.7");
        conn.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        conn.setRequestProperty("Cache-Control", "no-cache");
        conn.setRequestProperty("Pragma", "no-cache");
        conn.setRequestProperty("Sec-Ch-Ua", "\"Not_A Brand\";v=\"8\", \"Chromium\";v=\"120\", \"Google Chrome\";v=\"120\"");
        conn.setRequestProperty("Sec-Ch-Ua-Mobile", "?0");
        conn.setRequestProperty("Sec-Ch-Ua-Platform", "\"macOS\"");
        conn.setRequestProperty("Sec-Fetch-Dest", "empty");
        conn.setRequestProperty("Sec-Fetch-Mode", "cors");
        conn.setRequestProperty("Sec-Fetch-Site", "same-origin");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        conn.setRequestProperty("Referer", "https://www.tesla.com/tr_tr/my/inventory");
        conn.setRequestProperty("Origin", "https://www.tesla.com");
        conn.setRequestProperty("Connection", "keep-alive");
        conn.setRequestProperty("DNT", "1");
        conn.setRequestProperty("Cookie", "_abck=1C66BCA182D3FAC715A13E39D7E663EC~-1~YAAQVcETAt1Bn8aXAQAAtCCa2w68GrA5zZzCW//SZd96XEjL8EuJZh6yFINPrqBVcOrOMxyk5zUN6b5DP8S8HtgMB0s95oxHMqe+fyiRVs2ljjsOJlnAaZYlfydyu+Qoy7ormtUirsIq4z0xdve14omdaiv0LFBOBWDq2crKDVKgRfDerwMDUvDFxP2Lkk1lPcHRZFdI5a88+5P6dCpjlqeGrmHOZYlKKRrI4bR4FXPhP2V3cIMSCdgegPPuINPKaL+W84OaavqMb6mLVdp370rIk33sqWs+hn0QsL9B42cgRvg63SOgA/MaSgLHqCJ43UZbm145a6dlfF0MbozPGkvqeqXz92jXqvb+/HLwyo+iKdNQT7V3CjVlfoQq6caW5HA6B21Wk+xEN+5/X66FBDoNFSQh7aEpMPndMvwikEdNdioLnfmGcUnEQbH9aGIe~-1~-1~-1; bm_s=YAAQR8ETAsUbV7SXAQAABmeo2wNzhRaUJludy37WfupHid2NhZ/JMhOYx8DBNWy7c6BBfvo1gM99ofRAjFyQqn5g8rpkMcY+dRhxzBr70CwTH9dmAhTfq755MCkjM+FjL+zwEZoflnH+qDS2PpkTQfLKVvfFKyQyskumrimyt2kucAsN7VU95mUEH03jxGQ/mTB9Rrq62EDsPyfrNcWsvnc3WLRxX2wjZ1yrTJa+3hd24ymUQVWNHdz0tTWVavhgJhQS4IY8Ww2FJQUhD1SwgPDD69qe3XrPwkW/FhIUyjgwpDWig8NYAh2NOxZ9V75lykhfo2MSwCKLc4H37wrTOR1SaJB19gR22zxPYDMvc5li3bdgLLFSZhtMaCerrf5/8I8n8wfvsWUz8qHL9HpiGlJdshcU8bXLObmYkZ6ptJArVq5VWnxxupiX7nEMWIeSwHhvenqBM9DdMTpCAXLy7G6ZXoNMfpgcZcy1nJyxnX1fR7wFu5ZiYFtPRC+mLwviUL8T4I+laeAwbhN80W2vgZukSz5IiI6cVIWiW4Sr/fyBaVznivg=; bm_ss=ab8e18ef4e; bm_sz=381210DB21052F209CA0B796A556257C~YAAQVcETAt9Bn8aXAQAAtCCa2xxWn/xKpFHas41+txUcySk+xM6sig2ZUdGn8AtBnMBfz/cJACpowp3SWvTXT4rrcD299f3U5hzK+QGDZ4YM5DcFFS/+hCKqTYE79O4UI62CEdC4RbAHI7QY7ne+IuE9p37/KreMBxvMtU7vxt7mabXInCqY0wlKSrRcef8+rwXPhsHtsOh2Jax1iFR4GHBgt1WhNE1W88fkmM/hEU2Vg/u+0JXcrE56Wfoa4knyjebT2dc82Qj6FMo5fMEMPOU+CHIh0YNxmrWWD5LX8rzqvysBhW8mbU1XY9wTAk+zlBDo6QFFGTxQ+PnM5MSUvPt7B9fKHMZ3BDch~3749431~3355445; akavpau_zezxapz5yf=1751737236~id=3f25ed029f2b6cc4ad007c35fdba40b9");
        
        // Random delay
        Thread.sleep(1000 + random.nextInt(2000));
        
        int responseCode = conn.getResponseCode();
        System.out.println("📡 Response Code: " + responseCode);
        
        if (responseCode == 403) {
            throw new RuntimeException("403 Forbidden - Erişim engellendi");
        } else if (responseCode != 200) {
            throw new RuntimeException("API hatası: " + responseCode);
        }
        
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        conn.disconnect();
        
        // JSON parsing - Stealth Grey araçları bul
        String responseStr = response.toString();
        cars = parseStealthGreyCars(responseStr);
        
        return cars;
    }
    
    private static List<CarInfo> parseStealthGreyCars(String jsonResponse) {
        List<CarInfo> cars = new ArrayList<>();
        
        try {
            // PAINT değerlerini ara
            String[] parts = jsonResponse.split("\"PAINT\":");
            
            for (int i = 1; i < parts.length; i++) {
                String part = parts[i];
                
                // PAINT değerini al
                int paintStart = part.indexOf("\"") + 1;
                int paintEnd = part.indexOf("\"", paintStart);
                if (paintEnd == -1) continue;
                
                String paint = part.substring(paintStart, paintEnd);
                
                // Stealth Grey kontrolü (STY5S kodu)
                if (paint.equalsIgnoreCase("STY5S")) {
                    String model = extractValue(part, "trimName");
                    String price = extractValue(part, "price");
                    String vin = extractValue(part, "VIN");
                    
                    if (vin != null && !vin.isEmpty()) {
                        String orderLink = "https://www.tesla.com/tr_tr/my/order/" + vin + "?titleStatus=new&redirect=no#overview";
                        cars.add(new CarInfo(model, price, vin, orderLink));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("JSON parsing hatası: " + e.getMessage());
        }
        
        return cars;
    }
    
    private static String extractValue(String jsonPart, String key) {
        try {
            String searchKey = "\"" + key + "\":";
            int start = jsonPart.indexOf(searchKey);
            if (start == -1) return null;
            
            start += searchKey.length();
            
            // Boşlukları atla
            while (start < jsonPart.length() && Character.isWhitespace(jsonPart.charAt(start))) {
                start++;
            }
            
            if (start >= jsonPart.length()) return null;
            
            char firstChar = jsonPart.charAt(start);
            if (firstChar == '"') {
                // String değer
                start++;
                int end = jsonPart.indexOf("\"", start);
                if (end == -1) return null;
                return jsonPart.substring(start, end);
            } else {
                // Sayısal değer
                int end = start;
                while (end < jsonPart.length() && 
                       (Character.isDigit(jsonPart.charAt(end)) || 
                        jsonPart.charAt(end) == '.' || 
                        jsonPart.charAt(end) == ',')) {
                    end++;
                }
                return jsonPart.substring(start, end).replace(",", "");
            }
        } catch (Exception e) {
            return null;
        }
    }
    
    public static void main(String[] args) {
        int attemptCount = 0;
        int maxAttempts = 50; // Maksimum deneme sayısı
        
        System.out.println("🚗 Tesla Stealth Grey Model Y Bot Başlatıldı");
        System.out.println("=============================================");
        
        // Başlangıç sesli uyarısı
        playVoiceAlert("Tesla bot başlatıldı. Stealth Grey Model Y aranıyor.");
        
        try {
            while (attemptCount < maxAttempts) {
                attemptCount++;
                System.out.println("\n=== Deneme #" + attemptCount + " ===");
                
                try {
                    List<CarInfo> cars = checkStealthGreyModelY();
                    
                    if (cars.isEmpty()) {
                        System.out.println("❌ Şu anda Stealth Grey Model Y bulunamadı.");
                        System.out.println("⏰ 60 saniye sonra tekrar denenecek...");
                        Thread.sleep(60000);
                    } else {
                        // SESLİ UYARI - Stealth Grey bulundu!
                        String alertMessage = "Dikkat! Stealth Grey Model Y bulundu! Hemen sipariş sayfasına gidin!";
                        playVoiceAlert(alertMessage);
                        
                        // 3 saniye sonra tekrar sesli uyarı
                        Thread.sleep(3000);
                        playVoiceAlert("Stealth Grey Tesla Model Y bulundu! Acele edin!");
                        
                        System.out.println("\n🎉 STEALTH GREY MODEL Y BULUNDU! 🎉");
                        System.out.println("=====================================");
                        
                        for (int i = 0; i < cars.size(); i++) {
                            CarInfo car = cars.get(i);
                            System.out.println("\n🚗 Araç #" + (i + 1) + ":");
                            System.out.println("Model: " + car.model);
                            System.out.println("Fiyat: " + car.price);
                            System.out.println("VIN: " + car.vin);
                            System.out.println("🔗 Sipariş Linki: " + car.orderLink);
                            System.out.println("-----------------------------");
                        }
                        
                        System.out.println("\n📋 Sipariş için gerekli bilgiler:");
                        System.out.println("• Ad Soyad");
                        System.out.println("• TC Kimlik No");
                        System.out.println("• Doğum Tarihi");
                        System.out.println("• Telefon Numarası");
                        System.out.println("• E-posta Adresi");
                        System.out.println("• Adres Bilgileri");
                        
                        System.out.println("\n🔊 DİKKAT: Stealth Grey Model Y bulundu!");
                        System.out.println("Hemen sipariş sayfasına gidin!");
                        
                        // 5 dakika sonra tekrar sesli uyarı
                        Thread.sleep(300000); // 5 dakika
                        playVoiceAlert("Stealth Grey Tesla hala mevcut. Sipariş vermeyi unutmayın!");
                        
                        // 5 dakika daha bekle ve tekrar kontrol et
                        System.out.println("\n⏰ 5 dakika daha bekleniyor...");
                        Thread.sleep(300000);
                        
                        System.out.println("\n🔄 Tekrar kontrol ediliyor...");
                        continue;
                    }
                } catch (RuntimeException e) {
                    if (e.getMessage().contains("403")) {
                        System.out.println("⚠️  403 Hatası: Tesla API'si erişimi engelliyor.");
                        playVoiceAlert("Tesla API erişimi engellendi. 10 dakika bekleniyor.");
                        System.out.println("⏰ 10 dakika bekleyip tekrar deneyeceğiz...");
                        Thread.sleep(600000);
                    } else {
                        System.out.println("❌ Hata: " + e.getMessage());
                        System.out.println("⏰ 30 saniye sonra tekrar denenecek...");
                        Thread.sleep(30000);
                    }
                } catch (Exception e) {
                    System.out.println("❌ Beklenmeyen hata: " + e.getMessage());
                    System.out.println("⏰ 60 saniye sonra tekrar denenecek...");
                    Thread.sleep(60000);
                }
            }
            
            if (attemptCount >= maxAttempts) {
                System.out.println("\n❌ Maksimum deneme sayısına ulaşıldı (" + maxAttempts + ").");
                playVoiceAlert("Tesla bot maksimum deneme sayısına ulaştı. Program sonlandırılıyor.");
                System.out.println("Program sonlandırılıyor.");
            }
            
        } catch (InterruptedException e) {
            System.out.println("Program kullanıcı tarafından durduruldu.");
            playVoiceAlert("Tesla bot durduruldu.");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.out.println("❌ Kritik hata: " + e.getMessage());
            playVoiceAlert("Tesla bot kritik bir hata aldı.");
            e.printStackTrace();
        }
    }
} 