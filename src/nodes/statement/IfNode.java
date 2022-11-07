package nodes.statement;

import cfg.BasicBlock;
import cfg.quad.jump.FalseJump;
import nodes.Node;
import statics.assembly.AssemblyInfo;
import statics.assembly.AssemblyRes;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;

import java.io.IOException;

import static cfg.CFGBuilder.CFG_BUILDER;
import static statics.setup.SyntaxType.ELSETK;

public class IfNode extends Node {
    public void assemble(AssemblyInfo info, AssemblyRes res) throws IOException {
        // STORE THE SCENE
        BasicBlock temp_b = info.condFinalBlock;
        boolean jelse = info.needJump;
        children.get(2).assemble(info, res);
        boolean else_ = false;
        for (Node node : children) {
            if (node.type == ELSETK) {
                else_ = true;
                break;
            }
        }
        if (else_) {
            BasicBlock elseBlock = CFG_BUILDER.createBB();
            CFG_BUILDER.insert(new FalseJump(elseBlock, res.res));
            CFG_BUILDER.switchBlock(CFG_BUILDER.createBB());
            info.needJump = true;
            BasicBlock fcfg = CFG_BUILDER.createBB();
            info.condFinalBlock = fcfg;
            children.get(4).assemble(info, res);
            CFG_BUILDER.switchBlock(elseBlock);
            info.needJump = false;
            children.get(6).assemble(info, res);
            CFG_BUILDER.switchBlock(fcfg);
        } else {
            BasicBlock b = CFG_BUILDER.createBB();
            CFG_BUILDER.insert(new FalseJump(b, res.res));
            CFG_BUILDER.switchBlock(CFG_BUILDER.createBB());
            info.needJump = false;
            children.get(4).assemble(info, res);
            CFG_BUILDER.switchBlock(b);
        }
        info.condFinalBlock = temp_b;
        info.needJump = jelse;
    }

    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {

    }
}
