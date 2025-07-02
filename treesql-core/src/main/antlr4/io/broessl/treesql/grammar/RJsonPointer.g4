// $antlr-format alignTrailingComments true, columnLimit 150, minEmptyLines 1, maxEmptyLinesToKeep 1, reflowComments false, useTab false
// $antlr-format allowShortRulesOnASingleLine false, allowShortBlocksOnASingleLine true, alignSemicolons hanging, alignColons hanging

grammar RJsonPointer;

options {
    caseInsensitive = true;
}

//it does not seem to work as i want it. lab.antlr.org gives me other results than antlr4 runtime. hard to understand these quirks
rJsonPointer
    : relativeRJsonPointer EOF
    | absoluteRJsonPointer EOF
    ;

absoluteRJsonPointer
    : SLASH relativeRJsonPointer
    ;

relativeRJsonPointer
    : step (SLASH step)*
    ;

step
    : simpleStep
    | ANON_FORWARD_RANGE
    | ANON_BACKWARD_RANGE
    | namedForwardRange
    | namedBackwardRange
    | depthScan
    | levelScan
    ;

NUMERIC_LITERAL
    : '-'? (DIGIT+ ('.' DIGIT*)?)
    ;

fragment DIGIT
    : [0-9]
    ;

RANGED_LITERAL
    : ([a-z]) ([0-9a-z_])+
    ;

TILDE_PLUS_LITERAL
    : '~' RANGED_LITERAL
    ;

DOTDOTTILDE_PLUS_LITERAL
    : '..~' RANGED_LITERAL
    ;

simpleStep
    : (~('/' | '~') | '~0' | '~1')*
    ;

namedForwardRange
    : TILDE_PLUS_LITERAL
    ;

namedBackwardRange
    : DOTDOTTILDE_PLUS_LITERAL
    ;

depthScan
    : '{' NUMERIC_LITERAL ',' NUMERIC_LITERAL '}~' RANGED_LITERAL?
    ;

levelScan
    : '[' NUMERIC_LITERAL ',' NUMERIC_LITERAL ']~' RANGED_LITERAL?
    ;

ANON_FORWARD_RANGE
    : '~'
    ;

SLASH
    : '/'
    ;

ANON_BACKWARD_RANGE
    : '..~'
    ;
