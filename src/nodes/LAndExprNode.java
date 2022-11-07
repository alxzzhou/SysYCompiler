package nodes;

import cfg.quad.calc.Singleton;
import cfg.quad.logic.AND;
import statics.assembly.AssemblyInfo;
import statics.assembly.AssemblyRes;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;

import java.io.IOException;

import static cfg.CFGBuilder.CFG_BUILDER;
import static cfg.quad.QuadUtil.isNumberFormat;

public class LAndExprNode extends Node {
    String val;

    @Override
    public void assemble(AssemblyInfo info, AssemblyRes res) throws IOException {
        if (children.size() == 1) {
            AssemblyRes r = new AssemblyRes();
            children.getFirst().assemble(info, r);
            if (isNumberFormat(r.res)) {
                val = r.res;
            } else {
                val = CFG_BUILDER.tempVar();
                CFG_BUILDER.insert(new Singleton(val, r.res));
            }
        } else if (children.size() == 3) {
            AssemblyRes r1 = new AssemblyRes(), r2 = new AssemblyRes();
            var op = children.get(1);
            children.getFirst().assemble(info, r1);
            children.getLast().assemble(info, r2);
            if (isNumberFormat(r1.res) && isNumberFormat(r2.res)) {
                int v1 = Integer.parseInt(r1.res);
                int v2 = Integer.parseInt(r2.res);
                val = String.valueOf(v1 & v2);
            } else {
                val = CFG_BUILDER.tempVar();
                CFG_BUILDER.insert(new AND(val, r1.res, r2.res));
            }
        }
        res.res = val;
    }

    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        for (var node : children) {
            node.check(info, res);
        }
    }
}
