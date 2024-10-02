%{

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

struct FIP { // forma interna a programului
    int codAtom;
    int codTS;
};

struct TS { // tabela de simboluri
    char atom[100];
    int codAtomTS;
};

struct FIP fip[300]; // forma interna a programului
struct TS TS_ID[300]; // tabela de simboluri de identificatori
struct TS TS_CONST[300]; // tabela de simboluri de constante
int fipIndex = 0, ts_id = 0, ts_const = 0, lenTS_ID = 0, lenTS_CONST = 0;
int lineNumber = 1;
int errorFound = 0;

void addToFIP(int codAtom, int codTS) {
    fip[fipIndex].codAtom = codAtom;
    fip[fipIndex].codTS = codTS;
    fipIndex++;
    // printf("Added to FIP: codAtom=%d, codTS=%d\n", codAtom, codTS);
}

// Functia addToTS adauga un simbol nou in tabela de simboluri (ts) sau returneaza codul simbolului existent.
// simbol: sir de caractere reprezentand simbolul de adaugat in tabela
// ts: tabela de simboluri in care se adauga simbolul
// lenTS: pointer catre variabila care retine lungimea curenta a tabelei de simboluri
// ts_counter: pointer catre variabila care retine contorul pentru codurile atomilor
int addToTS(char simbol[], struct TS ts[], int *lenTS, int *ts_counter) {
    // Daca tabela e goala sau atomul trebuie pus pe prima pozitie
    if (*lenTS == 0 || strcmp(ts[0].atom, simbol) > 0) {
        // Adaugam la inceputul tabelei
        for (int j = *lenTS; j > 0; j--) {
            strcpy(ts[j].atom, ts[j - 1].atom);  // Rearanjam atomii existenti pentru a face loc noului simbol
            ts[j].codAtomTS = ts[j - 1].codAtomTS;
        }

        strcpy(ts[0].atom, simbol);          // Copiem simbolul in tabela de simboluri
        ts[0].codAtomTS = (*ts_counter)++;   // Atribuim un cod unic simbolului
        (*lenTS)++;                          // Incrementam lungimea tabelei de simboluri
        return ts[0].codAtomTS;              // Returnam codul simbolului adaugat
    }

    // Verificam daca atomul exista deja in tabel
    for (int i = 0; i < *lenTS; i++) {
        if (strcmp(ts[i].atom, simbol) == 0) {
            return ts[i].codAtomTS;          // Returnam codul simbolului existent
        }

        // Gasim locul potrivit pentru noul atom
        if (strcmp(ts[i].atom, simbol) > 0) {
            // Facem loc pentru noul atom
            for (int j = *lenTS; j > i; j--) {
                strcpy(ts[j].atom, ts[j - 1].atom);  // Rearanjam atomii existenti pentru a face loc noului simbol
                ts[j].codAtomTS = ts[j - 1].codAtomTS;
            }

            // Adaugam noul atom
            strcpy(ts[i].atom, simbol);          // Copiem simbolul in tabela de simboluri
            ts[i].codAtomTS = (*ts_counter)++;   // Atribuim un cod unic simbolului
            (*lenTS)++;                          // Incrementam lungimea tabelei de simboluri
            return ts[i].codAtomTS;              // Returnam codul simbolului adaugat
        }
    }

    // Daca ajungem aici, inseamna ca noul atom trebuie adaugat la sfarsitul tabelei
    strcpy(ts[*lenTS].atom, simbol);          // Copiem simbolul in tabela de simboluri
    ts[*lenTS].codAtomTS = (*ts_counter)++;   // Atribuim un cod unic simbolului
    (*lenTS)++;                              // Incrementam lungimea tabelei de simboluri
    return ts[*lenTS - 1].codAtomTS;          // Returnam codul simbolului adaugat
}


void printFIP() {
    printf("--------------TABELA FIP---------------\n");
    printf("| Index | CodAtom | CodTS  |\n");
    printf("---------------------------------------\n");
    for (int i = 0; i < fipIndex; i++) {
        printf("| %-6d| %-8d| %-7d|\n", i, fip[i].codAtom, fip[i].codTS);
    }
    printf("---------------------------------------\n");
}

void printTS(struct TS ts[], int lenTS) {
    printf("TABELA DE SIMBOLURI:\n");
    for (int i = 0; i < lenTS; i++) {
        printf("%-6s | %-7d\n", ts[i].atom, ts[i].codAtomTS);
    }
    printf("\n");
}


%}

ID [a-z]{1,8}
CONST [+-]?(0|[1-9][0-9]*)(\.[0-9]+)?(e[+-]?[0-9]+)?|0x[0-9A-F]+|0b[01]+|0[0-7]+

%%
"#include <iostream>"   { /* printf("%s - cod ATOM: %d \n", yytext, 2); */ addToFIP(2, -1); }
"#include <cstring>"    { /* printf("%s - cod ATOM: %d \n", yytext, 3); */ addToFIP(3, -1); }
"#include <cmath>"      { /* printf("%s - cod ATOM: %d \n", yytext, 4); */ addToFIP(4, -1); }
using                   { /* printf("%s - cod ATOM: %d \n", yytext, 5); */ addToFIP(5, -1); }
namespace               { /* printf("%s - cod ATOM: %d \n", yytext, 6); */ addToFIP(6, -1); }
std                     { /* printf("%s - cod ATOM: %d \n", yytext, 7); */ addToFIP(7, -1); }
main                    { /* printf("%s - cod ATOM: %d \n", yytext, 8); */ addToFIP(8, -1); }
int                     { /* printf("%s - cod ATOM: %d \n", yytext, 9); */ addToFIP(9, -1); }
double                  { /* printf("%s - cod ATOM: %d \n", yytext, 10); */ addToFIP(10, -1); }
struct                  { /* printf("%s - cod ATOM: %d \n", yytext, 11); */ addToFIP(11, -1); }
if                      { /* printf("%s - cod ATOM: %d \n", yytext, 12); */ addToFIP(12, -1); }
else                    { /* printf("%s - cod ATOM: %d \n", yytext, 13); */ addToFIP(13, -1); }
while                   { /* printf("%s - cod ATOM: %d \n", yytext, 14); */ addToFIP(14, -1); }
cin                     { /* printf("%s - cod ATOM: %d \n", yytext, 15); */ addToFIP(15, -1); }
cout                    { /* printf("%s - cod ATOM: %d \n", yytext, 16); */ addToFIP(16, -1); }
\+                      { /* printf("%s - cod ATOM: %d \n", yytext, 17); */ addToFIP(17, -1); }
\-                      { /* printf("%s - cod ATOM: %d \n", yytext, 18); */ addToFIP(18, -1); }
\*                      { /* printf("%s - cod ATOM: %d \n", yytext, 19); */ addToFIP(19, -1); }
\%                      { /* printf("%s - cod ATOM: %d \n", yytext, 20); */ addToFIP(20, -1); }
\/                      { /* printf("%s - cod ATOM: %d \n", yytext, 21); */ addToFIP(21, -1); }
\.                      { /* printf("%s - cod ATOM: %d \n", yytext, 22); */ addToFIP(22, -1); }
\<                      { /* printf("%s - cod ATOM: %d \n", yytext, 23); */ addToFIP(23, -1); }
\<=                     { /* printf("%s - cod ATOM: %d \n", yytext, 24); */ addToFIP(24, -1); }
\>                      { /* printf("%s - cod ATOM: %d \n", yytext, 25); */ addToFIP(25, -1); }
\>=                     { /* printf("%s - cod ATOM: %d \n", yytext, 26); */ addToFIP(26, -1); }
\==                     { /* printf("%s - cod ATOM: %d \n", yytext, 27); */ addToFIP(27, -1); }
\!=                     { /* printf("%s - cod ATOM: %d \n", yytext, 28); */ addToFIP(28, -1); }
\<<                     { /* printf("%s - cod ATOM: %d \n", yytext, 29); */ addToFIP(29, -1); }
\>>                     { /* printf("%s - cod ATOM: %d \n", yytext, 30); */ addToFIP(30, -1); }
\=                      { /* printf("%s - cod ATOM: %d \n", yytext, 31); */ addToFIP(31, -1); }
\,                      { /* printf("%s - cod ATOM: %d \n", yytext, 32); */ addToFIP(32, -1); }
\;                      { /* printf("%s - cod ATOM: %d \n", yytext, 33); */ addToFIP(33, -1); }
\(                      { /* printf("%s - cod ATOM: %d \n", yytext, 34); */ addToFIP(34, -1); }
\)                      { /* printf("%s - cod ATOM: %d \n", yytext, 35); */ addToFIP(35, -1); }
\{                      { /* printf("%s - cod ATOM: %d \n", yytext, 36); */ addToFIP(36, -1); }
\}                      { /* printf("%s - cod ATOM: %d \n", yytext, 37); */ addToFIP(37, -1); }
"->"                    { /* printf("%s - cod ATOM: %d \n", yytext, 37); */ addToFIP(38, -1); }
"daca"                  { /* printf("%s - cod ATOM: %d \n", yytext, 37); */ addToFIP(39, -1); }
"atunci"                { /* printf("%s - cod ATOM: %d \n", yytext, 37); */ addToFIP(40, -1); }
"sfdaca"                { /* printf("%s - cod ATOM: %d \n", yytext, 37); */ addToFIP(41, -1); }
{ID}                    { /* printf("%s - ID: %d \n", yytext, 1); */ int codTS = addToTS(yytext, TS_ID, &lenTS_ID, &ts_id); addToFIP(1, codTS); /* 1 pt constante */ }
{CONST}                 { /* printf("%s - CONST: %d \n", yytext, 0); */ int codTS = addToTS(yytext, TS_CONST, &lenTS_CONST, &ts_const); addToFIP(0, codTS); /* 0 pt identificatori */ }
\n                      {/*lineNumber++;*/}
\r                      {lineNumber++;}
[ \t\n]+                { ; /* eat up whitespace */ }
.                       {   
                            printf("Illegal token  %s on line %d \n",  yytext, lineNumber); 
                            printf("--------------TABELA CONST--------------\n");
                            printTS(TS_CONST, lenTS_CONST);
                            printf("----------TABELA IDENTIFICATORI---------\n");
                            printTS(TS_ID, lenTS_ID);
                            printFIP();
                            exit(EXIT_FAILURE);
                        }

%%

int yywrap() {
    return 1;
}

int main(int argc, char **argv) {
    FILE *fp;
    fp = fopen(argv[1], "r");

    if (!fp) {
        printf("Error opening file.\n");
        return 1;
    }

    yyin = fp;
    yylex();

    fclose(fp);
    printf("--------------TABELA CONST--------------\n");
    printTS(TS_CONST, lenTS_CONST);
    printf("----------TABELA IDENTIFICATORI---------\n");
    printTS(TS_ID, lenTS_ID);
    printFIP();
    return 0;
}
