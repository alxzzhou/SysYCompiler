package nodes;

import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;

import static statics.setup.SyntaxType.EXPR;

public class FuncArgsNode extends Node {
    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        for (var node : children) {
            var r = new ExcCheckRes();
            node.check(info, r);
            if (node.type == EXPR) {
                res.args.add(r.dim);
            }
        }
    }
}
