package nodes;

import statics.exception.CompException;
import statics.setup.SyntaxType;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;

import static statics.exception.CompException.ExcDesc.REDUNDANT_RETURN;
import static statics.exception.Errors.errors;
import static statics.setup.SyntaxType.EXPR;

public class ReturnNode extends Node {
    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        int line = 0;
        for (var node : children) {
            node.check(info, res);
            if (node.type == SyntaxType.RETURNTK) {
                line = node.startLine;
            } else if (node.type == EXPR && info.inVoid) {
                errors.add(new CompException(REDUNDANT_RETURN.toCode(), line));
            }
        }
    }
}
