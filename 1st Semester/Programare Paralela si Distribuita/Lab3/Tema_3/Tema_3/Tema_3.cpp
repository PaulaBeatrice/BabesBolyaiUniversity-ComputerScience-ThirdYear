﻿#include <iostream>
#include <mpi.h>
#include <fstream>
#include <vector>
#include <chrono>

#define N 1000
#define M 1000
#define n 3
#define m 3
#define lineOffset 1
#define columnOffset 1

using namespace std;
using namespace std::chrono;


int computeKernel(int x, int y, int A[][M], int K[][3]) {
    int prevLine, nextLine, prevColumn, nextColumn;

    if (x == 0)
        prevLine = 0;
    else
        prevLine = x - 1;

    if (x == N - 1)
        nextLine = N - 1;
    else
        nextLine = x + 1;

    if (y == 0)
        prevColumn = 0;
    else
        prevColumn = y - 1;

    if (y == M - 1)
        nextColumn = M - 1;
    else
        nextColumn = y + 1;


    //cout << x << " " << y << " " << prevLine << " " << nextLine << " " << prevColumn << " " << nextColumn << '\n';

    return K[0][0] * A[prevLine][prevColumn] + K[0][1] * A[prevLine][y] + K[0][2] * A[prevLine][nextColumn] +
        K[1][0] * A[x][prevColumn] + K[1][1] * A[x][y] + K[1][2] * A[x][nextColumn] +
        K[2][0] * A[nextLine][prevColumn] + K[2][1] * A[nextLine][y] + K[2][2] * A[nextLine][nextColumn];
}


int main()
{

    int K[3][3], A[N][M], R[N][M];

    MPI_Status status;

    MPI_Init(NULL, NULL);

    int world_size;
    MPI_Comm_size(MPI_COMM_WORLD, &world_size);

    int nr = N / (world_size - 1);
    int r = N % (world_size - 1);
    int lstart = 0, lend = 0;

    int world_rank;
    MPI_Comm_rank(MPI_COMM_WORLD, &world_rank);

    if (world_rank == 0) { // procesul master
        // start T1 (citire -> afisare)
        auto t1_start = high_resolution_clock::now();


        string fisierCitire = "";
        if (N == 10)
            fisierCitire = "D:\\FACULTATE\\FACULTATE\\Anul 3\\Semestrul 1\\Programare Paralela si Distribuita\\Tema_3\\date.txt";
        else
            fisierCitire = "D:\\FACULTATE\\FACULTATE\\Anul 3\\Semestrul 1\\Programare Paralela si Distribuita\\Tema_3\\date2.txt";
        ifstream file(fisierCitire);
        if (file.is_open()) {
            // citeste matricea kernel si o transmitem celorlalte procese
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    file >> K[i][j]; // elementele nucleului
                }
            }
            MPI_Bcast(&K, 9, MPI_INT, 0, MPI_COMM_WORLD); // trimit kernel-ul la restul proceselor

            // start T2 (calcul)
            auto t2_start = high_resolution_clock::now();

            // citeste cate nr linii si le transmite procesului i
            for (int i = 1; i < world_size; i++) { // indicele procesului la care se va transmite
                lend = lstart + nr;

                if (r > 0) {
                    r--;
                    lend++;
                }

                MPI_Send(&lstart, 1, MPI_INT, i, 0, MPI_COMM_WORLD); // transmitem indicele de start
                MPI_Send(&lend, 1, MPI_INT, i, 0, MPI_COMM_WORLD); // transmitem indicele de end

                for (int a = lstart; a < lend; a++)
                { // transmitem liniile de la indicele start la end
                    for (int b = 0; b < M; b++)
                        file >> A[a][b];
                    MPI_Send(&A[a], M, MPI_INT, i, 0, MPI_COMM_WORLD);
                }

                lstart = lend;
            }

            file.close();

            // stop T2
            auto t2_end = high_resolution_clock::now();
            double elapsed_time_ms_2 = chrono::duration<double, std::milli>(t2_end - t2_start).count();
            cout << "T2 (calcul) = " << elapsed_time_ms_2 << endl;

        }
        else {
            cerr << "Eroare la citire" + fisierCitire + "\n";
        }

        ofstream outputFile("output.txt");
        if (outputFile.is_open()) {
            // Primește liniile matricei R de la fiecare proces worker și le scrie în fișier
            for (int i = 1; i < world_size; i++) {
                MPI_Recv(&lstart, 1, MPI_INT, i, 0, MPI_COMM_WORLD, &status);
                MPI_Recv(&lend, 1, MPI_INT, i, 0, MPI_COMM_WORLD, &status);
                for (int a = lstart; a < lend; a++) {
                    MPI_Recv(&R[a], M, MPI_INT, i, 0, MPI_COMM_WORLD, &status);
                    for (int b = 0; b < M; b++) {
                        outputFile << R[a][b] << " "; // scrie valorile în fișier
                    }
                    outputFile << endl; // treci la următoarea linie în fișier
                }
            }
            outputFile.close(); // închide fișierul de ieșire
        }


        // stop T1
        auto t1_end = chrono::high_resolution_clock::now();
        double elapsed_time_ms_1 = chrono::duration<double, std::milli>(t1_end - t1_start).count();
        cout << "T1 (citire -> scriere) = " << elapsed_time_ms_1 << endl;


        // verificarea corectitudinii
        string fisier = "";
        if (N == 10)
            fisier = "outputCorect.txt";
        else
            fisier = "outputCorect2.txt";
        vector<vector<int>> B(N, vector<int>(M, 0));

        ifstream file2(fisier);

        if (file2.is_open()) {
            // Citeste rezultatul corect de la laboratorul trecut
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < M; j++) {
                    file2 >> B[i][j];
                }
            }
            file2.close();
        }
        else {
            cerr << "Eroare la citire" + fisier + " \n";
        }

        // Verificați dacă matricile sunt identice
        int ok = 1;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if (R[i][j] != B[i][j]) {
                    ok = 0;
                    break; // Puteți opri verificarea dacă găsiți o diferență
                }
            }
            if (ok == 0) {
                break;
            }
        }

        if (ok == 0) {
            cout << "Matricile nu sunt identice!" << endl;
        }
        else {
            cout << "Matricile sunt identice!" << endl;
        }


    }
    else { // I am worker
        //MPI_Recv(&K, 9, MPI_INT, 0, 0, MPI_COMM_WORLD, &status); // primeste matricea kernel
        MPI_Bcast(&K, 9, MPI_INT, 0, MPI_COMM_WORLD); // preiau kernel-ul


        MPI_Recv(&lstart, 1, MPI_INT, 0, 0, MPI_COMM_WORLD, &status); // primeste indicele lstart
        MPI_Recv(&lend, 1, MPI_INT, 0, 0, MPI_COMM_WORLD, &status); // primeste indicele lend

        for (int a = lstart; a < lend; a++) {
            MPI_Recv(&A[a], M, MPI_INT, 0, 0, MPI_COMM_WORLD, &status); // primeste liniile de la lstart la lend
        }

        // trimite la vecinul anterior si la vecinul urmator
        if (world_rank > 1) // trimitem prima linie la procesul anterior
            MPI_Send(&A[lstart], M, MPI_INT, world_rank - 1, 0, MPI_COMM_WORLD);
        if (world_rank < world_size - 1) // trimitem ultima linia la urmatorul proces
            MPI_Send(&A[lend - 1], M, MPI_INT, world_rank + 1, 0, MPI_COMM_WORLD);

        // primeste de la vecinul anterior si de la vecinul urmator
        if (world_rank > 1) // primeste linia anterioara de la procesul anterior
            MPI_Recv(&A[lstart - 1], M, MPI_INT, world_rank - 1, 0, MPI_COMM_WORLD, &status);
        if (world_rank < world_size - 1) // primeste linia urmatoare de la procesul urmator
            MPI_Recv(&A[lend], M, MPI_INT, world_rank + 1, 0, MPI_COMM_WORLD, &status);

        // calculez convolutia pentru liniile transmise
        for (int a = lstart; a < lend; a++)
            for (int b = 0; b < M; b++)
                R[a][b] = computeKernel(a, b, A, K);

        MPI_Send(&lstart, 1, MPI_INT, 0, 0, MPI_COMM_WORLD); // transmit indicii liniilor
        MPI_Send(&lend, 1, MPI_INT, 0, 0, MPI_COMM_WORLD);

        for (int a = lstart; a < lend; a++) // transmit liniile calculate
        {
            MPI_Send(&R[a], M, MPI_INT, 0, 0, MPI_COMM_WORLD);
        }

    }
    MPI_Finalize();

}