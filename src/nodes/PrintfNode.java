package nodes;

import statics.exception.CompException;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;

import static statics.exception.CompException.ExcDesc.UNMATCHED_PRINT_PARAMS;
import static statics.exception.Errors.errors;
import static statics.setup.SyntaxType.EXPR;
import static statics.setup.SyntaxType.STRCON;

public class PrintfNode extends Node {
    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        int cnt = 0, line = 0;
        String formatStr = null;
        for (var node : children) {
            node.check(info, res);
            if (node.type == EXPR) {
                cnt++;
            } else if (node.type == STRCON) {
                formatStr = ((TokenNode) node).content;
                line = node.finishLine;
            }
        }
        if (cnt != 0) {
            errors.add(new CompException(UNMATCHED_PRINT_PARAMS.toCode(), line));
        }
    }
}
