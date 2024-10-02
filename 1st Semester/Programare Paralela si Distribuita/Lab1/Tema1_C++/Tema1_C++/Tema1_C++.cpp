#include <iostream>
#include <fstream>
#include <chrono>
#include <thread>
#include <ctime>
#define N 10
#define M 10
#define n 3 
#define m 3
#define p 16
#define lineOffset  (n-1)/2
#define columnOffset  (m-1)/2


using namespace std; 
using namespace std::chrono;

int A[N][M], Kernel[n][m], R[N][M];

void citire(string path) {
    ifstream file(path);
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
}

void afisare(string path) {
    ofstream file(path); 
    if (file.is_open()) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                file << R[i][j] << " ";
            }
            file << endl;
        }
        file.close();
    }
    else {
        cerr << "Eroare la scriere\n"; 
    }
}

int computeKernel(int x, int y) {
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

void runLine(int id, int start, int end) {
    for (int i = start; i < end; i++)
        for (int j = 0; j < M; j++)
            R[i][j] = computeKernel(i, j);
}

void runCollumn(int id, int start, int end) {
    for (int j = start; j < end; j++)
        for (int i = 0; i < N; i++)
            R[i][j] = computeKernel(i, j);
}




int resolve() {
    int res = 0;
    citire("D:\\FACULTATE\\FACULTATE\\Anul 3\\Semestrul 1\\Programare Paralela si Distribuita\\Tema1_C++\\date.txt");

    int secv = 1;
    if (secv == 0)
    {
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
            th[i] = thread(runLine, i, start, end);
            //th[i] = thread(runCollumn, i, start, end);
        }

        for (int i = 0; i < p; i++) {
            th[i].join();
        }

        auto t_end = chrono::high_resolution_clock::now();
        double elapsed_time_ms = chrono::duration<double, std::milli>(t_end - t_start).count();
        cout << "elapsed_time = " << elapsed_time_ms << endl;
        res = elapsed_time_ms;
    }
    else {
        auto t_start = high_resolution_clock::now();

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                R[i][j] = computeKernel(i, j);
            }
        }

        auto t_end = high_resolution_clock::now();
        auto elapsed_time = chrono::duration<double, std::milli>(t_end - t_start).count();

        cout << elapsed_time << endl;
        res = elapsed_time;
    }


    afisare("D:\\FACULTATE\\FACULTATE\\Anul 3\\Semestrul 1\\Programare Paralela si Distribuita\\Tema1_C++\\output.txt");
    return res;
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
   /* int elapsed_time = resolve();
    cout << "Timp de executie pentru " << elapsed_time << " ms" << endl;*/
    Test();
}