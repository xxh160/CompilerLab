lexer grammar CmmLexer;
import Common;

// unsigned int
INT:      DECIMAL | OCTAL | HEX;
// unsigned float
FLOAT:    BASE E EXPONENT | FDIGIT;
SEMI:     ';';
COMMA:    ',';
ASSIGNOP: '=';
RELOP:    '>' | '<' | '>=' | '<=' | '==' | '!=';
PLUS:     '+';
MINUS:    '-';
STAR:     '*';
DIV:      '/';
AND:      '&&';
OR:       '||';
DOT:      '.';
NOT:      '!';
TYPE:     'int' | 'float';
LP:       '(';
RP:       ')';
LB:       '[';
RB:       ']';
LC:       '{';
RC:       '}';
STRUCT:   'struct';
RETURN:   'return';
IF:       'if';
ELSE:     'else';
WHILE:    'while';
ID:       WORD+ ( WORD | DIGIT )*;

fragment E:          [eE];
fragment BASE:       DIGIT? DOT DIGIT | DIGIT+ DOT;
fragment EXPONENT:   (PLUS | MINUS)? DIGIT+;
fragment FDIGIT:     DECIMAL DOT DIGIT+;

fragment DECIMAL:    ZERO | POSDIGIT+ DIGIT*;
fragment OCTAL:      ZERO OCTALDIGIT*;
fragment HEX:        HEXPREFIX HEXDIGIT*;

fragment ZERO:       '0';
fragment POSDIGIT:   [1-9];
fragment DIGIT:      [0-9];
fragment OCTALDIGIT: [0-7];
fragment HEXDIGIT:   [0-9a-fA-F];
fragment HEXPREFIX:  ZERO [xX];
fragment WORD:       [a-zA-Z_];
