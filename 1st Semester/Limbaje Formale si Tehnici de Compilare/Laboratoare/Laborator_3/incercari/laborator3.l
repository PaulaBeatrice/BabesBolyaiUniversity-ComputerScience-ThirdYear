/*** Definition Section ***/
%{
#include <string.h>

int errorFound = 0;
int lineNumber = 1;


typedef struct{
	char atom[100];
	int codAtom;
	int codTS;
}FIP;

typedef struct{
	char atom[100];
	int codAtomTS;
}TS;

FIP fip[300];
TS ts[300];
int codTS = 0, lenFIP = 0, lenTS = 0;

void addToFIP(char atom[], int codAtom, int codAtomTS) {
	lenFIP++;
	strcpy(fip[lenFIP - 1].atom, atom);
	fip[lenFIP - 1].codAtom = codAtom;
	fip[lenFIP - 1].codAtomTS = codAtomTS;
}

int addToTS(char atom[]) {
	int i, j;
	for (i = 0; i < lenTS; i++) {
		if (strcmp(ts[i].atom, atom) == 0) {
			return ts[i].codAtomTS;
		}
	}
	if ((lenTS == 0) || (strcmp(ts[lenTS - 1].atom, atom) < 0)) {
		strcpy(ts[lenTS].atom, atom);
		ts[lenTS].codAtomTS = codTS;
		codTS++; lenTS++;
	}
	else if (strcmp(ts[0].atom, atom) > 0) {
		lenTS++;
		for (i = lenTS; i > 0; i--)
			ts[i] = ts[i - 1];
		strcpy(ts[0].atom, atom);
		ts[0].codAtomTS = codTS;
		codTS++;
	}
	else {
		i = 0;
		while (strcmp(ts[i].atom, atom) < 0)
			i++;
		lenTS++;
		for (j = lenTS; j > i; j--)
			ts[j] = ts[j - 1];
		strcpy(ts[i].atom, atom);
		ts[i].codAtomTS = codTS;
		codTS++;
	}
	return codTS - 1;
}

void printTS() {
	printf("TABELA DE SIMBOLURI:\n");
	int i;
	for (i = 0; i < lenTS; i++)
		printf("%s  |  %d\n", ts[i].atom, ts[i].codAtomTS);
	printf("\n");
}

void printFIP() {
	printf("FORMA INTERNA A PROGRAMULUI:\n");
	int i;
	for (i = 0; i < lenFIP; i++)
		if (fip[i].codAtomTS == -1)
			printf("%s  |  %d  |  -\n", fip[i].atom, fip[i].codAtom);
		else
			printf("%s  |  %d  |  %d\n", fip[i].atom, fip[i].codAtom, fip[i].codAtomTS);
}
%}

%option noyywrap
REAL_NUMBER  [+-]?(0|[1-9][0-9]*)(\.[0-9]+)?
IDENTIFIER   [a-z][a-z0-9_]*
OPERATOR	"<"| ">"| "<="| ">="| "+"| "-"| "/"| "*"| "%"| ">>"| "<<"| "=="| "!="| "="
SEPARATOR	"."| ","|";"| "{"| "}"| "("|")"
KEYWORD "#include"| "<iostream>"| "<cmath>"| "<cstring>"| "using"| "namespace"| "std"| "int"| "main"| "double"| "struct"| "cin"| "cout"| "if"| "else"| "while"

/*** Rule Section ***/
%%
"#include"			                    { printf("%s - cod ATOM: %d \n", yytext, 2); }
"<iostream>"                           	{ printf("%s - cod ATOM: %d \n", yytext, 3); }
"<cmath>"                               { printf("%s - cod ATOM: %d \n", yytext, 4); }
"<cstring>"                             { printf("%s - cod ATOM: %d \n", yytext, 5); }
"<iostream>"                            { printf("%s - cod ATOM: %d \n", yytext, 6); }
"using"                                 { printf("%s - cod ATOM: %d \n", yytext, 7); }
"namespace"                             { printf("%s - cod ATOM: %d \n", yytext, 8); }
"std"                                   { printf("%s - cod ATOM: %d \n", yytext, 9); }
"main"                                  { printf("%s - cod ATOM: %d \n", yytext, 10); }
"cin"                                   { printf("%s - cod ATOM: %d \n", yytext, 11); }
"cout"                                  { printf("%s - cod ATOM: %d \n", yytext, 12); }
"int"                                   { printf("%s - cod ATOM: %d \n", yytext, 13); }
"double"                                { printf("%s - cod ATOM: %d \n", yytext, 14); }
"struct"                                { printf("%s - cod ATOM: %d \n", yytext, 15); }
"if"                                    { printf("%s - cod ATOM: %d \n", yytext, 16); }
"else"                                  { printf("%s - cod ATOM: %d \n", yytext, 17); }
"while"                                 { printf("%s - cod ATOM: %d \n", yytext, 18); }
\<                                    	{ printf("%s - cod ATOM: %d \n", yytext, 19); }
\<= 									{ printf("%s - cod ATOM: %d \n", yytext, 20); }
\;                                    	{ printf("%s - cod ATOM: %d \n", yytext, 21); }
\(                                    	{ printf("%s - cod ATOM: %d \n", yytext, 22); }
\)                                    	{ printf("%s - cod ATOM: %d \n", yytext, 23); }
\,                                    	{ printf("%s - cod ATOM: %d \n", yytext, 24); }
\+                                    	{ printf("%s - cod ATOM: %d \n", yytext, 25); }
\-                                    	{ printf("%s - cod ATOM: %d \n", yytext, 26); }
\*                                    	{ printf("%s - cod ATOM: %d \n", yytext, 27); }
\/                                    	{ printf("%s - cod ATOM: %d \n", yytext, 28); }
\%                                    	{ printf("%s - cod ATOM: %d \n", yytext, 29); }
\>                                    	{ printf("%s - cod ATOM: %d \n", yytext, 30); }
\>=                                    	{ printf("%s - cod ATOM: %d \n", yytext, 31); }
\=                                    	{ printf("%s - cod ATOM: %d \n", yytext, 32); }
\==                                    	{ printf("%s - cod ATOM: %d \n", yytext, 33); }
\.                                    	{ printf("%s - cod ATOM: %d \n", yytext, 34); }
\!=                                    	{ printf("%s - cod ATOM: %d \n", yytext, 35); }
\>>                                    	{ printf("%s - cod ATOM: %d \n", yytext, 36); }
\<<                                    	{ printf("%s - cod ATOM: %d \n", yytext, 37); }
\{                                   	{ printf("%s - cod ATOM: %d \n", yytext, 38); }
\}                                    	{ printf("%s - cod ATOM: %d \n", yytext, 39); }
{IDENTIFIER}							{ printf("%s - ID: %d \n", yytext, 0); }
{REAL_NUMBER}                           { printf("%s - CONST: %d \n", yytext, 1); addToTS(yytext);}


				

[\n] {
	currentLine++;
}

[ \t\n]+ ;

. {
	printf("Error on line %d. Unrecognized character: %s\n", currentLine, yytext);
}
%% 

/* yywrap() - wraps the above rule section */



int yywrap() {}

int main(int argc, char **argv) {
    ++argv, --argc;
    if (argc > 0)
        yyin = fopen(argv[0], "r");
    else
        yyin = stdin;
    yylex();
    printTS();
    printFIP();
}
