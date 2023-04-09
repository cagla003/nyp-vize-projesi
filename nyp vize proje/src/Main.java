import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.Scanner;
import java.io.*;
import javax.mail.*;
import javax.mail.internet.*;

class Uye { //uyelerin ozelliklerini barindiran bir super class olusturduk
    String isim, soyIsim, eMail; // uyelerin ozellikleri
    public Uye(String isim, String soyIsim, String eMail) {
        this.isim = isim;
        this.soyIsim = soyIsim;
        this.eMail = eMail;
    }
    static class ElitUye extends Uye { //super classindan kalitim yoluyla ozellikleri miras alan elit uyelere ait bir sub class olusturdum
        public ElitUye(String isim, String soyIsim, String eMail) {
            super(isim, soyIsim, eMail);
        }
    }
    static class GenelUye extends Uye { //super classindan kalitim yoluyla ozellikleri miras alan genel uyelere ait bir sub class olusturdum
        public GenelUye(String isim, String soyIsim, String eMail) {
            super(isim, soyIsim, eMail);
        }
    }
}

class DosyaIslemleri { //dosyaya veri yazdırma ya da dosyadan veri okuma islemleri icin bir class olusturdum
    public static void dosyaOku(String filepath) { //dosyadaki verilerin okunmasini saglayan metod
        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader(filepath, Charset.forName("UTF-8")));
            String line = reader.readLine();
            while (line != null) {
                System.out.println(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void ElitDosyaYaz(String filepath, String line){ //main classta kullanicidan alinan elit uye bilgilerini alarak verileri dosyaya yazdıran metod
        try {
            // dosyayi okuyor
            FileReader dosyaOkuyucu = new FileReader(filepath);
            BufferedReader okumaPens = new BufferedReader(dosyaOkuyucu);

            String satir = okumaPens.readLine();
            String metin = "";
            while (satir != null) {
                metin += satir + "\n";
                satir = okumaPens.readLine();
            }

            // elit uyeler dosyanin basinda yaziyor ve genel uyelerden # isareti ile ayrildigi icin metotta # karakteri araniyor ve
            // hemen oncesine uyeye ait bilgiler yazdiriliyor.
            int karakterIndeksi = metin.indexOf("#");
            if (karakterIndeksi >= 0) {
                String yeniMetin = metin.substring(0, karakterIndeksi) + line + "\n" + metin.substring(karakterIndeksi);
                FileWriter dosyaYazici = new FileWriter(filepath);
                dosyaYazici.write(yeniMetin);
                dosyaYazici.close();
            }

            okumaPens.close();
            dosyaOkuyucu.close();

            System.out.println("uye bilgileri basariyla kaydedildi\n");
        } catch (IOException e) {
            System.out.println("Hata oluştu: " + e.getMessage());
        }
    }
    public static void GenelDosyaYaz(String filepath, String line){ //bu metodla main classta alinan genel kullanici bilgileri dosyaya yazdiriliyor

        BufferedWriter bw = null;
        try {

            File file = new File(filepath);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file,true);
            bw = new BufferedWriter(fw);
            bw.write(line);
            System.out.println("uye bilgileri basariyla kaydedildi\n");

            bw.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
class MailGonderme{
    public static void MailGonderme(String filepath, String body){
        // SMTP sunucusu ve kimlik bilgileri
        final String username = "caglassarf@gmail.com";
        final String password = "dsbgdjsjudkgjafw";
        final String host = "smtp.gmail.com";
        final int port = 587;

        // mail ayarlari
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        // oturum acma
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        // dosya adı ve yolunu belirleme
        String fileName = filepath;

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;

            // her satırı okur ve isler
            while ((line = br.readLine()) != null) {

                // ad, soyad ve e-posta adresini ayırma
                //bunu en az uc string ifadesi icermeyen satirlarda yapiyor, mail atildiginde hata vermesinin sebebi bu
                //en az uc string ifadesi iceren satirlardaki mail adreslerine e-posta yollaniyor
                String[] parts = line.split("\\s+");
                if (parts.length < 3) {
                    System.out.println("Hata: Satır " + line + " geçersiz. Ad, soyad ve e-posta adresi gerekli.");
                    continue;
                }
                String firstName = parts[0];
                String lastName = parts[1];
                String email = parts[2];

                // Mesaj oluşturma ve gönderme
                try {
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(username));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
                    message.setSubject("Uyelere Mail!!!");
                    message.setText(body);

                    Transport.send(message);

                    System.out.println("Mesaj gönderildi " + email);
                } catch (MessagingException e) {
                    System.err.println("Mesaj gönderilemedi " + email);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.err.println("Dosya okunamadı " + fileName);
            e.printStackTrace();
        }
    }

}
public class Main {
    public static void main(String[] args) {
        //kullanicidan veri almak icin scanner sinifini kullandim
        Scanner scanner = new Scanner(System.in);
        String dosyam = "C:\\Users\\cagla\\IdeaProjects\\proje\\proje.txt";
        while(true){ // menuyu donguye sokmak icin donguye girmesini sagladim
            //ekrana cikacak menu
            System.out.println("programa hosgeldiniz."); //ekrana cikacak menu
            System.out.println("1- elit uye ekleme\n" + "2- genel uye ekleme\n" + "3- tum uyelere mail gonderme");
            System.out.println("yapmak istediginiz islemi secin:");
            // kullanicidan secimini int deger ile aldim
            int secim1 = scanner.nextInt();

            scanner.nextLine(); // kullanıcıdan scannerla int deger aldiktan sonra string deger
            // alirken sorun yasandigi icin bos bir string degeri aldim

            //kullanicinin secimine gore programin yapacagi seyler
            switch (secim1) {
                case 1:
                    //kullanicidan kaydedilecek elit uyeye ait bilgiler aliyoruz
                    System.out.println("uye adini giriniz:");
                    String elitAd = scanner.nextLine();
                    System.out.println("uye soyadini giriniz:");
                    String elitSoyad = scanner.nextLine();
                    System.out.println("uyenin mail adresini giriniz:");
                    String elitMailAdresi = scanner.nextLine();

                    //uye classinin alt classi olan ElitUye classindan nesne oluşturuldu
                    Uye.ElitUye yeniElit = new Uye.ElitUye(elitAd, elitSoyad, elitMailAdresi);

                    //elit uye bilgilerini dosyaya yazdırmak icin DosyaIslemleri classinin metodunu cagiriyoruz
                    DosyaIslemleri.ElitDosyaYaz(dosyam, elitAd + "\t" + elitSoyad + "\t" + elitMailAdresi + "\n");
                    break;
                case 2:
                    //kullanicidan kaydedilecek genel uyeye ait bilgiler aliyoruz
                    System.out.println("uye adini giriniz:");
                    String genelAd = scanner.nextLine();
                    System.out.println("uye soyadini giriniz:");
                    String genelSoyad = scanner.nextLine();
                    System.out.println("uyenin mail adresini giriniz:");
                    String genelMailAdresi = scanner.nextLine();

                    //uye classinin alt classi olan GenelUye classindan nesne oluşturuldu
                    Uye.GenelUye yeniGenel = new Uye.GenelUye(genelAd, genelSoyad, genelMailAdresi);

                    //genel uye bilgilerini dosyaya yazdırmak icin DosyaIslemleri classinin metodunu cagiriyoruz
                    DosyaIslemleri.GenelDosyaYaz(dosyam, genelAd + "\t" + genelSoyad + "\t" + genelMailAdresi + "\n");
                    break;
                case 3:
                    //kullanicidan gonderilecek olan maile ait bilgileri istiyoruz
                    System.out.println("gonderilecek olan mailin metnini giriniz:");
                    String mailMetin = scanner.nextLine();
                    MailGonderme.MailGonderme(dosyam, mailMetin); //mail gondermek icin gerekli classtaki metodu cagirarak bilgileri gonderiyoruz

                    break;
                default:
                    System.out.println("gecersiz bir secim yaptiniz, lutfen tekrar deneyin.");
            }}

    }
}