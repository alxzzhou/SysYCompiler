package nodes;

import statics.exception.CompException;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;

import static statics.exception.Errors.errors;

public class ErrorNode extends Node {
    CompException.Exception errType;
    int line;

    public ErrorNode(CompException ce) {
        this.errType = ce.code;
        this.line = ce.line;
    }

    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        errors.add(new CompException(errType,line));
    }
}
