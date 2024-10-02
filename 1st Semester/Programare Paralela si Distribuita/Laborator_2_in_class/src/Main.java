import java.util.Random;
import java.util.SortedMap;

public class Main {
    private static class MyThread extends Thread{
        int id;
        int start;
        int end;
        int A[];
        int B[];
        int C[];
        public MyThread(int id){
            this.id = id;
        }

        public MyThread(int id, int start, int end, int A[], int B[], int C[]){
            this.id = id;
            this.start = start;
            this.end = end;
            this.A = A;
            this.B = B;
            this.C = C;
        }

        @Override
        public void run() {
//            System.out.println("Hey! Eu sunt thread-ul cu id-ul " + id);
            for(int i = start; i < end; i++)
                C[i] = A[i] + B[i];
        }
    }

    private static class MyThread2 extends Thread{
        int id;
        int N;
        int p;
        int A[];
        int B[];
        int C[];


        public MyThread2(int id, int N, int p, int A[], int B[], int C[]){
            this.id = id;
            this.N = N;
            this.p = p;
            this.A = A;
            this.B = B;
            this.C = C;
        }

        @Override
        public void run() {
//            System.out.println("Hey! Eu sunt thread-ul cu id-ul " + id);
            for(int i = id; i < N; i+=p){
                C[i] = A[i] + B[i];
            }

        }
    }

    public static void main(String[] args) throws InterruptedException {
        int p=4;
        Thread[] threads = new MyThread[p];
        Thread[] threads2 = new MyThread2[p];

        Random rand = new Random();
        int N = 10, L = 100;

        int[] A = new int[N];
        int[] B = new int[N];
        int[] C = new int[N];

        for (int i = 0; i < A.length; i++) {
            A[i] = rand.nextInt(L) + 1;
            B[i] = rand.nextInt(L) + 1;
            C[i] = 0;
        }

        int start = 0, end = 0;
        int c = N/p, r = N%p;


        for(int i = 0; i < p; ++i){
            end = start + c;
            if(r > 0)
            {
                r--;
                end++;
//                System.out.println(r + " " + end);
            }
            threads[i] = new MyThread(i, start, end, A, B, C);
            threads[i].start();
            start = end;
        }

        for(int i = 0; i < p; ++i){
            threads2[i] = new MyThread2(i, N, p, A, B, C);
            threads2[i].start();
        }


        for(int i = 0; i < p; ++i){
            threads2[i].join();
        }

        for(int i = 0; i < N;i++){
            System.out.print(A[i] + " ");
        }
        System.out.println();
        for(int i = 0; i < N;i++){
            System.out.print(B[i] + " ");
        }
        System.out.println();

        for(int i = 0; i < N;i++){
            System.out.print(C[i] + " ");
        }
    }
}
