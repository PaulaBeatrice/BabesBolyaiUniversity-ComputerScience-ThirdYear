import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Random;

public class GenMatr {
    public static void main(String[] args) {
        int rows = 500;
        int cols = 500;
        String fileName = "date4.txt";

        int[][] matrice = new int[rows][cols];
        Random random = new Random();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrice[i][j] = random.nextInt(100);
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    writer.write(matrice[i][j] + " ");
                }
                writer.newLine();
            }
            System.out.println("Matricea cu valori aleatoare a fost scrisa in fisierul " + fileName);
        } catch (IOException e) {
            System.err.println("Eroare la scrierea in fisier: " + e.getMessage());
        }
    }
//    public static void main(String[] args) {
//        int rows = 10000;
//        int cols = 10000;
//        String fileName = "matrice.txt";
//
//        int[][] matrice = new int[rows][cols];
//        Random random = new Random();
//
//        for (int i = 0; i < rows; i++) {
//            for (int j = 0; j < cols; j++) {
//                matrice[i][j] = random.nextInt(100);
//            }
//        }
//
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
//            for (int i = 0; i < rows; i++) {
//                for (int j = 0; j < cols; j++) {
//                    writer.write(matrice[i][j] + " ");
//                }
//                writer.newLine();
//            }
//            System.out.println("Matricea cu valori aleatoare a fost scrisa in fisierul " + fileName);
//        } catch (IOException e) {
//            System.err.println("Eroare la scrierea in fisier: " + e.getMessage());
//        }
//    }
}
