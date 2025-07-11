// $antlr-format alignTrailingComments true, columnLimit 150, minEmptyLines 1, maxEmptyLinesToKeep 1, reflowComments false, useTab false
// $antlr-format allowShortRulesOnASingleLine false, allowShortBlocksOnASingleLine true, alignSemicolons hanging, alignColons hanging

grammar TreeSQL;

options {
    caseInsensitive = false;
}

expr
    : literalValue
    | NOT ' ' expr
    | expr ' '? ( STAR | DIV | MOD) ' '? expr
    | expr ' '? ( PLUS | MINUS) ' '? expr
    | expr ' '? ( LT | LT_EQ | GT | GT_EQ | NOT_EQ | EQ) ' '? expr
    | expr ' '? ( NOT | IN | NOT_IN | MATCH | NOT_MATCH) ' '? expr
    | expr ' '? AND ' '? expr
    | expr ' '? OR ' '? expr
    | expr ' '? IS ' '? expr
    | functionName OPEN_PAR funcArgs CLOSE_PAR
    | functionName OPEN_PAR CLOSE_PAR
    | OPEN_PAR expr CLOSE_PAR
    ;

funcArgs
    : ((expr ( COMMA ' '? expr)*))?
    ;

literalValue
    : NUMERIC_LITERAL
    | STRING_LITERAL
    | JSON_TEXT_VALUE
    | RANGED_LITERAL
    | VALUE_AT_LITERAL
    | PATH_VARIABLE
    | NULL
    | TRUE
    | FALSE
    ;

selectStmt
    : selectCore whereExpr? orderByStmt? limitStmt?
    ;

selectCore
    : (
        SELECT ' ' resultColumn (COMMA ' '? resultColumn)* (
            ' ' FROM ' ' (jsonTextValue (' JOIN '? jsonTextValue)*)
        )?
    )
    ;

whereExpr
    : ' ' WHERE ' ' expr
    ;

jsonTextValue
    : JSON_TEXT_VALUE
    ;

resultColumn
    : expr (' ' AS ' ' columnAlias)?
    ;

orderByStmt
    : ' ' ORDER_BY ' ' orderingTerm
    ;

limitStmt
    : ' ' LIMIT ' ' NUMERIC_LITERAL
    ;

orderingTerm
    : expr (' ' ascOrDesc)?
    ;

ascOrDesc
    : ASC
    | DESC
    ;

columnAlias
    : STRING_LITERAL
    ;

functionName
    : FUNC_NAME
    ;

OPEN_PAR
    : '('
    ;

CLOSE_PAR
    : ')'
    ;

COMMA
    : ','
    ;

IS
    : 'IS'
    ;

STAR
    : '*'
    ;

PLUS
    : '+'
    ;

MINUS
    : '-'
    ;

DIV
    : '/'
    ;

MOD
    : '%'
    ;

LT
    : '<'
    ;

LT_EQ
    : '<='
    ;

GT
    : '>'
    ;

GT_EQ
    : '>='
    ;

EQ
    : '=='
    ;

NOT_EQ
    : '!='
    ;

AND
    : 'AND'
    ;

AS
    : 'AS'
    ;

ASC
    : 'ASC'
    ;

DESC
    : 'DESC'
    ;

FROM
    : 'FROM'
    ;

IN
    : 'IN'
    ;

NOT_IN
    : 'NOT IN'
    ;

LIMIT
    : 'LIMIT'
    ;

MATCH
    : 'MATCH'
    ;

NOT_MATCH
    : 'NOT MATCH'
    ;

NOT
    : 'NOT'
    ;

NULL
    : 'NULL'
    ;

OR
    : 'OR'
    ;

ORDER_BY
    : 'ORDER BY'
    ;

SELECT
    : 'SELECT'
    ;

WHERE
    : 'WHERE'
    ;

TRUE
    : 'TRUE'
    ;

FALSE
    : 'FALSE'
    ;

FUNC_NAME
    : [A-Z_]+
    ;

TREE_VALUE_TYPE
    : 'STRING' | 'NUMBER' | 'LIST' | 'BOOL' | NULL
    ;

NUMERIC_LITERAL
    : '-'? (DIGIT+ ('.' DIGIT*)?)
    ;

STRING_LITERAL
    : '\'' (~'\'' | '\'\'')* '\''
    ;

JSON_TEXT_VALUE
    : '"' (ESC_JSON | SAFECODEPOINT_JSON)* '"'
    ;

RANGED_LITERAL
    : ([a-z]) ([0-9a-z_])+
    ;

VALUE_AT_LITERAL
    : '@' RANGED_LITERAL
    ;

PATH_VARIABLE
    : '~' RANGED_LITERAL
    ;

fragment ESC_JSON
    : '\\' (["\\/bfnrt] | UNICODE)
    ;

fragment UNICODE
    : 'u' HEX HEX HEX HEX
    ;

fragment HEX
    : [0-9A-Fa-f]
    ;

fragment SAFECODEPOINT_JSON
    : ~ ["\\\u0000-\u001F]
    ;

fragment DIGIT
    : [0-9]
    ;

