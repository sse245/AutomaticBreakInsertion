grammar OJ;

// comments
Comment : '//' ~[\r\n]* '\r'? '\n' -> skip ;
Whitespace : [ \t\r\n]+ -> skip;

// programs
program : statement* EOF ;

// statements
statement
    : control_statement
    | assignment
    | declaration
    | output_statement ;

// assignment statements
assignment : variable '=' expression ';' ;

// declarations
declaration : 'int ' variable ';' ;

// control structures
control_statement
    : while_statement
    | if_statement ;

while_statement : 'while(' condition ')' body ;

body
    : empty_statement
    | '{' statement* '}' ;

if_statement : 'if(' condition ')' body 'else' body ;
empty_statement : ';' ;

// expressions
expression : term (add_operator term)* ;

add_operator
    : '+'
    | '-' ;

term : factor (mult_operator factor)* ;

mult_operator
    : '*'
    | '/'
    | '%' ;

factor
    : variable
    | integer
    | '(' expression ')'
    | input_expression ;

// conditions
condition : expression relation expression ;

relation
    : '=='
    | '!='
    | '<='
    | '>='
    | '<'
    | '>' ;

// integers
integer : DIGIT DIGIT* ;

// input/output
output_statement : 'out(' (expression | string) ');' ;
input_expression : 'in()' ;

LETTER : 'a'..'z' | 'A'..'Z' ;
DIGIT : '0'..'9' ;

// strings
string : '"' STRING_ELEMENT* '"' ;

STRING_ELEMENT
    : '\\' SPECIAL_CHARACTER
    | CONTROL_CHARACTER
    | ~('\\' | '"') ;

SPECIAL_CHARACTER
    : '\\'
    | '"' ;

CONTROL_CHARACTER : '\\' ('n' | 't') ;

// variables
variable : LETTER (LETTER | DIGIT)* ;