lexer grammar CmmLexer;

// unsigned float
FLOAT:    (BASE E EXPONENT) | FDIGIT;
// unsigned int
INT:      DECIMAL | OCTAL | HEX;
SEMI:     ';';
COMMA:    ',';
ASSIGNOP: '=';
RELOP:    '>=' | '<=' | '==' | '!=' | '>' | '<';
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
LCOM:     '//' ~[\r\n]* -> skip;
BCOM:     '/*' .*? '*/' -> skip;
WS:       '\t'+ -> skip ;
NEWLINE:  ( '\r' '\n'? | '\n' ) -> skip;
ID:       WORD+ ( WORD | DIGIT )*;

fragment E:          [eE];
fragment BASE:       DIGIT* DOT DIGIT+ | DIGIT+ DOT;
fragment EXPONENT:   [+-]? DIGIT+;
fragment FDIGIT:     DIGIT+ DOT DIGIT+;

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
