package statics.assembly;

public enum AssemblyType {

    LOAD_ADDRESS,
    CREATE_POINTER,
    ASSIGN,

    NOT_LOGIC,
    NEGATE_ARITH,

    LW,
    SW,

    JUMP_TRUE,
    JUMP_FALSE,
    JUMP,

    SINGLETON,
    EQ_LOGIC,
    OR_LOGIC,
    AND_LOGIC,
    NEQ_LOGIC,
    GE_LOGIC,
    GT_LOGIC,
    LE_LOGIC,
    LT_LOGIC,
    PLUS_ARITH,
    MINUS_ARITH,
    MUL_ARITH,
    DIV_ARITH,
    MOD_ARITH,

    FUNC_CALL,
    PRINT_INT,
    PRINT_STR,
    RETURN,
    GET_INT,
    FETCH_INT,

    PHI,

    LOAD_PARAM,
    PUSH_PARAM,
    GET_RETURN,
    END
}
