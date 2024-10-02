#include <iostream>
#include <fstream>
#include <chrono>
#include <thread>
#include <ctime>
#include <sstream>
#define N 10
#define M 10000
#define n 5 
#define m 5
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

void run(int id, int i_start, int j_start, int dim) {
    for (int i = i_start; i < N; i++)
        for (int j = 0; j < M; j++) {
            if ((i == i_start && j >= j_start || i > i_start) && dim != 0) {
                dim--;
                R[i][j] = computeKernel(i, j);
            }
            if (dim == 0) {
                return;
            }
        }
}
string getNextIndexes(int i_start, int j_start, int dim) {
    for (int i = i_start; i < N; i++)
        for (int j = 0; j < M; j++) {
            if ((i == i_start && j >= j_start || i > i_start) && dim != 0) {
                dim--;
            }
            if (dim == 0) {
                ostringstream oss;
                oss << i << " " << j;
                return oss.str();
            }
        }
    return "";
}





int resolve() {
    int res = 0;
    citire("C:\\FACULTATE\\Anul 3\\Semestrul 1\\Programare Paralela si Distribuita\\Tema1_C++\\date3.txt");

    int secv = 1;
    if (secv == 0)
    {
        auto t_start = high_resolution_clock::now();
        thread th[p];
        int i_start = 0, j_start = 0;
        int c = N * M / p, r = N * M % p;

        for (int i = 0; i < p; i++) {
            int dim = c;
            if (r > 0) {
                dim++;
                r--;
            }
            th[i] = thread(run, i, i_start, j_start, dim);

            string newIndexes = getNextIndexes(i_start, j_start, dim);
            istringstream iss(newIndexes);
            string i_str, j_str;
            iss >> i_str >> j_str;
            i_start = stoi(i_str);
            j_start = stoi(j_str) + 1;
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


    afisare("C:\\FACULTATE\\Anul 3\\Semestrul 1\\Programare Paralela si Distribuita\\Tema1_C++\\output.txt");
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
    int elapsed_time = resolve();
    cout << "Timp de executie pentru " << elapsed_time << " ms" << endl;
}