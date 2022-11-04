package nodes;

import statics.setup.SyntaxType;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;

public class NumberNode extends Node {
    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        if (children.getLast().type == SyntaxType.INTCON) {
            String content = ((TokenNode) children.getLast()).content;
            res.val = Integer.parseInt(content);
            res.isConst = true;
        }
    }
}
