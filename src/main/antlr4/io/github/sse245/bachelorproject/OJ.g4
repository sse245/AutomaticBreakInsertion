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

int_assignment : variable '=' expression ';' ;
array_assignment : variable '[' expression ']' '=' expression ';' ;

// declarations
declaration
    : int_declaration
    | array_declaration ;

int_declaration : 'int ' variable ';' ;
array_declaration : 'int[' expression '] ' variable ';' ;

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
expression : term expression_ext* ;
expression_ext : add_operator term ;

add_operator
    : '+'
    | '-' ;

term : factor term_ext* ;
term_ext : mult_operator factor ;

mult_operator
    : '*'
    | '/'
    | '%' ;

factor
    : variable
    | array_load
    | integer
    | '(' expression ')'
    | input_expression ;

array_load : variable '[' expression ']' ;

// conditions
condition : expression relation expression ;

relation
    : '=='
    | '!='
    | '<='
    | '>='
    | '<'
    | '>' ;

// input/output
output_statement : 'out(' (expression | STRING) ');' ;
input_expression : 'in()' ;

LETTER : 'a'..'z' | 'A'..'Z' ;
DIGIT : '0'..'9' ;

// variables
variable : LETTER (LETTER | DIGIT)* ;

// strings
STRING : '"' STRING_ELEMENT* '"' ;

STRING_ELEMENT
    : '\\' SPECIAL_CHARACTER
    | CONTROL_CHARACTER
    | ~('\\' | '"') ;

Whitespace : [ \t\r\n]+ -> skip;

// integers
integer : DIGIT DIGIT* ;

SPECIAL_CHARACTER
    : '\\'
    | '"' ;

CONTROL_CHARACTER : '\\' ('n' | 't') ;
