/********************************************************************************
 * Copyright (c) 2015-2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 ********************************************************************************/


grammar FilterGrammar;

@header {
package org.eclipse.mdm.businessobjects.filter;
}

parse
 : expression EOF
 ;

expression
 : LPAREN expression RPAREN                       #parenExpression
 | NOT expression                                 #notExpression
 | left=attribute op=unary_comparator             #unaryComparatorExpression
 | left=attribute op=comparator right=value       #comparatorExpression
 | left=attribute op=list_comparator right=values #listComparatorExpression
 | left=expression op=and right=expression        #andExpression
 | left=expression op=or right=expression         #orExpression
 ;

attribute
 : ATTRIBUTE_IDENTIFIER
;
value
 : STRINGLITERAL 
 | DECIMAL
 | LONG
 | BOOL
;

values
 : LPAREN value ( ',' value ) * RPAREN
;

comparator
 : EQUAL 
 | NOT_EQUAL 
 | LESS_THAN 
 | LESS_THAN_OR_EQUAL 
 | GREATER_THAN 
 | GREATER_THAN_OR_EQUAL 
 | LIKE
 | NOT_LIKE
 | CASE_INSENSITIVE_EQUAL
 | CASE_INSENSITIVE_NOT_EQUAL
 | CASE_INSENSITIVE_LESS_THAN
 | CASE_INSENSITIVE_LESS_THAN_OR_EQUAL
 | CASE_INSENSITIVE_GREATER_THAN
 | CASE_INSENSITIVE_GREATER_THAN_OR_EQUAL
 | CASE_INSENSITIVE_LIKE
 | CASE_INSENSITIVE_NOT_LIKE
 ;
 
list_comparator
 : IN_SET
 | NOT_IN_SET
 | CASE_INSENSITIVE_IN_SET
 | CASE_INSENSITIVE_NOT_IN_SET
 | BETWEEN
 ;
 
unary_comparator
 : IS_NULL
 | IS_NOT_NULL
 ;
  
and: AND ;

or: OR ;

binary: AND | OR ;

AND                  : A N D ;
OR                   : O R ;
NOT                  : N O T ;

EQUAL                                  : E Q ;
NOT_EQUAL                              : N E ;
LESS_THAN                              : L T ;
LESS_THAN_OR_EQUAL                     : L E ;
GREATER_THAN                           : G T ;
GREATER_THAN_OR_EQUAL                  : G E ;
IN_SET                                 : I N ; 
NOT_IN_SET                             : N O T '_' I N;
LIKE                                   : L K ;
NOT_LIKE                               : N O T '_' L K;
CASE_INSENSITIVE_EQUAL                 : C I '_' E Q;
CASE_INSENSITIVE_NOT_EQUAL             : C I '_' N E;
CASE_INSENSITIVE_LESS_THAN             : C I '_' L T;
CASE_INSENSITIVE_LESS_THAN_OR_EQUAL    : C I '_' L E;
CASE_INSENSITIVE_GREATER_THAN          : C I '_' G T;
CASE_INSENSITIVE_GREATER_THAN_OR_EQUAL : C I '_' G E;
CASE_INSENSITIVE_IN_SET                : C I '_' I N;
CASE_INSENSITIVE_NOT_IN_SET            : C I '_' N O T '_' I N ;
CASE_INSENSITIVE_LIKE                  : C I '_' L K;
CASE_INSENSITIVE_NOT_LIKE              : C I '_' N O T '_' L K ;
IS_NULL                                : I S '_' N U L L ;
IS_NOT_NULL                            : I S '_' N O T '_' N U L L;
BETWEEN                                : B W ;

LPAREN               : '(' ;
RPAREN               : ')' ;
DECIMAL              : '-'? DIGIT+ '.' DIGIT+ ;
LONG                 : '-'? DIGIT+ ;
BOOL                 : T R U E | F A L S E ;
IDENTIFIER           : [a-zA-Z_] [a-zA-Z_0-9]* ;
ATTRIBUTE_IDENTIFIER : IDENTIFIER '.' IDENTIFIER ;
STRINGLITERAL        : QUOTE ( ~'\'' | ESCAPED_QUOTE )* QUOTE ;
WS                   : [ \r\t\u000C\n]+ -> skip ;

fragment DIGIT : [0-9] ;
fragment ESCAPED_QUOTE : '\\\'' ;
fragment QUOTE         : '\'' ;

/* case insensitive lexer matching */
fragment A          : ('a'|'A');
fragment B          : ('b'|'B');
fragment C          : ('c'|'C');
fragment D          : ('d'|'D');
fragment E          : ('e'|'E');
fragment F          : ('f'|'F');
fragment G          : ('g'|'G');
fragment H          : ('h'|'H');
fragment I          : ('i'|'I');
fragment J          : ('j'|'J');
fragment K          : ('k'|'K');
fragment L          : ('l'|'L');
fragment M          : ('m'|'M');
fragment N          : ('n'|'N');
fragment O          : ('o'|'O');
fragment P          : ('p'|'P');
fragment Q          : ('q'|'Q');
fragment R          : ('r'|'R');
fragment S          : ('s'|'S');
fragment T          : ('t'|'T');
fragment U          : ('u'|'U');
fragment V          : ('v'|'V');
fragment W          : ('w'|'W');
fragment X          : ('x'|'X');
fragment Y          : ('y'|'Y');
fragment Z          : ('z'|'Z');