package nodes;

import cfg.BasicBlock;
import cfg.quad.jump.TrueJump;
import cfg.quad.jump.UnconditionalJump;
import statics.assembly.AssemblyInfo;
import statics.assembly.AssemblyRes;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;
import statics.setup.SyntaxType;

import java.io.IOException;

import static cfg.CFGBuilder.CFG_BUILDER;

public class StatementNode extends Node {
    public void assemble(AssemblyInfo info, AssemblyRes res) throws IOException {
        BasicBlock contBlock = info.continueBlock, cbb = CFG_BUILDER.cbb;
        BasicBlock cur = CFG_BUILDER.cbb;
        boolean inLoop = info.inLoop;
        if (inLoop) {
            info.continueBlock = CFG_BUILDER.createBB();
        }
        boolean jelse = info.needJump;
        info.inLoop = false;
        info.needJump = false;
        for (var node : children) {
            node.assemble(info, res);
        }
        if (inLoop) {
            CFG_BUILDER.switchBlock(info.continueBlock);
            info.cond.assemble(info, res);
            CFG_BUILDER.insert(new TrueJump(cur, res.res));
        } else if (jelse) {
            CFG_BUILDER.insert(new UnconditionalJump(info.condFinalBlock));
        }
        info.continueBlock = contBlock;
    }

    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        for (Node node : children) {
            node.check(info, res);
            res.isReturn = node.type == SyntaxType.RETURN_STMT;
        }
    }
}
