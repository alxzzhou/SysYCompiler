package nodes;

import statics.exception.CompException;
import statics.setup.Symbol;
import statics.setup.SyntaxType;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;

import static statics.exception.CompException.ExcDesc.MODIFY_CONST;
import static statics.exception.CompException.ExcDesc.UNDEFINED_IDENT;
import static statics.setup.Symbol.SYMBOL;
import static statics.exception.Errors.errors;

public class LValNode extends Node {
    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        Symbol.LValSymbol lvs = null;
        String name = null;
        int cnt = 0;
        for (Node node : children) {
            if (node.type == SyntaxType.IDENFR) {
                name = ((TokenNode) node).content;
                lvs = SYMBOL.getVar(name);
                if (lvs == null) {
                    errors.add(new CompException(UNDEFINED_IDENT.toCode(), node.finishLine));
                } else if (info.isLVal && lvs.isConst) {
                    errors.add(new CompException(MODIFY_CONST.toCode(), node.finishLine));
                }
                info.isLVal = false;
                var r = new ExcCheckRes();
                node.check(info, r);
                if (node.type == SyntaxType.EXPR) {
                    cnt++;
                }
            }
        }
        if (lvs != null) {
            res.dim = lvs.dims.size() - cnt;
        }
    }
}
