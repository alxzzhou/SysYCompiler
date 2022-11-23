package nodes.expr;

import nodes.Node;
import nodes.statement.TokenNode;
import statics.assembly.AssemblyInfo;
import statics.assembly.AssemblyRes;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;
import statics.setup.SyntaxType;

public class NumberNode extends Node {
    String r;

    @Override
    public void assemble(AssemblyInfo info, AssemblyRes res) {
        r = ((TokenNode) children.getFirst()).content;
        res.res = r;
    }

    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        if (children.getLast().type == SyntaxType.INTCON) {
            String content = ((TokenNode) children.getLast()).content;
            res.val = Integer.parseInt(content);
            res.isConst = true;
        }
    }
}
