package nodes.expr;

import cfg.BasicBlock;
import cfg.quad.calc.Singleton;
import cfg.quad.func.Assign;
import cfg.quad.jump.TrueJump;
import nodes.Node;
import statics.assembly.AssemblyInfo;
import statics.assembly.AssemblyRes;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;

import java.io.IOException;

import static cfg.CFGBuilder.CFG_BUILDER;
import static cfg.quad.QuadUtil.isNumberFormat;

public class LOrExprNode extends Node {
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
            BasicBlock block = null;
            boolean fl = false;
            AssemblyRes r = new AssemblyRes();
            children.getFirst().assemble(info, r);
            if (isNumberFormat(r.res)) {
                if (Integer.parseInt(r.res) > 0) {
                    res.res = val = "1";
                    return;
                } else {
                    fl = true;
                }
            } else {
                block = CFG_BUILDER.createBB();
                res.res = val = CFG_BUILDER.tempVar();
                CFG_BUILDER.insert(new Assign(val, r.res));
                CFG_BUILDER.insert(new TrueJump(block, val));
                CFG_BUILDER.switchBlock(CFG_BUILDER.createBB());
            }
            r = new AssemblyRes();
            children.getLast().assemble(info, r);
            if (fl) {
                if (isNumberFormat(r.res)) {
                    res.res = val = Integer.parseInt(r.res) > 0 ? "1" : "0";
                    return;
                }
                res.res = val = CFG_BUILDER.tempVar();
            }
            CFG_BUILDER.insert(new Assign(val, r.res));
            if (!fl) {
                CFG_BUILDER.switchBlock(block);
            }
        }
        res.res = val;
    }

    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        for (Node node : children) {
            node.check(info, res);
        }
    }
}
