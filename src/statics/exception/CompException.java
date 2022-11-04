package statics.exception;

import static statics.exception.CompException.ExcDesc.CORRECT;
import static statics.exception.CompException.ExcDesc.INVALID_CHAR;
import static statics.exception.CompException.ExcDesc.LACK_BRACKET;
import static statics.exception.CompException.ExcDesc.LACK_PARENT;
import static statics.exception.CompException.ExcDesc.LACK_RETURN;
import static statics.exception.CompException.ExcDesc.LACK_SEMICN;
import static statics.exception.CompException.ExcDesc.MODIFY_CONST;
import static statics.exception.CompException.ExcDesc.MULTIPLE_DEFINITION;
import static statics.exception.CompException.ExcDesc.REDUNDANT_RETURN;
import static statics.exception.CompException.ExcDesc.UNDEFINED;
import static statics.exception.CompException.ExcDesc.UNDEFINED_IDENT;
import static statics.exception.CompException.ExcDesc.UNEXPECTED_BREAK_CONTINUE;
import static statics.exception.CompException.ExcDesc.UNMATCHED_PARAM_NUM;
import static statics.exception.CompException.ExcDesc.UNMATCHED_PARAM_TYPE;
import static statics.exception.CompException.ExcDesc.UNMATCHED_PRINT_PARAMS;
import static statics.exception.CompException.Exception.a;
import static statics.exception.CompException.Exception.b;
import static statics.exception.CompException.Exception.c;
import static statics.exception.CompException.Exception.correct;
import static statics.exception.CompException.Exception.d;
import static statics.exception.CompException.Exception.e;
import static statics.exception.CompException.Exception.f;
import static statics.exception.CompException.Exception.g;
import static statics.exception.CompException.Exception.h;
import static statics.exception.CompException.Exception.i;
import static statics.exception.CompException.Exception.j;
import static statics.exception.CompException.Exception.k;
import static statics.exception.CompException.Exception.l;
import static statics.exception.CompException.Exception.m;
import static statics.exception.CompException.Exception.u;

public class CompException {
    public final Exception code;
    public final int line;

    public CompException(Exception e, int line) {
        this.code = e;
        this.line = line;
    }
    public enum Exception {
        a(INVALID_CHAR),
        b(MULTIPLE_DEFINITION),
        c(UNDEFINED_IDENT),
        d(UNMATCHED_PARAM_NUM),
        e(UNMATCHED_PARAM_TYPE),
        f(REDUNDANT_RETURN),
        g(LACK_RETURN),
        h(MODIFY_CONST),
        i(LACK_SEMICN),
        j(LACK_PARENT),
        k(LACK_BRACKET),
        l(UNMATCHED_PRINT_PARAMS),
        m(UNEXPECTED_BREAK_CONTINUE),
        u(UNDEFINED),
        correct(CORRECT);

        Exception(ExcDesc i) {
        }
    }

    public enum ExcDesc {
        INVALID_CHAR(a),
        MULTIPLE_DEFINITION(b),
        UNDEFINED_IDENT(c),
        UNMATCHED_PARAM_NUM(d),
        UNMATCHED_PARAM_TYPE(e),
        REDUNDANT_RETURN(f),
        LACK_RETURN(g),
        MODIFY_CONST(h),
        LACK_SEMICN(i),
        LACK_PARENT(j),
        LACK_BRACKET(k),
        UNMATCHED_PRINT_PARAMS(l),
        UNEXPECTED_BREAK_CONTINUE(m),
        UNDEFINED(u),
        CORRECT(correct);

        private final Exception ex;

        ExcDesc(Exception ex) {
            this.ex = ex;
        }

        public Exception toCode() {
            return ex;
        }
    }
}
