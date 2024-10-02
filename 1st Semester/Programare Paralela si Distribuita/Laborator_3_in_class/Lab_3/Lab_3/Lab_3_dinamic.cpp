#include <iostream>
#include <thread>
#include <ctime>
#include <chrono>

auto t_start = std::chrono::high_resolution_clock::now();

#define n 20000000
#define p 4

using namespace std;


void run(int id, int* a, int* b, int* c) {
    //cout << "hello " << id << endl;
    for (int i = id; i < n; i += p)
        c[i] = sqrt(pow(a[i], 3) + pow(b[i], 3));
}

void print(int v[]) {
    for (int i = 0; i < n; i++)
        cout << v[i] << " ";
    cout << endl;
}

int main()
{
    // declarare dinamica
    int* a = new int[n];
    int* b = new int[n];
    int* c = new int[n];
    srand(time(0));
    for (int i = 0; i < n; i++) {
        a[i] = rand() % 100;
        b[i] = rand() % 100;
    }

    thread th[p];

    for (int i = 0; i < p; i++) {
        th[i] = thread(run, i, a, b, c); // referinta la o functie, urmata de parametrii functiei
    }

    for (int i = 0; i < p; i++) {
        th[i].join();
    }

    auto t_end = chrono::high_resolution_clock::now();
    double elapsed_time_ms = chrono::duration<double, std::milli>(t_end - t_start).count();
    cout << "elapsed_time = " << elapsed_time_ms << endl;

    /*print(a);
    print(b);
    print(c);*/

    return 0;
}


