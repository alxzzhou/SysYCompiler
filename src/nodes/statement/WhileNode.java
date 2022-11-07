package nodes.statement;

import cfg.BasicBlock;
import cfg.quad.jump.FalseJump;
import nodes.Node;
import nodes.expr.LOrExprNode;
import statics.assembly.AssemblyInfo;
import statics.assembly.AssemblyRes;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;

import java.io.IOException;

import static cfg.CFGBuilder.CFG_BUILDER;

public class WhileNode extends Node {
    @Override
    public void assemble(AssemblyInfo info, AssemblyRes res) throws IOException {
        BasicBlock b = info.breakBlock;
        boolean loop = info.inLoop;
        // CONDITION
        children.get(2).assemble(info, res);
        BasicBlock fb = CFG_BUILDER.createBB();
        LOrExprNode cond = info.cond;
        CFG_BUILDER.insert(new FalseJump(fb, res.res));
        BasicBlock body = CFG_BUILDER.createBB();
        CFG_BUILDER.switchBlock(body);
        info.breakBlock = fb;
        info.inLoop = true;
        info.cond = (LOrExprNode) children.get(2);
        // BLOCK
        children.get(4).assemble(info, res);
        info.breakBlock = b;
        info.inLoop = loop;
        info.cond = cond;
        CFG_BUILDER.switchBlock(fb);
    }

    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        info.loop++;
        for (Node node : children) {
            node.check(info, res);
        }
        info.loop--;
    }
}
