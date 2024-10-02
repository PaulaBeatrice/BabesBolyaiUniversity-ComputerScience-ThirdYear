import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GenerareDate {
    public static void main(String[] args) {
        String path = "D:\\FACULTATE\\FACULTATE\\Anul 3\\Semestrul 1\\Programare Paralela si Distribuita\\Lab_4\\src\\main\\java\\data\\data";
        for (int i = 1; i <= 5; i++) {
            String folderCountry = path + "Country" + i + "/";
            File directory = new File(folderCountry); // cream directorul pentru fiecare tara
            if (!directory.exists()) {
                directory.mkdirs();
                System.out.println("Director creat: " + folderCountry);
            }
            int idParticipant = i * 100;
            for (int j = 1; j <= 10; j++) { // general fisierul pt tara i, si punctajele de la problema j
                String fisier = "RezultatC" + i + "_P" + j + ".txt";
                String pathFisier = folderCountry + fisier;
                try {
                    File file = new File(pathFisier);
                    if (file.createNewFile()) {
                        System.out.println("Fisier creat: " + pathFisier);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try (FileWriter writer = new FileWriter(pathFisier)) {
                    StringBuilder continut = new StringBuilder();
                    int nrParticipanti = ThreadLocalRandom.current().nextInt(80, 101);
                    for (int k = 1; k <= nrParticipanti; k++) { // cream perechea: id participant, punctaj participant
                        int idPr = idParticipant + k;
                        int pctPart = ThreadLocalRandom.current().nextInt(-1, 101);
                        String text = "C" + i + "_" + idPr +"," + pctPart;
                        continut.append(text).append("\n");
                    }
                    writer.write(continut.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Datele au fost generate cu succes");
    }
}
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.Random;
//import java.util.concurrent.ThreadLocalRandom;
//
//public class GenerareDate {
//    public static void main(String[] args) {
//        String path = "D:\\FACULTATE\\FACULTATE\\Anul 3\\Semestrul 1\\Programare Paralela si Distribuita\\Lab_4\\src\\main\\java\\data\\data";
//        for (int i = 1; i <= 5; i++) {
//            String folderCountry = path + "Country" + i + "/";
//            File directory = new File(folderCountry); // cream directorul pentru fiecare tara
//            if (!directory.exists()) {
//                directory.mkdirs();
//                System.out.println("Director creat: " + folderCountry);
//            }
//            int idParticipant = i * 100;
//            for (int j = 1; j <= 10; j++) { // general fisierul pt tara i, si punctajele de la problema j
//                String fisier = "RezultatC" + i + "_P" + j + ".txt";
//                String pathFisier = folderCountry + fisier;
//                try {
//                    File file = new File(pathFisier);
//                    if (file.createNewFile()) {
//                        System.out.println("Fisier creat: " + pathFisier);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                try (FileWriter writer = new FileWriter(pathFisier)) {
//                    StringBuilder continut = new StringBuilder();
//                    int nrParticipanti = ThreadLocalRandom.current().nextInt(80, 101);
//                    for (int k = 1; k <= nrParticipanti; k++) { // cream perechea: id participant, punctaj participant
//                        int idPr = idParticipant + k;
//                        int pctPart = ThreadLocalRandom.current().nextInt(-1, 101);
//                        continut.append(idPr).append(" ").append(pctPart).append("\n");
//                    }
//                    writer.write(continut.toString());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        System.out.println("Datele au fost generate cu succes");
//    }
//}