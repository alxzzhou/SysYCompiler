package nodes;

import cfg.quad.Quadruple;
import cfg.quad.calc.Singleton;
import cfg.quad.logic.GEQ;
import cfg.quad.logic.GRE;
import cfg.quad.logic.LEQ;
import cfg.quad.logic.LSS;
import statics.assembly.AssemblyInfo;
import statics.assembly.AssemblyRes;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;

import java.io.IOException;

import static cfg.CFGBuilder.CFG_BUILDER;
import static cfg.quad.QuadUtil.isNumberFormat;

public class RelExprNode extends Node {
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
            var op = children.get(1);
            children.getFirst().assemble(info, r1);
            children.getLast().assemble(info, r2);
            if (isNumberFormat(r1.res) && isNumberFormat(r2.res)) {
                int v1 = Integer.parseInt(r1.res);
                int v2 = Integer.parseInt(r2.res);
                switch (op.type) {
                    case GEQ -> val = eq(v1 >= v2);
                    case LEQ -> val = eq(v1 <= v2);
                    case GRE -> val = eq(v1 > v2);
                    case LSS -> val = eq(v1 < v2);
                }
            } else {
                val = CFG_BUILDER.tempVar();
                Quadruple insert = null;
                switch (op.type) {
                    case GEQ -> insert = new GEQ(val, r1.res, r2.res);
                    case LEQ -> insert = new LEQ(val, r1.res, r2.res);
                    case GRE -> insert = new GRE(val, r1.res, r2.res);
                    case LSS -> insert = new LSS(val, r1.res, r2.res);
                }
                CFG_BUILDER.insert(insert);
            }
        }
        res.res = val;
    }

    String eq(boolean b) {
        return b ? "1" : "0";
    }

    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        for (var node : children) {
            node.check(info, res);
        }
    }
}
