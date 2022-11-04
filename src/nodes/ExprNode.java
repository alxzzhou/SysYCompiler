package nodes;

import statics.assembly.AssemblyInfo;
import statics.assembly.AssemblyRes;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;

import java.io.IOException;

public class ExprNode extends Node {
    String res;
    public void assemble(AssemblyInfo info, AssemblyRes res) throws IOException {
        children.getFirst().assemble(info,res);
        this.res = res.res;
    }
    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        for (Node node : children) {
            var r = new ExcCheckRes();
            node.check(info, r);
            if (r.isConst) {
                res.isConst = true;
            }
            res.dim = r.dim;
        }
    }
}
