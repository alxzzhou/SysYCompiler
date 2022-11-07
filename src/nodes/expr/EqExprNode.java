package nodes.expr;

import cfg.quad.calc.Singleton;
import cfg.quad.logic.EQ;
import cfg.quad.logic.NEQ;
import nodes.Node;
import statics.assembly.AssemblyInfo;
import statics.assembly.AssemblyRes;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;

import java.io.IOException;

import static cfg.CFGBuilder.CFG_BUILDER;
import static cfg.quad.QuadUtil.isNumberFormat;
import static statics.setup.SyntaxType.EQL;
import static statics.setup.SyntaxType.NEQ;

public class EqExprNode extends Node {
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
            res.res = val;
        } else if (children.size() == 3) {
            AssemblyRes r1 = new AssemblyRes(), r2 = new AssemblyRes();
            Node op = children.get(1);
            children.getFirst().assemble(info, r1);
            children.getLast().assemble(info, r2);
            if (isNumberFormat(r1.res) && isNumberFormat(r2.res)) {
                int v1 = Integer.parseInt(r1.res);
                int v2 = Integer.parseInt(r2.res);
                val = op.type == EQL ? eq(v1 == v2) :
                        op.type == NEQ ? eq(v1 != v2) : "0";
            } else {
                val = CFG_BUILDER.tempVar();
                CFG_BUILDER.insert(
                        op.type == EQL ? new EQ(val, r1.res, r2.res) :
                                new NEQ(val, r1.res, r2.res)
                );
            }
        }
        res.res = val;
    }

    String eq(boolean b) {
        return b ? "1" : "0";
    }

    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        for (Node node : children) {
            node.check(info, res);
        }
    }
}
