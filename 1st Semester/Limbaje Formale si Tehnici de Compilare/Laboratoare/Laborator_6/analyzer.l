%{
#include "analyzer.tab.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#define YY_DECL int yylex()

int lineNumber = 1;

%}
%option noyywrap

ID [a-z]{1,8}
CONST [+-]?(0|[1-9][0-9]*)(\.[0-9]+)?(e[+-]?[0-9]+)?|0x[0-9A-F]+|0b[01]+|0[0-7]+([ulUL]+)?

%%
"#include<iostream>"   { return INCLUDE;}
"using namespace std;"  { return NAMESPACE;}
int                     { return INT; }
"main()"                { return MAIN; }
cin                     { return CIN; }
cout                    { return COUT; }
"+"                     { yylval.value = strdup(yytext); return PLUS; }
"-"                     { yylval.value = strdup(yytext); return MINUS; }
"*"                     { yylval.value = strdup(yytext); return MUL; }
"/"                     { yylval.value = strdup(yytext); return DIV; }
"="                     { return ASSIGN; }
","                     { return COMMA; }
";"                     { return SEMICOLON; }
"{"                     { return LBRACE; }
"}"                     { return RBRACE; }
">>"                    { return RS; }
"<<"                    { return LS; }
"("                     { return LPARAN; }
")"                     { return RPARAN; }

{ID}                    { yylval.value = strdup(yytext); return ID; }
{CONST}                 { yylval.value = strdup(yytext); return CONST; }

[\n] { lineNumber++; }

[ \t]+ ;

%%