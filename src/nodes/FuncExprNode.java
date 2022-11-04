package nodes;

import statics.exception.CompException;
import statics.setup.Symbol;
import statics.setup.SyntaxType;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;

import static statics.exception.CompException.ExcDesc.CORRECT;
import static statics.exception.CompException.ExcDesc.UNDEFINED;
import static statics.setup.Symbol.SYMBOL;
import static statics.exception.Errors.errors;

public class FuncExprNode extends Node {
    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        Symbol.FuncSymbol fs = null;
        int line = 0;
        boolean fl = false;
        for (var node : children) {
            if (node.type == SyntaxType.IDENFR) {
                var funcName = ((TokenNode) node).content;
                fs = SYMBOL.getFunc(funcName);
                if (fs == null) {
                    errors.add(new CompException(UNDEFINED.toCode(), node.finishLine));
                } else if (fs.isVoid) {
                    res.dim = -1;
                }
            }
            var r = new ExcCheckRes();
            node.check(info, r);
            if (node.type == SyntaxType.IDENFR) {
                line = ((TokenNode) node).finishLine;
            } else if (fs != null &&
                    (node.type == SyntaxType.FUNC_ARGS ||
                            (!fl && node.type == SyntaxType.RPARENT))) {
                fl = true;
                CompException.Exception e = fs.matchParams(r.args);
                if (e != CORRECT.toCode()) {
                    errors.add(new CompException(e, line));
                }
            }
        }
    }
}
