package nodes.statement;

import cfg.quad.func.Return;
import nodes.Node;
import statics.assembly.AssemblyInfo;
import statics.assembly.AssemblyRes;
import statics.exception.CompException;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;
import statics.setup.SyntaxType;

import java.io.IOException;

import static cfg.CFGBuilder.CFG_BUILDER;
import static statics.exception.CompException.ExcDesc.REDUNDANT_RETURN;
import static statics.exception.Errors.errors;
import static statics.setup.SyntaxType.EXPR;

public class ReturnNode extends Node {
    @Override
    public void assemble(AssemblyInfo info, AssemblyRes res) throws IOException {
        String r = null;
        for (Node node : children) {
            node.assemble(info, res);
            if (node.type == EXPR) {
                r = res.res;
            }
        }
        CFG_BUILDER.insert(new Return(r == null ? "-" : r));
    }

    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        int line = 0;
        for (Node node : children) {
            node.check(info, res);
            if (node.type == SyntaxType.RETURNTK) {
                line = node.startLine;
            } else if (node.type == EXPR && info.inVoid) {
                errors.add(new CompException(REDUNDANT_RETURN.toCode(), line));
            }
        }
    }
}
