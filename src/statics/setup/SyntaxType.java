package statics.setup;

import java.util.HashMap;
import java.util.HashSet;

public enum SyntaxType {
    IDENFR, INTCON, STRCON, MAINTK, CONSTTK, INTTK, BREAKTK,
    CONTINUETK, IFTK, ELSETK, NOT, AND, OR, WHILETK, GETINTTK, PRINTFTK,
    RETURNTK, PLUS, MINU, VOIDTK, MULT, DIV, MOD, LSS, LEQ, GRE, GEQ, EQL, NEQ,
    ASSIGN, SEMICN, COMMA, LPARENT, RPARENT, LBRACK, RBRACK, LBRACE, RBRACE,
    LCOMMENT, BCOMMENT,
    ERR, EOF,


    COMP_UNIT,
    LITERAL_EXPR,
    VAR_EXPR,
    FUNC_ARGS,
    FUNC_EXPR,
    PREFIX_EXPR,
    PREFIX_OP,
    BIN_EXPR,
    EXPR,
    EXPR_STMT,
    BLOCK,
    IF_STMT,
    WHILE_STMT,
    BREAK_STMT,
    CONTINUE_STMT,
    RETURN_STMT,
    ASSIGN_STMT,
    PRINTF_STMT,
    STMT,
    INIT_VAL,
    VAR_DEF,
    VAR_DECL,
    FUNC_TYPE,
    FUNC_PARAM,
    FUNC_PARAMS,
    FUNC_DEF,
    ADD_EXPR, MUL_EXPR, UNARY_EXPR, PRIMARY_EXPR, NUMBER, LVAL, CONST_DECL, CONST_DEF, CONST_INIT_VAL, LOR_EXPR, LAND_EXPR, EQ_EXPR, REL_EXPR;


    public static final HashMap<String, SyntaxType> KEYWORDS =
            new HashMap<String, SyntaxType>() {{
                put("main", MAINTK);
                put("const", CONSTTK);
                put("int", INTTK);
                put("break", BREAKTK);
                put("continue", CONTINUETK);
                put("if", IFTK);
                put("else", ELSETK);
                put("while", WHILETK);
                put("getint", GETINTTK);
                put("printf", PRINTFTK);
                put("return", RETURNTK);
                put("void", VOIDTK);
            }};

    public static final HashSet<SyntaxType> STMT_PREFIX =
            new HashSet<SyntaxType>() {{
                add(SEMICN);
                add(LBRACE);
                add(IFTK);
                add(WHILETK);
                add(BREAKTK);
                add(CONTINUETK);
                add(RETURNTK);
                add(PRINTFTK);

                add(IDENFR);
                add(LPARENT);
                add(INTCON);
                add(PLUS);
                add(MINU);
                add(NOT);
            }};

    public static final HashSet<SyntaxType> EXPR_PREFIX =
            new HashSet<SyntaxType>() {{
                add(IDENFR);
                add(LPARENT);
                add(INTCON);
                add(PLUS);
                add(MINU);
                add(NOT);
            }};

    public static final HashSet<SyntaxType> OPERAND_PREFIX =
            new HashSet<SyntaxType>() {{
                add(PLUS);
                add(MINU);
                add(NOT);
            }};

    public static final HashMap<SyntaxType, Integer> OPERAND_PRIORITY =
            new HashMap<SyntaxType, Integer>() {{
                put(OR, 1);
                put(AND, 2);
                put(EQL, 3);
                put(NEQ, 3);
                put(GEQ, 4);
                put(LEQ, 4);
                put(GRE, 4);
                put(LSS, 4);
                put(PLUS, 5);
                put(MINU, 5);
                put(DIV, 6);
                put(MULT, 6);
                put(MOD, 6);
            }};

    public static SyntaxType checkKeyWords(String s) {
        return KEYWORDS.getOrDefault(s, IDENFR);
    }
}
