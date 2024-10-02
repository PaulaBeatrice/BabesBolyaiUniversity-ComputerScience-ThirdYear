import java.io.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Main2 {
    private static CyclicBarrier barrier;
    public static class MyThread extends Thread {
        private int id, start, end, N, M;
        private int[][]A, K;
        MyThread(int id, int start, int end, int[][] A, int[][] K, int N, int M) {
            this.id = id;
            this.start = start;
            this.end = end;
            this.A = A;
            this.K = K;
            this.N = N;
            this.M = M;
        }

        @Override
        public void run() {
            Queue<Integer> valoriFrontiera = new LinkedList<>();
            int[][] previous = new int[2][M + 2];
            int[] current = new int[M + 2];

            for (int i = 0; i < M + 2; i++)
                previous[1][i] = A[start - 1][i];

            for (int i = start; i < end; i++) {
                for (int j = 0; j < M + 2; j++)
                    current[j] = A[i][j];

                for (int j = 2; j <= M - 1; j++) {
                    int number = convolutieKernel3(i, j, A, K);
                    if ((i - start < 1) || (end - i <= 1))
                        valoriFrontiera.add(number);
                    else
                        A[i][j] = number;
                }
                for (int j = 0; j < M + 2; j++)
                    previous[0][j] = previous[1][j];
                for (int j = 0; j < M + 2; j++)
                    previous[1][j] = current[j];

            }

            try {
                barrier.await();
                for (int i = start; i < end; i++)
                    for (int j = 2; j <= M - 1; j++)
                        if ((i - start < 1) || (end - i <= 1)) {
                            A[i][j] = valoriFrontiera.element();
                            valoriFrontiera.remove();
                        }
            } catch (BrokenBarrierException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) throws FileNotFoundException {

        String numeFisier = "C:\\FACULTATE\\Anul 3\\Semestrul 1\\Programare Paralela si Distribuita\\Tema2_Java\\src\\main\\java\\date.txt";
        Scanner scanner = new Scanner(new File(numeFisier));
        String NsiM = scanner.nextLine();
        int N = Integer.parseInt(NsiM.split(" ")[0]);
        int M = Integer.parseInt(NsiM.split(" ")[1]) + 2;
        int[][] matriceSecvential = new int[N + 5][M + 5];
        int[][] matriceThread = new int[N + 5][M + 5];
        int[][] K = new int[3][3];
        N = 2;


        //------------------------------------citesc matricea din fisier---------------------------------------------------
        while (scanner.hasNextLine()) {
            String linie = scanner.nextLine();
            String[] inputs = linie.split(" ");
            for (int i = 0; i < inputs.length; i++) {
                matriceSecvential[N][i + 2] = Integer.parseInt(inputs[i]);
                matriceThread[N][i + 2] = Integer.parseInt(inputs[i]);
            }
            N = N + 1;
        }
        scanner.close();
        //------------------------------------declar matricea KERNEL-----------------------------------------
        matriceKernel(K);

        //-----------------------------------bordez matricea initiala-------------------------------------------
        bordareMatrice(matriceSecvential, N, M);
        bordareMatrice(matriceThread, N, M);
        int[] v1 = new int[M + 5];
        int[] v2 = new int[M + 5];
        //-----------------------------------calculez matricea rezultata in urma aplicarii convolutiei--------------
        ///------------------------------------------SECVENTIAL----------------------------------------------------
        long timp1 = System.nanoTime();
        for (int i = 2; i <= N - 1; i++) {
            for (int j = 2; j <= M - 1; j++)
                v2[j] = v1[j];
            for (int j = 2; j <= M - 1; j++)
                v1[j] = convolutieKernel3(i, j, matriceSecvential, K);
            for (int j = 2; j <= M - 1; j++)
                matriceSecvential[i - 1][j] = v2[j];
        }
        for (int j = 2; j <= M - 1; j++)
            matriceSecvential[N - 1][j] = v1[j];
        long timp2 = System.nanoTime();
        System.out.println("Timpul SECVENTIAL: " + (double) (timp2 - timp1) / 1000000);

        //-------------------------------------afisez matricea SECVENTIALA rezultat intr-un fisier output-------------
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\FACULTATE\\Anul 3\\Semestrul 1\\Programare Paralela si Distribuita\\Tema2_Java\\src\\main\\java\\outputSecvential"));
            for (int i = 2 ; i <= N - 1 ; i++) {
                for (int j = 2 ; j <= M - 1 ; j++) {
                    bw.write(matriceSecvential[i][j] + " ");
                }
                bw.newLine();
            }
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //--------------------------------------THREAD-----------------------------------------------------
        System.out.print("Introduceti nr de thread uri: ");
        Scanner scannerInt = new Scanner(System.in);
        int P = scannerInt.nextInt();
        MyThread[] threads = new MyThread[P];
        barrier = new CyclicBarrier(P);
        int intreg = (N - 2) / P;
        int rest = (N - 2) % P;
        int start = 2, end;
        long timpThread1 = System.nanoTime();
        for (int i = 0; i < P; i++) {
            end = start + intreg;
            if (rest > 0) {
                rest--;
                end++;
            }
            threads[i] = new MyThread(i, start, end, matriceThread, K, N, M);
            threads[i].start();
            start = end;
        }
        for (int i = 0; i < P; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        long timpThread2 = System.nanoTime();
        System.out.println("Timpul THREAD: " + (double) (timpThread2 - timpThread1) / 1000000);
        if (verificareEgalitate(matriceThread, matriceSecvential, N, M) == 1)
            System.out.println("Cele doua matrice au valori egale!");
        else System.out.println("Nu sunt egale!");

        //-----------------------------------afisez matricea SECVENTIALA rezultat intr-un fisier output----------
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\FACULTATE\\Anul 3\\Semestrul 1\\Programare Paralela si Distribuita\\Tema2_Java\\src\\main\\java\\output.txt"));
            for (int i = 2 ; i <= N - 1 ; i++) {
                for (int j = 2 ; j <= M - 1 ; j++) {
                    bw.write(matriceThread[i][j] + " ");
                }
                bw.newLine();
            }
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void bordareMatrice(int[][] matrice, int n, int m) {
        for (int j = 2; j < m; j++) {
            matrice[0][j] = matrice[2][j];matrice[1][j] = matrice[2][j];matrice[n][j] = matrice[n - 1][j];matrice[n + 1][j] = matrice[n - 1][j];
        }

        for (int i = 2; i < n; i++) {
            matrice[i][0] = matrice[i][2];matrice[i][1] = matrice[i][2];matrice[i][m] = matrice[i][m - 1];matrice[i][m + 1] = matrice[i][m - 1];
        }

        matrice[0][0] = matrice[2][2];matrice[0][1] = matrice[2][2];matrice[1][0] = matrice[2][2];matrice[1][1] = matrice[2][2];
        matrice[n][0] = matrice[n - 1][2];matrice[n][1] = matrice[n - 1][2];matrice[n + 1][0] = matrice[n - 1][2];matrice[n + 1][1] = matrice[n - 1][2];
        matrice[0][m] = matrice[2][m - 1];matrice[0][m + 1] = matrice[2][m - 1];matrice[1][m] = matrice[2][m - 1];matrice[1][m + 1] = matrice[2][m - 1];
        matrice[n][m] = matrice[n - 1][m - 1];matrice[n + 1][m] = matrice[n - 1][m - 1];matrice[n][m + 1] = matrice[n - 1][m - 1];matrice[n + 1][m + 1] = matrice[n - 1][m - 1];
    }

    private static void matriceKernel(int[][] k) {
        k[0][0] = 0;k[0][1] = 0;k[0][2] = 0;
        k[1][0] = 0;k[1][1] = 1;k[1][2] = 1;
        k[2][0] = 0;k[2][1] = 0;k[2][2] = 0;
    }

    private static int verificareEgalitate(int[][] a, int[][] b, int n, int m) {
        for (int i = 2; i <= n - 1; i++)
            for (int j = 2; j <= m - 1; j++)
                if (a[i][j] != b[i][j]) return 0;
        return 1;
    }

    private static int convolutieKernel3(int i, int j, int[][] matrice, int[][] k) {
        return  matrice[i][j] * k[1][1] +
                matrice[i][j - 1] * k[1][0] +
                matrice[i][j + 1] * k[1][2] +
                matrice[i - 1][j - 1] * k[0][0] +
                matrice[i - 1][j] * k[0][1] +
                matrice[i - 1][j + 1] * k[0][2] +
                matrice[i + 1][j] * k[2][1] +
                matrice[i + 1][j + 1] * k[2][2] +
                matrice[i + 1][j - 1] * k[2][0];
    }
}