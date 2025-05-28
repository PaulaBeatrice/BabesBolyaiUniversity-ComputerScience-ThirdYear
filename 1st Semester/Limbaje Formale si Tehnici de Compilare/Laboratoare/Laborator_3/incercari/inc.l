%{
#include <stdio.h>
%}

%option noyywrap

%%

"int"       { printf("Keyword: int\n"); }
"double"    { printf("Keyword: double\n"); }
[a-zA-Z][a-zA-Z0-9_]*   { printf("Identifier: %s\n", yytext); }
[0-9]+      { printf("Number: %s\n", yytext); }
[ \t\n]     ; // Ignore whitespace
.           { printf("Unrecognized character: %s\n", yytext); }

%%

int main() {
    yylex();
    return 0;
}
