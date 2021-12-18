parser grammar CmmParser;

options {
    tokenVocab=CmmLexer;
}

// high level definitions
program: extDef* EOF;

extDef: specifier extDecList? SEMI // #ExtDefVars
      | specifier funDec compSt // #ExtDefFuc
      ;

extDecList: varDec (COMMA varDec)*;

// specifilers
specifier: TYPE // #SpecifierType
         | structSpecifier // #SpecifierStruct
         ;

structSpecifier: STRUCT optTag LC defList RC // #SSWithBody
               | STRUCT tag // #SSWithoutBody
               ;

optTag: ID?;

tag: ID;

// declarators
varDec: ID (LB INT RB)*
      | ID (LB INT RB)* (LB (FLOAT|ID) RB) (LB (INT|FLOAT|ID) RB)* { notifyErrorListeners ("Index must be an integer"); }
      ;

funDec: ID LP varList? RP;

varList: paramDec (COMMA paramDec)*;

paramDec: specifier varDec;

// statements
compSt: LC defList stmtList RC;

stmtList: stmt*;

stmt: compSt // #StmtChildScope
    | RETURN? exp SEMI // #StmtReturn
    | IF LP exp RP stmt (ELSE stmt)? // #StmtIf
    | WHILE LP exp RP stmt // #StmtWhile
    ;

// local definitions
defList: def*;

def: specifier decList SEMI;

decList: dec (COMMA dec)*;

dec: varDec (ASSIGNOP exp)*;

// expressions and args
exp: ID LP args* RP #ExpFuncCall
   | exp LB exp RB #ExpArrayRef
   | exp DOT ID #ExpStructRef
   | <assoc=right> NOT exp #ExpNot
   | <assoc=right> MINUS exp #ExpNegative
   | exp (DIV|STAR) exp #ExpMulOrDiv
   | exp (MINUS|PLUS) exp #ExpPlusOrMinus
   | exp RELOP exp #ExpCompare
   | exp AND exp #ExpAnd
   | exp OR exp #ExpOr
   | <assoc=right> exp ASSIGNOP exp #ExpAssign
   | LP exp RP #ExpParenthesis
   | ID #ExpId
   | INT #ExpInt
   | FLOAT #ExpFloat
   ;

args: exp (COMMA exp)*;