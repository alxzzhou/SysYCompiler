package nodes;

import statics.exception.CompException;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;

import static statics.exception.CompException.ExcDesc.UNEXPECTED_BREAK_CONTINUE;
import static statics.exception.Errors.errors;

public class BreakNode extends Node {
    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        for (var node : children) {
            node.check(info, res);
        }
        if (info.loop == 0) {
            errors.add(new CompException(UNEXPECTED_BREAK_CONTINUE.toCode()
                    , children.getLast().startLine));
        }
    }
}
