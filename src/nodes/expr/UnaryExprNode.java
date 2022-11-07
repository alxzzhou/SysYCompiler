package nodes.expr;

import cfg.quad.func.Assign;
import nodes.Node;
import statics.assembly.AssemblyInfo;
import statics.assembly.AssemblyRes;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;
import statics.setup.SyntaxType;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static cfg.CFGBuilder.CFG_BUILDER;
import static cfg.quad.QuadUtil.isNumberFormat;
import static statics.setup.SyntaxType.FUNC_EXPR;
import static statics.setup.SyntaxType.PREFIX_OP;
import static statics.setup.SyntaxType.PRIMARY_EXPR;

public class UnaryExprNode extends Node {
    static final Set<SyntaxType> EXPR = new HashSet<SyntaxType>() {{
        add(FUNC_EXPR);
        add(PRIMARY_EXPR);
    }};
    String r;

    @Override
    public void assemble(AssemblyInfo info, AssemblyRes res) throws IOException {
        if (info.isLVal) {
            children.getFirst().assemble(info, res);
            this.r = res.res;
            return;
        }
        for (Node node : children) {
            if (EXPR.contains(node.type)) {
                node.assemble(info, res);
                info.res = res.res;
            }
        }
        info.isConst = isNumberFormat(res.res);
        if (info.isConst || children.getFirst().type != PREFIX_OP) {
            r = res.res;
        } else {
            r = CFG_BUILDER.tempVar();
            CFG_BUILDER.insert(new Assign(r, res.res));
        }
        if (children.getFirst().type == PREFIX_OP) {
            info.res = r;
            children.getFirst().assemble(info, res);
            r = info.res;
        }
        info.isConst = false;
        res.res = r;
    }

    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        for (Node node : children) {
            node.check(info, res);
        }
    }
}
