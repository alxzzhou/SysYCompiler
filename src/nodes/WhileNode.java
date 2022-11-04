package nodes;

import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;

public class WhileNode extends Node {
    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        info.loop++;
        for (var node : children) {
            node.check(info, res);
        }
        info.loop--;
    }
}
