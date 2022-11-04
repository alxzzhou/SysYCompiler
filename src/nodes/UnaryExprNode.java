package nodes;

import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;

public class UnaryExprNode extends Node {
    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        for (Node node : children) {
            node.check(info, res);
        }
    }
}
