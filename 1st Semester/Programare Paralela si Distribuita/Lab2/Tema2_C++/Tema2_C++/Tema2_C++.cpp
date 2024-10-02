#include <iostream>
#include <fstream>
#include <vector>
#include <thread>
#include <chrono>
#include "MyBarrier.h"
#include <queue>

#define N 1000
#define M 1000
#define n 3
#define m 3
#define p 2


MyBarrier barrier(p);

using namespace std;
using namespace std::chrono;

void bordareMatrice(int** A, int a, int b) {
    // Bordam cu o linie la inceput si la final
    for (int j = 1; j <= b; j++) {
        A[0][j] = A[1][j];
        A[a + 1][j] = A[a][j];
    }

    // Bordam cu o coloana la inceput si la final
    for (int i = 1; i <= a; i++) {
        A[i][0] = A[i][1];
        A[i][b + 1] = A[i][b];
    }

    // Bordam colturile
    A[0][0] = A[1][1];
    A[0][b + 1] = A[1][b];
    A[a + 1][0] = A[a][1];
    A[a + 1][b + 1] = A[a][b];
}

int computeKernel(int i, int j, int** K, int* previousLines, int* currentLine, int* nextLine) {
    // Calculează convoluția pentru elementul de pe poziția i și j
    return K[0][0] * previousLines[j - 1] + K[0][1] * previousLines[j] + K[0][2] * previousLines[j + 1] +
        K[1][0] * currentLine[j - 1] + K[1][1] * currentLine[j] + K[1][2] * currentLine[j + 1] +
        K[2][0] * nextLine[j - 1] + K[2][1] * nextLine[j] + K[2][2] * nextLine[j + 1];
}


void run(int start, int end, int** A, int** K) {
    int previousLine[M + 2];
    int currentLine[M + 2];
    int lastLine[M + 2];

    // Copiază liniile vecine în vectorii auxiliari
    std::copy(&A[start - 1][0], &A[start - 1][0] + M + 2, &previousLine[0]);
    std::copy(&A[end][0], &A[end][0] + M + 2, &lastLine[0]);

    try {
        barrier.wait();
    }
    catch (std::exception& e) {
        std::cerr << "Exception caught: " << e.what() << std::endl;
    }

    for (int i = start; i < end; i++) {
        // Copiază linia următoare în nextLine
        std::copy(&A[i ][0], &A[i ][0] + M + 2, &currentLine[0]);

        for (int j = 1; j < M + 1; j++) {
            int nextLine [M+2];
            if (i == end - 1) {
                std::copy(&lastLine[0], &lastLine[0] + M + 2, &nextLine[0]);
            }
            else {
                std::copy(&A[i + 1][0], &A[i + 1][0] + M + 2, &nextLine[0]);
            }

            A[i][j] = computeKernel(i, j,K, previousLine, currentLine, nextLine);
        }

        // Actualizează vectorul auxiliar anterior și vectorul auxiliar curent
        std::copy(&previousLine[0], &previousLine[0] + M + 2, &currentLine[0]);
    }

}


int main() {
    int** A_secv = new int* [N+2];
    int** A_paralel = new int* [N+2];
    for (int i = 0; i < N + 2; ++i) {
        A_secv[i] = new int[M+2];
    }
    for (int i = 0; i < N + 2; ++i) {
        A_paralel[i] = new int[M + 2];
    }

    int** K = new int* [n];
    for (int i = 0; i < n; ++i) {
        K[i] = new int[m];
    }

    // CITIRE
    ifstream file("C:\\FACULTATE\\Anul 3\\Semestrul 1\\Programare Paralela si Distribuita\\Tema2_C++\\Tema2_C++\\date2.txt");
    if (file.is_open()) {
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= M; j++) {
                file >> A_secv[i][j]; // elementele matricei initiale
                A_paralel[i][j] = A_secv[i][j];
            }
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                file >> K[i][j]; // elementele nucleului
            }
        }
        file.close();
    }
    else {
        cerr << "Eroare la citire\n";
    }

    bordareMatrice(A_secv, N, M);
    bordareMatrice(A_paralel, N, M);


    // PARCURGEREA SECVENTIALA
    auto t_start = high_resolution_clock::now();
    int lines[3 * N][M + 2];

    // Inițializarea matricei lines cu valorile liniilor vecine
    for (int i = 1; i <= N; i++) {
        for (int j = 1; j <= M + 1; j++) {
            lines[3 * (i - 1)][j] = A_secv[i - 1][j];   // Linia de dinainte
            lines[3 * (i - 1) + 1][j] = A_secv[i][j];   // Linia curentă
            lines[3 * (i - 1) + 2][j] = A_secv[i + 1][j]; // Linia de după
        }
    }

    // Calcularea noilor valori pentru matricea A_secv folosind Kernel
    for (int i = 1; i <= N; i++) {
        for (int j = 1; j <= M; j++) {
            A_secv[i][j] = K[0][0] * lines[3 * (i - 1)][j - 1] + K[0][1] * lines[3 * (i - 1)][j] + K[0][2] * lines[3 * (i - 1)][j + 1] +
                K[1][0] * lines[3 * (i - 1) + 1][j - 1] + K[1][1] * lines[3 * (i - 1) + 1][j] + K[1][2] * lines[3 * (i - 1) + 1][j + 1] +
                K[2][0] * lines[3 * (i - 1) + 2][j - 1] + K[2][1] * lines[3 * (i - 1) + 2][j] + K[2][2] * lines[3 * (i - 1) + 2][j + 1];
        }
    }

    auto t_end = chrono::high_resolution_clock::now();
    double elapsed_time_ms = chrono::duration<double, std::milli>(t_end - t_start).count();
    //cout << "elapsed_time secv = " << elapsed_time_ms << endl;

    ofstream file3("C:\\FACULTATE\\Anul 3\\Semestrul 1\\Programare Paralela si Distribuita\\Tema2_C++\\Tema2_C++\\outputSecvential.txt");
    if (file3.is_open()) {
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= M; j++) {
                file3 << A_secv[i][j] << " ";
            }
            file3 << endl;
        }
        file3.close();
    }
    else {
        cerr << "Eroare la scriere\n";
    }

    t_start = high_resolution_clock::now();
    vector<thread> th;
    int start = 1, end = 1;
    int c = N / p, r = N % p;

    for (int i = 0; i < p; i++) {
        start = end;
        end = start + c;
        if (r > 0) {
            end++;
            r--;
        }
        thread thr = thread(run, start, end, A_paralel, K);
        th.push_back(move(thr));
    }

    for (auto& thr : th) {
        if (thr.joinable())
            thr.join();
    }

    t_end = chrono::high_resolution_clock::now();
    elapsed_time_ms = chrono::duration<double, std::milli>(t_end - t_start).count();
    cout << "elapsed_time = " << elapsed_time_ms << endl;
    ofstream file2("C:\\FACULTATE\\Anul 3\\Semestrul 1\\Programare Paralela si Distribuita\\Tema2_C++\\Tema2_C++\\output.txt");
    if (file2.is_open()) {
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= M; j++) {
                file2 << A_paralel[i][j] << " ";
            }
            file2 << endl;
        }
        file2.close();
    }
    else {
        cerr << "Eroare la scriere\n";
    }


    int ok = 1;
    for (int i = 1; i <= N; i++)
        for (int j = 1; j <= M; j++)
            if (A_secv[i][j] != A_paralel[i][j])
                ok = 0;
    if (ok == 0)
        cout << "Matricile nu sunt egale!";

}
