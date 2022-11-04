package nodes;

import cfg.BasicBlock;
import statics.assembly.AssemblyInfo;
import statics.assembly.AssemblyRes;
import statics.setup.SyntaxType;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;

import java.io.IOException;

import static cfg.CFGBuilder.CFG_BUILDER;
import static statics.setup.Symbol.SYMBOL;

public class StatementNode extends Node {
    public void assemble(AssemblyInfo info, AssemblyRes res) throws IOException {
        BasicBlock contBlock = info.continueBlock,cbb = CFG_BUILDER.cbb;
        
    }

    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        for (Node node : children) {
            node.check(info, res);
            res.isReturn = node.type == SyntaxType.RETURN_STMT;
        }
    }
}
