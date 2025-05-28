%{
#include <string.h>
#include <stdio.h>
#include <stdlib.h>

extern void printTS();
extern void printFIP();
extern int yylex();
extern int yyparse();
extern FILE* yyin;
extern int lineNumber;
void yyerror(char *s);
 %}

%token IOSTREAM
%token CSTRING
%token CMATH
%token USING
%token NAMESPACE
%token STD
%token MAIN
%token INT
%token DOUBLE
%token STRUCT
%token IF
%token ELSE
%token WHILE
%token CIN
%token COUT
%token PLUS
%token MINUS
%token MUL
%token MOD
%token DIV
%token PERIOD
%token LT
%token LET
%token GT
%token GET
%token EQ
%token NE
%token RS
%token LS
%token ASSIGN
%token COMMA
%token SEMICOLON
%token LPARAN
%token RPARAN
%token LBRACE
%token RBRACE
%token ID
%token CONST
%token CORECT
%token GRESIT


%%
program: CORECT;


%%

int main(int argc, char* argv[]) {
    ++argv, --argc;

    // sets the input for flex file
    if (argc > 0)
        yyin = fopen(argv[0], "r");
    else
        yyin = stdin;

    //read each line from the input file and process it
    while (!feof(yyin)) {
        yyparse();
    }
    //printTS();
    //printFIP();
    //printf("The file is sintactically correct!\n");

    return 0;
}

void yyerror(char *s) {
    // printTS();
    //printFIP();
    extern char* yytext;
    //printf("Error for symbol %s at line: %d ! \n",yytext, lineNumber);
    exit(1);
}