parser grammar CmmParser;

options {
    tokenVocab=CmmLexer;
}

// high level definitions
program: extDef* EOF;

extDef: specifier extDecList? SEMI
      | specifier funDec compSt;

extDecList: varDec (COMMA varDec)*;

// specifilers
specifier: TYPE
         | structSpecifier;

structSpecifier: STRUCT optTag LC defList RC
               | STRUCT tag;

optTag: ID?;

tag: ID;

// declarators
varDec: ID (LB INT RB)*
      | ID (LB INT RB)* (LB (FLOAT|ID) RB) (LB (INT|FLOAT|ID) RB)* { notifyErrorListeners ("array size must be an integer constant"); }
      ;

funDec: ID LP varList? RP;

varList: paramDec (COMMA paramDec)*;

paramDec: specifier varDec;

// statements
compSt: LC defList stmtList RC;

stmtList: stmt*;

stmt: compSt
    | RETURN? exp SEMI
    | IF LP exp RP stmt (ELSE stmt)?
    | WHILE LP exp RP stmt;

// local definitions
defList: def*;

def: specifier decList SEMI;

decList: dec (COMMA dec)*;

dec: varDec (ASSIGNOP exp)*;

// expressions and args
exp: exp DOT ID
   | exp LB exp RB
   | <assoc=right> MINUS exp
   | <assoc=right> NOT exp
   | exp STAR     exp
   | exp DIV      exp
   | exp PLUS     exp
   | exp MINUS    exp
   | exp RELOP    exp
   | exp AND      exp
   | exp OR       exp
   | <assoc=right> exp ASSIGNOP exp
   | LP exp RP
   | ID LP args* RP
   | ID
   | INT
   | FLOAT
   ;

args: exp (COMMA exp)*;