package nodes;

import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;

public class EqExprNode extends Node {
    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        for (var node : children) {
            node.check(info, res);
        }
    }
}