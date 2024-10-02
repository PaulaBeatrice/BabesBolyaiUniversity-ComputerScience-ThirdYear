import java.io.*;
import java.util.Random;
import java.util.Scanner;

public class Main {
    private static class MyThreadLines extends Thread{
        int start;
        int end;
        int N, M, n, m;
        int A[][], K[][], R[][];

        public MyThreadLines( int start, int end, int n, int m, int n1, int m1, int[][] a, int[][] k, int[][] r) {
            this.start = start;
            this.end = end;
            N = n;
            M = m;
            this.n = n1;
            this.m = m1;
            A = a;
            K = k;
            R = r;
        }

        @Override
        public void run() { // calculeaza rezultatul pt liniile cu indicii de la start la end
            for(int i = start; i < end; i++)
                for(int j = 0; j < M; j++)
                    R[i][j] = computeKernel(i, j);
        }
    }

    private static class MyThreadCollumns extends Thread{
        int start;
        int end;
        int N, M, n, m;
        int A[][], K[][], R[][];

        public MyThreadCollumns(int start, int end, int n, int m, int n1, int m1, int[][] a, int[][] k, int[][] r) {
            this.start = start;
            this.end = end;
            N = n;
            M = m;
            this.n = n1;
            this.m = m1;
            A = a;
            K = k;
            R = r;
        }

        @Override
        public void run() { // calculeaza rezultatul pt coloanele cu indicii de la start la end
            for(int j = start; j < end; j++)
                for(int i = 0; i < N; i++)
                    R[i][j] = computeKernel(i, j);
        }
    }

    public static int computeKernel(int x, int y){
        int result = 0;
        for (int i_kernel = 0; i_kernel < n; i_kernel++) {
            for (int j_kernel = 0; j_kernel < m; j_kernel++) {   // parcurgem kernelul
                int i_matr = x - lineOffset + i_kernel;
                int j_matr = y - columnOffset + j_kernel;

                // inafara matricii
                if (i_matr < 0)
                    i_matr = 0;
                else
                if (i_matr >= N)
                    i_matr = N - 1;

                if (j_matr < 0)
                    j_matr = 0;
                else if (j_matr >= M) j_matr = M - 1;

                result += A[i_matr][j_matr] * Kernel[i_kernel][j_kernel];
            }
        }
        return result;
    }

    private static int N, M, n, m, p, lineOffset, columnOffset;
    private static int[][] A, Kernel, R;

    public static void citire(String path) {
        try {
            File fisier = new File(path);
            Scanner reader = new Scanner(fisier);

            if (reader.hasNextLine()) {
                N = Integer.parseInt(reader.nextLine()); // citeste nr de linii
                M = Integer.parseInt(reader.nextLine()); // citeste nr de coloane
            }
            A = new int[N][M]; // initializeaza matricea
            if (reader.hasNextLine()) {
                for (int i = 0; i < N; i++) { // citeste fiecare linie a matricii
                    String data = reader.nextLine();
                    String[] linie = data.split(" ");
                    for (int j = 0; j < M; j++) {
                        A[i][j] = Integer.parseInt(linie[j]);
                    }
                }
            }

            if (reader.hasNextLine()) {
                n = Integer.parseInt(reader.nextLine()); // citeste nr de linii al nucleului
                m = Integer.parseInt(reader.nextLine()); // citeste nr de coloane al nucleului
            }
            Kernel = new int[n][m]; // initializeaza nucleul

            if (reader.hasNextLine()) {
                for (int i = 0; i < n; i++) { // citeste liniile nucleului
                    String data = reader.nextLine();
                    String[] linie = data.split(" ");
                    for (int j = 0; j < m; j++) {
                        Kernel[i][j] = Integer.parseInt(linie[j]);
                    }
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Eroare la citire");
            e.printStackTrace();
        }
    }

    public static void afisare(String path, int[][] R, int N, int M) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(path));
            for(int i = 0; i < N; i++){
                for(int j = 0; j < M; j++)
                    bw.write(R[i][j] + " ");
                bw.newLine();
            }
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static double resolve() throws InterruptedException {
        citire("D:\\FACULTATE\\FACULTATE\\Anul 3\\Semestrul 1\\Programare Paralela si Distribuita\\Tema1\\src\\main\\java\\date5.txt");
        R = new int[N][M];
        p = 8; // nr threaduri
        lineOffset = (n - 1) / 2;
        columnOffset = (m - 1) / 2;
        double res ;

        int secv = 0;
        if(secv == 0)
        {
            Thread[] t = new MyThreadLines[p];

            int start, end = 0;
            int c = N / p, r = N % p;

            long startTime = System.nanoTime();

            for (int i = 0; i < t.length; i++) {
                start = end;
                end = start + c;
                if (r > 0) {
                    end++;
                    r--;
                }
                t[i] = new MyThreadLines(start, end, N,M,n,m,A,Kernel, R);
                t[i].start();
            }

            for (Thread thread : t) {
                thread.join();
            }

            long stopTime = System.nanoTime();

            System.out.println((double)(stopTime - startTime) / 1E6);
            res = (double)(stopTime - startTime) / 1E6;
        }
        else{
            long startTime = System.nanoTime();

            for (int i = 0; i < N; i++)
                for(int j = 0; j < M; j++)
                    R[i][j] = computeKernel(i,j);
            long stopTime = System.nanoTime();

            System.out.println((double)(stopTime - startTime) / 1E6);
            res = (double)(stopTime - startTime) / 1E6;
        }


        afisare("D:\\FACULTATE\\FACULTATE\\Anul 3\\Semestrul 1\\Programare Paralela si Distribuita\\Tema1\\src\\main\\java\\output.txt", R, N,M);
        return res;
    }

    public static void Test() throws InterruptedException {
        resolve();
        int[][] T = new int [N][M];
        for (int i = 0; i < N; i++)
            for(int j = 0; j < M; j++)
                T[i][j] = computeKernel(i,j);
        for (int i = 0; i < N; i++)
            for(int j = 0; j < M; j++)
                if(T[i][j] != R[i][j]){
                    System.out.println("GRESIT");
                }
        System.out.println("CORECT");
    }

    public static void main(String[] args) throws InterruptedException {
//        resolve();
        Test();
    }



}
