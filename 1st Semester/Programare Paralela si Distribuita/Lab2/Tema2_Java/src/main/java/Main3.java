import java.io.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Main3 {
    private static int N, M, p;
    private static int[][] A_secv, A_paralel, K;
    private static CyclicBarrier barrier;

    public static void citire(String path) {
        try {
            File fisier = new File(path);
            Scanner reader = new Scanner(fisier);
            int copieN = 0;
            if (reader.hasNextLine()) {
                N = Integer.parseInt(reader.nextLine()); // citeste nr de linii
                M = Integer.parseInt(reader.nextLine()); // citeste nr de coloane
                copieN = N;
            }

            A_secv = new int[N + 2][M + 2]; // initializeaza matricile conform dimensiunilor citite
            A_paralel = new int[N + 2][M + 2];
            K = new int[3][3];

            // Citirea matricei din fisier
            for (int i = 1; i <= copieN; i++) {
                String linie = reader.nextLine();
                String[] inputs = linie.split(" ");
                for (int j = 1; j <= M; j++) {
                    A_secv[i][j] = Integer.parseInt(inputs[j - 1]);
                    A_paralel[i][j] = Integer.parseInt(inputs[j - 1]);
                }
            }

            if (reader.hasNextLine()) {
                for (int i = 0; i < 3; i++) { // citeste liniile nucleului
                    String data = reader.nextLine();
                    String[] linie = data.split(" ");
                    for (int j = 0; j < 3; j++) {
                        K[i][j] = Integer.parseInt(linie[j]);
                    }
                }
            }
            reader.close();
            } catch (FileNotFoundException e) {
                System.out.println("Eroare la citire");
                e.printStackTrace();
            }
    }

    public static int singlePixelConvolution(int x, int y, int[] previousLines, int[] currentLine, int[][] A) {
        int output = 0;
        // Compute neighbours above
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 3; j++) {
//                int a = j - y - 1 + j;
                int a = y + j - 1;
                output += K[i][j] * previousLines[a];
            }
        }
        // Compute current neighbours and element
        for (int i = 0; i < 3; i++) {
            int a = y - 1 + i;
            output = K[1][i] * currentLine[a];
        }
        // Compute neighbours below
        for (int i = 2; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int a = x - 1 + i;
                int b = y - 1 + j;
                output += K[i][j] * A[a][b];
            }
        }
        return output;
    }

    public static class MyThread extends Thread {
        private int start, end, N, M;
        private int[][]A, K;
        MyThread(int start, int end, int[][] A, int[][] K, int N, int M) {
            this.start = start;
            this.end = end;
            this.A = A;
            this.K = K;
            this.N = N;
            this.M = M;
        }

        @Override
        public void run() {
            int[] previousLine = new int[M + 2];
            int[] currentLine = new int[M + 2];
            Queue<Integer> frontierValues = new LinkedList<>();

            // Copiază vecinii în vectorul auxiliar anterior
            System.arraycopy(A[start - 1], 0, previousLine, 0, M + 2);

            for (int i = start; i < end; i++) {
                // Copiază linia curentă în vectorul auxiliar curent
                System.arraycopy(A[i], 0, currentLine, 0, M + 2);

                for (int j = 1; j < M + 1; j++) {
                    int number = singlePixelConvolution(i, j, previousLine, currentLine, A);
                    if ((i - start < 1) || (end - i <= 1)) {
                        frontierValues.add(number);
                    } else {
                        A[i][j] = number;
                    }
                }

                // Actualizează vectorul auxiliar anterior și vectorul auxiliar curent
                System.arraycopy(previousLine, 0, currentLine, 0, M + 2);
            }

            try {
                barrier.await();
                for (int i = start; i < end; i++) {
                    for (int j = 1; j < M + 1; j++) {
                        if ((i - start < 1) || (end - i <= 1)) {
                                A[i][j] = frontierValues.element();
                        }
                    }
                }
            } catch (BrokenBarrierException | InterruptedException e) {
                e.printStackTrace();
            }
        }

//        public void run() {
//            int[][] previousLines = new int[2][M + 2];
//            int[] currentLine = new int[M + 2];
//            Queue<Integer> frontierValues = new LinkedList<>();
//
//            // Copiază vecinii în vectorii auxiliari manual
//            for (int i = start - 1; i <= start; i++) {
//                for (int j = 0; j <= M + 1; j++) {
//                    previousLines[i - start + 1][j] = A[i][j];
//                }
//            }
//            System.out.println(previousLines[1][0]);
//
//            for (int i = start; i < end; i++) {
//                // Copiază linia curentă într-un vector auxiliar manual
//                for (int j = 0; j <= M + 1; j++) {
//                    currentLine[j] = A[i][j];
//                }
//
//                for (int j = 1; j < M + 1; j++) {
//                    int number = singlePixelConvolution(i, j, previousLines, currentLine, A);
//                    if ((i - start < 1) || (end - i <= 1)) {
//                        frontierValues.add(number);
//                    } else {
//                        A[i][j] = number;
//                    }
//                }
//
//                // Actualizează vectorii auxiliari manual
//                for (int j = 0; j <= M + 1; j++) {
//                    previousLines[0][j] = previousLines[1][j];
//                    previousLines[1][j] = currentLine[j];
//                }
//            }
//
//            try {
//                barrier.await();
//                for (int i = start; i < end; i++) {
//                    for (int j = 1; j < M + 1; j++) {
//                        if ((i - start < 1) || (end - i <= 1)) {
//                            synchronized (frontierValues) {
//                                A[i][j] = frontierValues.poll();
//                            }
//                        }
//                    }
//                }
//            } catch (BrokenBarrierException | InterruptedException e) {
//                e.printStackTrace();
//            }
//            Queue<Integer> valoriFrontiera = new LinkedList<>();
//            int[][] previous = new int[2][M + 2]; // liniile vecine
//            int[] current = new int[M + 2]; // linia curenta
//
//            for (int i = 0; i <= M + 1; i++) // linia anterioara
//                previous[1][i] = A[start - 1][i];
//
//            for (int i = start; i < end; i++) { // parcurgem liniile pentru care threadul curent trebuie sa calculeze convolutia
//                for (int j = 0; j <= M + 1; j++)
//                    current[j] = A[i][j]; // linia curenta
//
//                for (int j = 1; j <= M ; j++) { // calculam convolutia
//                    int number = computeKernel(i, j, A, K);
//                    if ((i - start < 1) || (end - i <= 1)) // daca suntem pe margine
//                        valoriFrontiera.add(number); // adaugam in frontiera
//                    else
//                        A[i][j] = number; // preia valoarea returnata
//                }
//                for (int j = 0; j <= M + 1; j++) // actualizam linia anterioara cu valorile liniei curente
//                    previous[0][j] = previous[1][j];
//                for (int j = 0; j <= M + 1; j++)
//                    previous[1][j] = current[j];
//
//            }
//
//            try {
//                barrier.await(); // bariera pentru a sincroniza threadurile
//                for (int i = start; i < end; i++)
//                    for (int j = 1; j <= M ; j++)
//                        if ((i - start < 1) || (end - i <= 1)) { // actualizam elementele de pe margine cu valorile din coada
//                            A[i][j] = valoriFrontiera.element();
//                            valoriFrontiera.remove();
//                        }
//            } catch (BrokenBarrierException | InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }


    private static void bordareMatrice(int[][] matrice, int n, int m) { // bordam matricea
        for (int j = 1; j <= m; j++) { // bordam cu o linie la inceput si la final
            matrice[0][j] = matrice[1][j];
            matrice[n + 1][j] = matrice[n][j];
        }
        for (int i = 1; i <= n; i++) { // bordam cu o coloana la inceput si la fina;
            matrice[i][0] = matrice[i][1];
            matrice[i][m + 1] = matrice[i][m];
        }
        // bordam colturile
        matrice[0][0] = matrice[1][1];
        matrice[0][m + 1] = matrice[1][m];
        matrice[n + 1][0] = matrice[n][1];
        matrice[n + 1][m + 1] = matrice[n][m];
    }

    private static int computeKernel(int i, int j, int[][] A, int[][] Kernel) {
        // calculeaza convolutia pentru elementul de pe pozitia (i,j) si un Kernel de dimensiune 3 * 3
        return  A[i][j] * Kernel[1][1] +
                A[i][j - 1] * Kernel[1][0] +
                A[i][j + 1] * Kernel[1][2] +
                A[i - 1][j - 1] * Kernel[0][0] +
                A[i - 1][j] * Kernel[0][1] +
                A[i - 1][j + 1] * Kernel[0][2] +
                A[i + 1][j] * Kernel[2][1] +
                A[i + 1][j + 1] * Kernel[2][2] +
                A[i + 1][j - 1] * Kernel[2][0];
    }

    private static void afisare(String numeFisier, int [][] A){
        try { // scrie in fisierul dat ca parametru elementele matricii A
            BufferedWriter bw = new BufferedWriter(new FileWriter(numeFisier));
            for (int i = 1 ; i <= N  ; i++) {
                for (int j = 1 ; j <= M  ; j++) {
                    bw.write(A[i][j] + " ");
                }
                bw.newLine();
            }
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws FileNotFoundException {

        String numeFisier = "C:\\FACULTATE\\Anul 3\\Semestrul 1\\Programare Paralela si Distribuita\\Tema2_Java\\src\\main\\java\\date.txt";
        citire(numeFisier);
        bordareMatrice(A_secv, N, M);
        bordareMatrice(A_paralel, N, M);

        int[][] copieM = new int [N+2][N+2];
        int[] v1 = new int[M + 2];

        long start_time = System.nanoTime();

        int[][] lines = new int[3 * N][M + 2]; // matrice care retine liniile cu valorile initiale
        // pt linia i, va memora in matrice liniile vecine pe pozitiile 3 * (i - 1), 3 * (i - 1) + 1, 3 * (i - 1) + 2
        for(int i = 1; i <= N; i++) {
            for (int j = 1; j <= M + 1; j++)
            {
                lines[3 * (i-1)][j] = A_secv[i-1][j]; // linia de dinainte
                lines[3 * (i-1) + 1][j] = A_secv[i][j]; // linia curenta
                lines[3 * (i-1) + 2][j] = A_secv[i+1][j]; // linia de dupa
            }
        }
        for(int i = 1; i <= N; i++) {
            for (int j = 1; j <= M ; j++)
            {
                A_secv[i][j] = K[0][0] * lines[3 * (i-1)][j-1] + K[0][1] * lines[3 * (i-1)][j] + K[0][2] * lines[3 * (i-1)][j + 1]+
                        K[1][0] * lines[3 * (i-1) + 1][j-1] + K[1][1] * lines[3 * (i-1)+1][j] + K[1][2] * lines[3 * (i-1)+1][j+1]+
                K[2][0] * lines[3 * (i-1) + 2][j-1] + K[2][1] * lines[3 * (i-1)+2][j] + K[2][2] * lines[3 * (i-1)+2][j+1];
            }
        }


//        for(int i = 1; i <= N; i++)
//            for(int j = 1; j <= M; j++)
//                copieM[i][j] = computeKernel(i,j,A_secv, K);
//        for(int i = 1; i <= N; i++)
//            for(int j = 1; j <= M; j++)
//                A_secv[i][j] = copieM[i][j];

        long end_time = System.nanoTime();
        System.out.println("elapsed_time secv: " + (double) (end_time - start_time) / 1000000);
        afisare("C:\\FACULTATE\\Anul 3\\Semestrul 1\\Programare Paralela si Distribuita\\Tema2_Java\\src\\main\\java\\outputSecvential", A_secv);


        p = 2; // nr threaduri
        MyThread[] threads = new MyThread[p];
        barrier = new CyclicBarrier(p);
//        System.out.println(N);
        int c = N / p, r = N  % p;
        int start = 1, end;
        start_time = System.nanoTime();
        for (int i = 0; i < p; i++) {
            end = start + c;
            if (r > 0) {
                r--;
                end++;
            }
            threads[i] = new MyThread(start, end, A_paralel, K, N, M);
            threads[i].start();
            start = end;
        }
        for (int i = 0; i < p; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        end_time = System.nanoTime();
        System.out.println("elapsed_time thread: " + (double) (end_time - start_time) / 1000000);

        int ok = 1;
        for (int i = 1; i <= N; i++)
            for (int j = 1; j <= M; j++)
                if (A_secv[i][j] != A_paralel[i][j]) {
                    ok = 0;
                    System.out.println(A_paralel[i][j]);
                    System.out.println(A_secv[i][j]);
                    break;
                }

        if (ok == 1)
            System.out.println("Cele doua matrice au valori egale!");
        else
            System.out.println("Cele doua matrici nu sunt egale!");

        afisare("C:\\FACULTATE\\Anul 3\\Semestrul 1\\Programare Paralela si Distribuita\\Tema2_Java\\src\\main\\java\\output", A_paralel);
    }

}