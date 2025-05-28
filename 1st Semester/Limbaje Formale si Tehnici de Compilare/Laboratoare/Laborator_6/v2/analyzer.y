%{
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <ctype.h>

extern int yylex();
extern int yyparse();
extern FILE* yyin;
extern int currentLine;
void yyerror(char *s);
void yyerror();    

FILE* outputFile;
char* filename;

#define MAX 1000
char declaratii[MAX][MAX], codSursa[MAX][MAX], expresii[MAX][MAX];
int lenDecl = 0, lenCodSursa = 0, lenExpresii = 0;
char varCitite[MAX][MAX];
int n = 0, nr = 0;

void computeExpr(char* element);
void printDeclaratii();
void printCod();

 %}

%union {
	char * value;
}

%token INCLUDE
%token NAMESPACE
%token INT
%token MAIN
%token CIN
%token COUT
%token PLUS
%token MINUS
%token MUL
%token DIV
%token ASSIGN
%token COMMA
%token SEMICOLON
%token LBRACE
%token RBRACE
%token RS
%token LS
%token LPARAN
%token RPARAN
%token ID
%token CONST

%%


program: INCLUDE NAMESPACE INT MAIN LBRACE instr_declaratii bloc RBRACE
       ;

instr_declaratii: INT declarare
        ;

declarare: ID SEMICOLON 
                {
                        char tmp[100]; // creez un buffer temporar
                        strcpy(tmp, " ");
                        strcat(tmp, $<value>1); // concatenez id-ul 
                        strcpy(declaratii[lenDecl], strcat(tmp, " times 4 db 0")); // adauga declatia de tip "ID times 4 db 0"
                        lenDecl++;
                }
                | ID COMMA declarare 
                {
                        char tmp[100];
                        strcpy(tmp, " ");
                        strcat(tmp, $<value>1);
                        strcpy(declaratii[lenDecl], strcat(tmp, " times 4 db 0"));
                        lenDecl++;
                }
        ;

bloc: instr_in atribuire instr_out
        ;

instr_in: CIN RS ID SEMICOLON {
        n = 0;

        // construieste instructiunile pentru citire
        strcpy(varCitite[n], "mov eax, ");
        strcat(varCitite[n], $<value>3); // plaseaza in eax adresa Id-ului
        strcat(varCitite[n], "\n\t\tcall io_readint\n"); // apeleaza functia de citire
        strcat(varCitite[n], "\t\tmov ["); // transfera valoarea citita in ID
        strcat(varCitite[n], $<value>3);
        strcat(varCitite[n], "]");
        strcat(varCitite[n], " , eax");

        n++;

        for (int i = n - 1; i >= 0; i--) 
        { // adauga instructiunea in codul sursa 
                strcpy(codSursa[lenCodSursa++], varCitite[i]);
        }

        }
        ;

instr_out: COUT LS ID SEMICOLON {
        // construieste instructiunile pentru afisare
        strcpy(codSursa[lenCodSursa], "mov eax, ["); // pune in eax adresa variabilei ce urmeaza sa fie afisata 
        strcat(codSursa[lenCodSursa], $<value>3);
        strcat(codSursa[lenCodSursa], "]");
        lenCodSursa++;
        strcpy(codSursa[lenCodSursa], "call io_writeint\n"); // apel la functia de afisare
        lenCodSursa++;
        }


atribuire: ID ASSIGN expr_aritmetica SEMICOLON {
        char tmp[MAX]; // se declara un buffer temporar in care se va pune expresia aritmetica
        strcpy(tmp, $<value>3);
        char* token = strtok(tmp, " ");
        while (token != NULL) { // separam expresia dupa caracterul " "
            strcpy(expresii[lenExpresii], token); // copiem fiecare expresie
            lenExpresii++;
            token = strtok(NULL, " ");
        }
        computeExpr($<value>1); // evaluam expresia
        }
	;

expr_aritmetica: term 
	| term operator expr_aritmetica {
        char tmp[MAX]; // se declara un cuffer temporar unde se vor pune termenii expresiei
        strcpy(tmp, $<value>1);
        strcat(tmp, " ");
        strcat(tmp, $<value>2);
        strcat(tmp, " ");
        strcat(tmp, $<value>3);
        $<value>$ = strdup(tmp);
        }
	;

operator: PLUS
	| MINUS
	| MUL
	| DIV
	;

term: ID
	| CONST
	;

%%

int main(int argc, char* argv[]) {
        FILE* f = NULL;
        if (argc > 1) { 
                f = fopen(argv[1], "r");
        }

        if (!f) {
                perror("Could not open file!\n");
                yyin = stdin;
        } else {
                yyin = f;
        }
        
        while (!feof(yyin)) {
                yyparse();
        }

        printf("Fisierul e corect sintactic!\n");
        
        outputFile = fopen("rez.asm", "w+");

        fprintf(outputFile, "%%include 'io.inc'\n");
        //fprintf(outputFile, "%include 'io.inc'\n global main \n section .text \n main:\n");

        fprintf(outputFile, "section .data \n");
        for (int i = 0; i < lenDecl; i++) {
		fprintf(outputFile, "\t%s\n", declaratii[i]);
	}

        fprintf(outputFile, "global main \n section .text \n main:\n");


        printCod();

        
        return 0;
}

void yyerror(char *s) {
        extern char* yytext;
        printf("Error for symbol %s on line: %d\n", yytext, currentLine);
	exit(1);
}

void printCod() {
        // scriem in fisierul asm liniile de cod 
	for (int i = 0; i < lenCodSursa; i++) {
		fprintf(outputFile, "\t\t%s\n", codSursa[i]);
                //printf("%s\n", codSursa[i]);
	}
}

void computeExpr(char*  element) {
    if (isdigit(expresii[0][0])) { // daca primul termen al expresiei este nr il incarca in AL
        strcpy(codSursa[lenCodSursa], "mov AL, ");
        strcat(codSursa[lenCodSursa++], expresii[0]);
    } else {  // daca nu e nr, incarca in AL, valoarea de la adresa indicata de primul termen
        strcpy(codSursa[lenCodSursa], "mov AL, [");
        strcat(codSursa[lenCodSursa], expresii[0]);
        strcat(codSursa[lenCodSursa++], "]");
    }
	for (int i = 1; i < lenExpresii - 1; i+=2) { 
		if (strcmp(expresii[i], "*") == 0) { // inmultire
			if (isdigit(expresii[i + 1][0])) { // daca primul termen e nr in punem in DL
				strcpy(codSursa[lenCodSursa], "mov DL, ");
				strcat(codSursa[lenCodSursa++], expresii[i + 1]);
				strcpy(codSursa[lenCodSursa++], "mul DL"); // inmultim AL cu DL
			} else { // daca nu e nr, inmultim cu valoarea de la adresa indicata 
				strcpy(codSursa[lenCodSursa], "mul byte [");
				strcat(codSursa[lenCodSursa], expresii[i + 1]);
				strcat(codSursa[lenCodSursa++], "]");
			}
		}
                else if (strcmp(expresii[i], "/") == 0) { // impartire
			if (isdigit(expresii[i + 1][0])) { // daca primul termen e nr, setam AH la 0 si punem continul in DL
                                strcpy(codSursa[lenCodSursa++], "mov AH, 0");
				strcpy(codSursa[lenCodSursa], "mov DL, ");
				strcat(codSursa[lenCodSursa++], expresii[i + 1]);
				strcpy(codSursa[lenCodSursa++], "div DL"); // imparte DX:AX cu DL
			} else {  
				strcpy(codSursa[lenCodSursa], "div byte ["); // dc nu e nr, incarcam valoarea de la adresa data
				strcat(codSursa[lenCodSursa], expresii[i + 1]);
				strcat(codSursa[lenCodSursa++], "]");
			}
		}
		else if (strcmp(expresii[i], "+") == 0) { // adunare
			if (isdigit(expresii[i + 1][0])) {
				strcpy(codSursa[lenCodSursa], "add AL, ");
				strcat(codSursa[lenCodSursa++], expresii[i + 1]);
			} else {
				strcpy(codSursa[lenCodSursa], "add AL, [");
				strcat(codSursa[lenCodSursa], expresii[i + 1]);
				strcat(codSursa[lenCodSursa++], "]");
			}
		}
		else if (strcmp(expresii[i], "-") == 0) { // scadere
			if (isdigit(expresii[i + 1][0])) {
				strcpy(codSursa[lenCodSursa], "sub AL, ");
				strcat(codSursa[lenCodSursa++], expresii[i + 1]);
			} else {
				strcpy(codSursa[lenCodSursa], "sub AL, [");
				strcat(codSursa[lenCodSursa], expresii[i + 1]);
				strcat(codSursa[lenCodSursa++], "]");
			}
		}
 	}

	strcpy(codSursa[lenCodSursa], "mov ["); // plasam rezultatul expresiei in variabila data
	strcat(codSursa[lenCodSursa], element);
	strcat(codSursa[lenCodSursa++], "], AL\n");
	lenExpresii = 0;
}