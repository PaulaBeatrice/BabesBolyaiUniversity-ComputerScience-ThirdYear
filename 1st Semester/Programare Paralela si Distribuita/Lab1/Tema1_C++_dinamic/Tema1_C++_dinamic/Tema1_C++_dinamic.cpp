#include <iostream>
#include <fstream>
#include <chrono>
#include <thread>
#include <ctime>
#include <vector>

#define N 10000
#define M 10
#define n 5
#define m 5
#define p 16
#define lineOffset  (n-1)/2
#define columnOffset  (m-1)/2


using namespace std;
using namespace std::chrono;


int computeKernel(int x, int y, int** A, int** Kernel) {
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

void runLine(int id, int start, int end, int** A, int** K, int** R) {
    for (int i = start; i < end; i++)
        for (int j = 0; j < M; j++)
            R[i][j] = computeKernel(i, j, A, K);
}

void runCollumn(int id, int start, int end, int** A, int** K, int** R) {
    for (int j = start; j < end; j++)
        for (int i = 0; i < N; i++)
            R[i][j] = computeKernel(i, j, A, K);
}


int resolve() {
    int** A = new int* [N];
    for (int i = 0; i < N; ++i) {
        A[i] = new int[M];
    }

    int** Kernel = new int* [n];
    for (int i = 0; i < n; ++i) {
        Kernel[i] = new int[m];
    }

    int** R = new int* [N];
    for (int i = 0; i < N; ++i) {
        R[i] = new int[M];
    }

    // CITIRE
    ifstream file("C:\\FACULTATE\\Anul 3\\Semestrul 1\\Programare Paralela si Distribuita\\Tema1_C++\\date4.txt");
    if (file.is_open()) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                file >> A[i][j]; // elementele matricei initiale
            }
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                file >> Kernel[i][j]; // elementele nucleului
            }
        }
        file.close();
    }
    else {
        cerr << "Eroare la citire\n";
    }

    auto t_start = high_resolution_clock::now();
    thread th[p];
    int start, end = 0;
    int c = N / p, r = N % p;

    for (int i = 0; i < p; i++) {
        start = end;
        end = start + c;
        if (r > 0) {
            end++;
            r--;
        }
        th[i] = thread(runLine, i, start, end ,A, Kernel, R);
        //th[i] = thread(runCollumn, i, start, end, A, Kernel, R);
    }

    for (int i = 0; i < p; i++) {
        th[i].join();
    }

    auto t_end = chrono::high_resolution_clock::now();
    double elapsed_time_ms = chrono::duration<double, std::milli>(t_end - t_start).count();
    cout << "elapsed_time = " << elapsed_time_ms << endl;


    // AFISARE
    ofstream file2("C:\\FACULTATE\\Anul 3\\Semestrul 1\\Programare Paralela si Distribuita\\Tema1_C++\\output.txt");
    if (file2.is_open()) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                file2 << R[i][j] << " ";
            }
            file2 << endl;
        }
        file2.close();
    }
    else {
        cerr << "Eroare la scriere\n";
    }
    return elapsed_time_ms;
}

void Test() {
    resolve();
    int T[N][M];
    for (int i = 0; i < N; i++)
        for (int j = 0; j < M; j++)
            T[i][j] = computeKernel(i, j);
    for (int i = 0; i < N; i++)
        for (int j = 0; j < M; j++)
            if (T[i][j] != R[i][j]) {
                cout << "GRESIT";
            }
    cout << "CORECT";
}

int main()
{
    int elapsed_time = resolve();
    cout << "Timp de executie pentru " << elapsed_time << " ms" << endl;

}