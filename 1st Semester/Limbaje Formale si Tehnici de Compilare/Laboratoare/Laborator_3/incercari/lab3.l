/*** Definition Section ***/
%{
#include <stdio.h>
#include <string.h>

int errorFound = 0;
int lineNumber = 1;
FILE *yyin;


typedef struct
{
    char atom[100];
    int codAtom;
    int codAtomTS;  // Corrected member name from codTS to codAtomTS
} FIP;

typedef struct
{
    char atom[100];
    int codAtomTS;
} TS;

FIP fip[300];
TS ts[300];
int codTS = 0, lenFIP = 0, lenTS = 0;

void addToFIP(char atom[], int codAtom, int codAtomTS)
{
    lenFIP++;
    strcpy(fip[lenFIP - 1].atom, atom);
    fip[lenFIP - 1].codAtom = codAtom;
    fip[lenFIP - 1].codAtomTS = codAtomTS;  // Corrected assignment to codAtomTS
}
int addToTS(char atom[])
{
    int i, j;

    // Dacă tabela de simboluri este goală sau noul atom este primul
    if (lenTS == 0 || strcmp(ts[0].atom, atom) > 0)
    {
        strcpy(ts[0].atom, atom);
        ts[0].codAtomTS = codTS;
        codTS++;
        lenTS++;
        return codTS - 1;
    }

    // Verificare pentru atomul existent
    for (i = 0; i < lenTS; i++)
    {
        if (strcmp(ts[i].atom, atom) == 0)
        {
            return ts[i].codAtomTS;
        }
    }

    // Găsește locul potrivit pentru noul atom
    i = 0;
    while (i < lenTS && strcmp(ts[i].atom, atom) < 0)
    {
        i++;
    }

    // Face loc pentru noul atom
    for (j = lenTS; j > i; j--)
    {
        strcpy(ts[j].atom, ts[j - 1].atom);
        ts[j].codAtomTS = ts[j - 1].codAtomTS;
    }

    // Adaugă noul atom
    strcpy(ts[i].atom, atom);
    ts[i].codAtomTS = codTS;
    codTS++;
    lenTS++;

    return codTS - 1;
}
void printTS()
{
    printf("TABELA DE SIMBOLURI:\n");
    int i;
    for (i = 0; i < lenTS; i++)
        printf("%s  |  %d\n", ts[i].atom, ts[i].codAtomTS);
    printf("\n");
}

void printFIP()
{
    printf("FORMA INTERNA A PROGRAMULUI:\n");
    int i;
    for (i = 0; i < lenFIP; i++)
        if (fip[i].codAtomTS == -1)
            printf("%s  |  %d  |  -\n", fip[i].atom, fip[i].codAtom);
        else
            printf("%s  |  %d  |  %d\n", fip[i].atom, fip[i].codAtom, fip[i].codAtomTS);
}
%}


REAL_NUMBER [+-]?(0|[1-9][0-9]*)(\.[0-9]+)?
IDENTIFIER [a-z][a-z0-9_]*
OPERATOR "<"|">"|"<="|">="|"+"|"-"|"/"|"*"|"%"|">>"|"<<"|"=="|"!="|"="
SEPARATOR "."|","|";"|"{"|"}"|"("|")"
KEYWORD "#include"|"<iostream>"|"<cmath>"|"<cstring>"|"using"|"namespace"|"std"|"int"|"main"|"double"|"struct"|"cin"|"cout"|"if"|"else"|"while"

/*** Rule Section ***/
%%

"#include" { addToFIP(yytext, 2, -1); }
"<iostream>" { addToFIP(yytext, 3, -1); }
"<cmath>" { addToFIP(yytext, 4, -1); }
"<cstring>" { addToFIP(yytext, 5, -1); }
"using" { addToFIP(yytext, 6, -1); }
"namespace" { addToFIP(yytext, 7, -1); }
"std" { addToFIP(yytext, 8, -1); }
"main" { addToFIP(yytext, 9, -1); }
"cin" { addToFIP(yytext, 10, -1); }
"cout" { addToFIP(yytext, 11, -1); }
"int" { addToFIP(yytext, 12, -1); }
"double" { addToFIP(yytext, 13, -1); }
"struct" { addToFIP(yytext, 14, -1); }
"if" { addToFIP(yytext, 15, -1); }
"else" { addToFIP(yytext, 16, -1); }
"while" { addToFIP(yytext, 17, -1); }

{IDENTIFIER}{1,8} {
    int codTS = addToTS(yytext);
    addToFIP(yytext, 0, codTS);
}

{REAL_NUMBER}+ {
    int codTS = addToTS(yytext);
    addToFIP(yytext, 1, codTS);
}

"<" { addToFIP(yytext, 18, -1); }
">" { addToFIP(yytext, 19, -1); }
"<=" { addToFIP(yytext, 20, -1); }
">=" { addToFIP(yytext, 21, -1); }
"+" { addToFIP(yytext, 22, -1); }
"-" { addToFIP(yytext, 23, -1); }
"/" { addToFIP(yytext, 24, -1); }
"*" { addToFIP(yytext, 25, -1); }
"%" { addToFIP(yytext, 26, -1); }
">>" { addToFIP(yytext, 27, -1); }
"<<" { addToFIP(yytext, 28, -1); }
"==" { addToFIP(yytext, 29, -1); }
"!=" { addToFIP(yytext, 30, -1); }
"=" { addToFIP(yytext, 31, -1); }

"." { addToFIP(yytext, 32, -1); }
"," { addToFIP(yytext, 33, -1); }
";" { addToFIP(yytext, 34, -1); }
"{" { addToFIP(yytext, 35, -1); }
"}" { addToFIP(yytext, 36, -1); }
"(" { addToFIP(yytext, 37, -1); }
")" { addToFIP(yytext, 38, -1); }

[\n]									{ ++lineNumber; }
[ \t\n]+             					{ ; /* eat up whitespace */ } 
.                						{ errorFound = 1; printf("Illegal token %s at line %d !", yytext, lineNumber); printf("\n"); }

<<EOF>> {
    if (!errorFound) {
        printTS();
        printFIP();
    }
    return 0;
}


%%
int yywrap() {}

int main(int argc, char** argv) {
    if (argc != 2) {
        printf("Usage: %s <input_file>\n", argv[0]);
        return 1;
    }

    FILE *fp;
    fp = fopen(argv[1], "r");

    /* yyin - takes the file pointer which contains the input*/
    yyin = fp;

    /* yylex() - this is the main flex function which runs the Rule Section*/
    yylex();

    printTS();
    printFIP();

    fclose(fp);

    return 0;
}

