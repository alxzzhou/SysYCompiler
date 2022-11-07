package statics.assembly;

public enum AssemblyType {

    LOAD_ADDRESS,
    CREATE_POINTER,
    ASSIGN,

    NOT_UNI,
    NEGATE_UNI,

    LW,
    SW,

    JUMP_TRUE,
    JUMP_FALSE,
    JUMP,

    SINGLETON,
    EQ_BIN,
    OR_BIN,
    AND_BIN,
    NEQ_BIN,
    GE_BIN,
    GT_BIN,
    LE_BIN,
    LT_BIN,
    PLUS_BIN,
    MINUS_BIN,
    MUL_BIN,
    DIV_BIN,
    MOD_BIN,

    FUNC_CALL,
    PRINT_INT,
    PRINT_STR,
    RETURN,
    READ_INT,
    GETINT,

    PHI,

    LOAD_PARAM,
    PUSH_PARAM,
    GET_RETURN,
    END
}
