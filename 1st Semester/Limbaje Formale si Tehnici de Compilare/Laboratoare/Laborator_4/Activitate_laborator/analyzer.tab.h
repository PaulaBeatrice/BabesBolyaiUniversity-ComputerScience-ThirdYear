/* A Bison parser, made by GNU Bison 3.8.2.  */

/* Bison interface for Yacc-like parsers in C

   Copyright (C) 1984, 1989-1990, 2000-2015, 2018-2021 Free Software Foundation,
   Inc.

   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <https://www.gnu.org/licenses/>.  */

/* As a special exception, you may create a larger work that contains
   part or all of the Bison parser skeleton and distribute that work
   under terms of your choice, so long as that work isn't itself a
   parser generator using the skeleton or a modified version thereof
   as a parser skeleton.  Alternatively, if you modify or redistribute
   the parser skeleton itself, you may (at your option) remove this
   special exception, which will cause the skeleton and the resulting
   Bison output files to be licensed under the GNU General Public
   License without this special exception.

   This special exception was added by the Free Software Foundation in
   version 2.2 of Bison.  */

/* DO NOT RELY ON FEATURES THAT ARE NOT DOCUMENTED in the manual,
   especially those whose name start with YY_ or yy_.  They are
   private implementation details that can be changed or removed.  */

#ifndef YY_YY_ANALYZER_TAB_H_INCLUDED
# define YY_YY_ANALYZER_TAB_H_INCLUDED
/* Debug traces.  */
#ifndef YYDEBUG
# define YYDEBUG 0
#endif
#if YYDEBUG
extern int yydebug;
#endif

/* Token kinds.  */
#ifndef YYTOKENTYPE
# define YYTOKENTYPE
  enum yytokentype
  {
    YYEMPTY = -2,
    YYEOF = 0,                     /* "end of file"  */
    YYerror = 256,                 /* error  */
    YYUNDEF = 257,                 /* "invalid token"  */
    IOSTREAM = 258,                /* IOSTREAM  */
    CSTRING = 259,                 /* CSTRING  */
    CMATH = 260,                   /* CMATH  */
    USING = 261,                   /* USING  */
    NAMESPACE = 262,               /* NAMESPACE  */
    STD = 263,                     /* STD  */
    MAIN = 264,                    /* MAIN  */
    INT = 265,                     /* INT  */
    DOUBLE = 266,                  /* DOUBLE  */
    STRUCT = 267,                  /* STRUCT  */
    IF = 268,                      /* IF  */
    ELSE = 269,                    /* ELSE  */
    WHILE = 270,                   /* WHILE  */
    CIN = 271,                     /* CIN  */
    COUT = 272,                    /* COUT  */
    PLUS = 273,                    /* PLUS  */
    MINUS = 274,                   /* MINUS  */
    MUL = 275,                     /* MUL  */
    MOD = 276,                     /* MOD  */
    DIV = 277,                     /* DIV  */
    PERIOD = 278,                  /* PERIOD  */
    LT = 279,                      /* LT  */
    LET = 280,                     /* LET  */
    GT = 281,                      /* GT  */
    GET = 282,                     /* GET  */
    EQ = 283,                      /* EQ  */
    NE = 284,                      /* NE  */
    RS = 285,                      /* RS  */
    LS = 286,                      /* LS  */
    ASSIGN = 287,                  /* ASSIGN  */
    COMMA = 288,                   /* COMMA  */
    SEMICOLON = 289,               /* SEMICOLON  */
    LPARAN = 290,                  /* LPARAN  */
    RPARAN = 291,                  /* RPARAN  */
    LBRACE = 292,                  /* LBRACE  */
    RBRACE = 293,                  /* RBRACE  */
    ID = 294,                      /* ID  */
    CONST = 295,                   /* CONST  */
    CORECT = 296,                  /* CORECT  */
    GRESIT = 297                   /* GRESIT  */
  };
  typedef enum yytokentype yytoken_kind_t;
#endif

/* Value type.  */
#if ! defined YYSTYPE && ! defined YYSTYPE_IS_DECLARED
typedef int YYSTYPE;
# define YYSTYPE_IS_TRIVIAL 1
# define YYSTYPE_IS_DECLARED 1
#endif


extern YYSTYPE yylval;


int yyparse (void);


#endif /* !YY_YY_ANALYZER_TAB_H_INCLUDED  */
