package nodes;

import statics.exception.CompException;
import statics.setup.SyntaxType;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;

import java.util.ArrayList;
import java.util.List;

import static statics.exception.CompException.ExcDesc.MULTIPLE_DEFINITION;
import static statics.setup.Symbol.SYMBOL;
import static statics.exception.Errors.errors;

public class ConstDefNode extends Node {
    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        String name = null;
        int line = 0;
        List<Integer> lim = new ArrayList<>();
        for (var node : children) {
            if (node.type == SyntaxType.IDENFR) {
                name = ((TokenNode) node).content;
                line = node.finishLine;
            }
            var r = new ExcCheckRes();
            node.check(info, r);
            if (node.type == SyntaxType.EXPR) {
                lim.add(r.val);
            }
        }
        if (SYMBOL.addVar(info.isConst, name, lim, new ArrayList<>()) == 0) {
            errors.add(new CompException(MULTIPLE_DEFINITION.toCode(), line));
        }
    }
}
