grammar OJ;

// comments
Comment : '//' ~[\r\n]* '\r'? '\n' -> skip ;

// programs
program : statement* EOF ;

// statements
statement
    : control_statement
    | assignment
    | declaration
    | output_statement ;

// assignment statements
assignment
    : int_assignment
    | array_assignment ;

int_assignment : variable EQ expression SEMICOLON ;
array_assignment : variable LBRACKET expression RBRACKET EQ expression SEMICOLON ;

// declarations
declaration
    : int_declaration
    | array_declaration ;

int_declaration : INT_SPACE variable SEMICOLON ;
array_declaration : INT_LBRACKET expression RBRACKET variable SEMICOLON ;

// control structures
control_statement
    : while_statement
    | if_statement ;

while_statement : WHILE_LPAREN condition RPAREN body ;

body
    : empty_statement
    | LBRACE statement* RBRACE ;

if_statement : IF_LPAREN condition RPAREN body ELSE body ;
empty_statement : SEMICOLON ;

// expressions
expression : term expression_ext* ;
expression_ext : add_operator term ;

add_operator
    : PLUS
    | MINUS ;

term : factor term_ext* ;
term_ext : mult_operator factor ;

mult_operator
    : STAR
    | SLASH
    | PERCENT ;

factor
    : variable
    | array_load
    | integer
    | LPAREN expression RPAREN
    | input_expression ;

array_load : variable LBRACKET expression RBRACKET ;

// conditions
condition : expression relation expression ;

relation
    : EQEQ
    | NOTEQ
    | LE
    | GE
    | LT
    | GT ;

// input/output
output_statement : OUT_LPAREN (expression | STRING) RPAREN_SEMICOLON ;
input_expression : IN ;

EQEQ : '==' ;
NOTEQ : '!=' ;
LE : '<=' ;
GE : '>=' ;
LT : '<' ;
GT : '>' ;

EQ : '=' ;
SEMICOLON : ';' ;
LBRACKET : '[' ;
RBRACKET : ']' ;
INT_SPACE : 'int ' ;
INT_LBRACKET : 'int[' ;
WHILE_LPAREN : 'while(' ;
RPAREN_SEMICOLON : ');' ;
LPAREN : '(' ;
RPAREN : ')' ;
LBRACE : '{' ;
RBRACE : '}' ;
IF_LPAREN : 'if(' ;
ELSE : 'else' ;
OUT_LPAREN : 'out(' ;
IN : 'in()' ;

PLUS : '+' ;
MINUS : '-' ;
STAR : '*' ;
SLASH : '/' ;
PERCENT : '%' ;

LETTER : 'a'..'z' | 'A'..'Z' ;
DIGIT : '0'..'9' ;

// variables
variable : LETTER (LETTER | DIGIT)* ;

// strings
STRING : '"' ('\\' SPECIAL_CHARACTER | CONTROL_CHARACTER | ~('\\' | '"'))* '"' ;

WS : [ \t\r\n]+ -> skip ;

// integers
integer : DIGIT DIGIT* ;

SPECIAL_CHARACTER
    : '\\'
    | '"' ;

CONTROL_CHARACTER : '\\' ('n' | 't') ;
