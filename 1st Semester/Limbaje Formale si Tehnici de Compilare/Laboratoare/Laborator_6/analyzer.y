%{
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <ctype.h>

extern int yylex();
extern int yyparse();
extern FILE* yyin;
extern int lineNumber;
void yyerror();    

FILE* outputFile;
char* filename;

#define MAX 1000
char declaratii[MAX][MAX], codSursa[MAX][MAX], imports[MAX][MAX];
int lenDecl = 0, lenCodSursa = 0, lenImports = 0;
char varCitite[MAX][MAX];
int n = 0, nr = 0;
char expresii[MAX][MAX];
int lenExpresii = 0;

bool found(char col[][MAX], int n, char* var);
void parseExpresie(char* element);
void printDeclaratii();
void printCod();
void printImports();

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
                        char tmp[100];
                        strcpy(tmp, " ");
                        strcat(tmp, $<value>1);
                        if (!found(declaratii, lenDecl, tmp)) {
                                strcpy(declaratii[lenDecl], strcat(tmp, " times 4 db 0"));
                                lenDecl++;
                        }
                }
                | ID COMMA declarare 
                {
                        char tmp[100];
                        strcpy(tmp, " ");
                        strcat(tmp, $<value>1);
                        if (!found(declaratii, lenDecl, tmp)) {
                                strcpy(declaratii[lenDecl], strcat(tmp, " times 4 db 0"));
                                lenDecl++;
                        }
                }
        ;

bloc: list_instr 
        ;

list_instr: instr 
        | instr list_instr
        ;

instr: instr_in | instr_out | atribuire

instr_in: CIN RS ID SEMICOLON {
        n = 0;
        strcpy(varCitite[n], "push dword ");
        strcat(varCitite[n], $<value>3);
        strcat(varCitite[n], "\n\t\tpush dword format");
        strcat(varCitite[n], "\n\t\tcall [scanf]");
        strcat(varCitite[n], "\n\t\tadd ESP, 4 * 2\n");
        n++;

        if (!found(imports, lenImports, "scanf")) {
                strcpy(imports[lenImports], "scanf");
                lenImports++;
        }
        if (!found(declaratii, lenDecl, "format")) {
                strcpy(declaratii[lenDecl], " format db \"%d\", 0");
        }

        for (int i = n - 1; i >= 0; i--) 
        {
                strcpy(codSursa[lenCodSursa++], varCitite[i]);
        }

        }
        ;

instr_out: COUT LS ID SEMICOLON {
        if (!found(imports, lenImports, "printf")) {
            strcpy(imports[lenImports], "printf");
            lenImports++;
        }
        if (!found(declaratii, lenDecl, "format")) {
            strcpy(declaratii[lenDecl], " format db \"%d\", 0");
            lenDecl++;
        }
        strcpy(codSursa[lenCodSursa], "push dword [");
        strcat(codSursa[lenCodSursa], $<value>3);
        strcat(codSursa[lenCodSursa++], "]");
        strcpy(codSursa[lenCodSursa++], "push dword format");
        strcpy(codSursa[lenCodSursa++], "call [printf]");
        strcpy(codSursa[lenCodSursa++], "add ESP, 4 * 2\n");
        }
        | COUT LS CONST SEMICOLON {
                if (!found(imports, lenImports, "printf")) {
                strcpy(imports[lenImports], "printf");
                lenImports++;
                }
                if (!found(declaratii, lenDecl, "format")) {
                strcpy(declaratii[lenDecl], " format db \"%d\", 0");
                lenDecl++;
                }
                strcpy(codSursa[lenCodSursa], "push dword ");
                strcat(codSursa[lenCodSursa++], $<value>3);
                strcpy(codSursa[lenCodSursa++], "push dword format");
                strcpy(codSursa[lenCodSursa++], "call [printf]");
                strcpy(codSursa[lenCodSursa++], "add ESP, 4 * 2\n");
        }
	;

atribuire: ID ASSIGN expr_aritmetica SEMICOLON {
        char tmp[MAX];
        strcpy(tmp, $<value>3);
        char* token = strtok(tmp, " ");
        while (token != NULL) {
            strcpy(expresii[lenExpresii], token);
            lenExpresii++;
            token = strtok(NULL, " ");
        }
        parseExpresie($<value>1);
        }
	;

expr_aritmetica: term 
	| term operator expr_aritmetica {
        char tmp[MAX];
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
        
        strcpy(imports[lenImports++], "exit"); 

        while (!feof(yyin)) {
                yyparse();
        }

        printf("Fisierul e corect sintactic!\n");
        
        outputFile = fopen("asmCode.asm", "w+");

        fprintf(outputFile, "bits 32\nglobal start\n\n");

        printImports();

        fprintf(outputFile, "segment data use32 class=data\n");
        printDeclaratii();

        fprintf(outputFile, "\nsegment code use32 class=code\n\tstart:\n");
        strcpy(codSursa[lenCodSursa++], "push dword 0");
        strcpy(codSursa[lenCodSursa++], "call [exit]");
        printCod();
	return 0;
}

void yyerror(char *s) {
        extern char* yytext;
        printf("Error for symbol %s on line: %d\n", yytext, lineNumber);
	exit(1);
}

void parseExpresie(char*  element) {
    if (isdigit(expresii[0][0])) {
        strcpy(codSursa[lenCodSursa], "mov AL, ");
        strcat(codSursa[lenCodSursa++], expresii[0]);
    } else {
        strcpy(codSursa[lenCodSursa], "mov AL, [");
        strcat(codSursa[lenCodSursa], expresii[0]);
        strcat(codSursa[lenCodSursa++], "]");
    }
	for (int i = 1; i < lenExpresii - 1; i+=2) {
		if (strcmp(expresii[i], "*") == 0) {
			if (isdigit(expresii[i + 1][0])) {
				strcpy(codSursa[lenCodSursa], "mov DL, ");
				strcat(codSursa[lenCodSursa++], expresii[i + 1]);
				strcpy(codSursa[lenCodSursa++], "mul DL");
			} else {
				strcpy(codSursa[lenCodSursa], "mul byte [");
				strcat(codSursa[lenCodSursa], expresii[i + 1]);
				strcat(codSursa[lenCodSursa++], "]");
			}
		}
        else if (strcmp(expresii[i], "/") == 0) {
			if (isdigit(expresii[i + 1][0])) {
                strcpy(codSursa[lenCodSursa++], "mov AH, 0");
				strcpy(codSursa[lenCodSursa], "mov DL, ");
				strcat(codSursa[lenCodSursa++], expresii[i + 1]);
				strcpy(codSursa[lenCodSursa++], "div DL");
			} else {
				strcpy(codSursa[lenCodSursa], "div byte [");
				strcat(codSursa[lenCodSursa], expresii[i + 1]);
				strcat(codSursa[lenCodSursa++], "]");
			}
		}
		else if (strcmp(expresii[i], "+") == 0) {
			if (isdigit(expresii[i + 1][0])) {
				strcpy(codSursa[lenCodSursa], "add AL, ");
				strcat(codSursa[lenCodSursa++], expresii[i + 1]);
			} else {
				strcpy(codSursa[lenCodSursa], "add AL, byte [");
				strcat(codSursa[lenCodSursa], expresii[i + 1]);
				strcat(codSursa[lenCodSursa++], "]");
			}
		}
		else if (strcmp(expresii[i], "-") == 0) {
			if (isdigit(expresii[i + 1][0])) {
				strcpy(codSursa[lenCodSursa], "sub AL, ");
				strcat(codSursa[lenCodSursa++], expresii[i + 1]);
			} else {
				strcpy(codSursa[lenCodSursa], "sub AL, byte [");
				strcat(codSursa[lenCodSursa], expresii[i + 1]);
				strcat(codSursa[lenCodSursa++], "]");
			}
		}
 	}

	strcpy(codSursa[lenCodSursa], "mov [");
	strcat(codSursa[lenCodSursa], element);
	strcat(codSursa[lenCodSursa++], "], AL\n");
	lenExpresii = 0;
}

bool found(char col[][MAX], int n, char* var) {
	char tmp[MAX];
	strcpy(tmp, var);
	strcat(tmp, " ");
	for (int i = 0; i < n; i++) {
		if (strstr(col[i], tmp) != NULL) {
			return true;
		}
	}
	return false;
}

void printImports() {
	for (int i = 0; i < lenImports; i++) {
		fprintf(outputFile, "extern %s\nimport %s msvcrt.dll\n\n", imports[i], imports[i]);
	}
}

void printDeclaratii() {
	for (int i = 0; i < lenDecl; i++) {
		fprintf(outputFile, "\t%s\n", declaratii[i]);
	}
}

void printCod() {
	for (int i = 0; i < lenCodSursa; i++) {
		fprintf(outputFile, "\t\t%s\n", codSursa[i]);
	}
}