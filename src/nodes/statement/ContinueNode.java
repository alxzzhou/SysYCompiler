package nodes.statement;

import cfg.quad.jump.UnconditionalJump;
import nodes.Node;
import statics.assembly.AssemblyInfo;
import statics.assembly.AssemblyRes;
import statics.exception.CompException;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;

import java.io.IOException;

import static cfg.CFGBuilder.CFG_BUILDER;
import static statics.exception.CompException.ExcDesc.UNEXPECTED_BREAK_CONTINUE;
import static statics.exception.Errors.errors;

public class ContinueNode extends Node {
    @Override
    public void assemble(AssemblyInfo info, AssemblyRes res) throws IOException {
        CFG_BUILDER.insert(new UnconditionalJump(info.continueBlock));
    }

    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        for (Node node : children) {
            node.check(info, res);
        }
        if (info.loop == 0) {
            errors.add(new CompException(UNEXPECTED_BREAK_CONTINUE.toCode()
                    , children.getLast().startLine));
        }
    }
}
