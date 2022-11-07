package nodes;

import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;
import statics.setup.SyntaxType;

public class FuncTypeNode extends Node {
    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        if (children.get(0).type == SyntaxType.VOIDTK) {
            res.isVoid = true;
        }
    }
}
