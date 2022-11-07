package nodes.expr;

import cfg.quad.calc.Divide;
import cfg.quad.calc.Minus;
import cfg.quad.calc.Mod;
import cfg.quad.calc.Multiply;
import cfg.quad.calc.Plus;
import cfg.quad.calc.Singleton;
import nodes.Node;
import statics.assembly.AssemblyInfo;
import statics.assembly.AssemblyRes;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;

import java.io.IOException;

import static cfg.CFGBuilder.CFG_BUILDER;
import static cfg.quad.QuadUtil.isNumberFormat;
import static statics.setup.SyntaxType.DIV;
import static statics.setup.SyntaxType.MINU;
import static statics.setup.SyntaxType.MOD;
import static statics.setup.SyntaxType.MULT;
import static statics.setup.SyntaxType.PLUS;

public class ExprNode extends Node {
    String res;

    public void assemble(AssemblyInfo info, AssemblyRes res) throws IOException {
        children.getFirst().assemble(info, res);
        this.res = res.res;
    }

    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        for (Node node : children) {
            ExcCheckRes r = new ExcCheckRes();
            node.check(info, r);
            if (r.isConst) {
                res.isConst = true;
            }
            res.dim = r.dim;
        }
    }
}
