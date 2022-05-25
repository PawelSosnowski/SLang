grammar SLang;

prog: block;

block: ( (stat|function)? NEWLINE )* ;

stat:	IF comparison ':' blockif FI  #if
      | FOR repetitions ':' block ROF     #for
      | ID '=' expr0		      #assign
	| PRINT ID   		      #print
      | read0                       #read
      | ID '()'                     #call
;

expr0:  expr1	            #single0
      | expr1 ADD expr0       #add
      | expr1 SUB expr0       #sub 
      | expr1 ADD expr1	      #add
      | expr1 SUB expr1       #sub 
;

expr1:  expr2			#single1
      | expr2 MULT expr1	#mult
      | expr2 DIV expr1       #div
      | expr2 MULT expr2	#mult
      | expr2 DIV expr2       #div 
;

expr2: value			#value0
      | TOINT expr2		#toInt
      | TOREAL expr2		#toReal
      | '(' expr0 ')'		#par
;	

read0:  READ_INT ID          #readInt
      | READ_DOUBLE ID       #readDouble
;

value: ID
      | REAL
      | INT
;

repetitions: INT
;

comparison: ID '==' INT       #equal
            | ID '!=' INT     #nonequal
;

function: FUN fid fblock NUF;

fid: ID;
fblock: ( stat? NEWLINE )*;

blockif: block;

IF: 'if';
FI: 'fi';
FOR: 'for';
ROF: 'rof';
FUN: 'fun';
NUF: 'nuf';

READ_DOUBLE: 'peepf' ;
READ_INT:   'peepi' ;
PRINT:	'yeet' ;

TOINT: '(int)' ;
TOREAL: '(double)' ;

ID:   ('a'..'z'|'A'..'Z')+ ;

REAL: '0'..'9'+'.''0'..'9'+ ;
INT: '0'..'9'+ ;

ADD: '+' ;
MULT: '*' ;
SUB:  '-' ;
DIV:  '/' ;

NEWLINE:	'\r'? '\n' ;

WS:   (' '|'\t')+ { skip(); } ;

//TODO: scopes, function