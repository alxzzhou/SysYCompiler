package nodes;

import cfg.quad.calc.Negate;
import cfg.quad.calc.Not;
import statics.assembly.AssemblyInfo;
import statics.assembly.AssemblyRes;
import statics.setup.SyntaxType;

import java.io.IOException;

import static cfg.CFGBuilder.CFG_BUILDER;

public class UnaryPrefixOpNode extends Node {
    @Override
    public void assemble(AssemblyInfo info, AssemblyRes res) throws IOException {
        if (info.isConst) {
            var val = Integer.parseInt(info.res);
            for (var node : children) {
                if (node.type == SyntaxType.NOT) {
                    val = val == 0 ? 1 : 0;
                }
                if (node.type == SyntaxType.MINU) {
                    val = -val;
                }
            }
            info.res = String.valueOf(val);
            return;
        }
        String t;
        for (var node : children) {
            t = CFG_BUILDER.tempVar();
            if (node.type == SyntaxType.NOT) {
                CFG_BUILDER.insert(new Not(t, info.res));
                info.res = t;
            }
            if (node.type == SyntaxType.MINU) {
                CFG_BUILDER.insert(new Negate(t, info.res));
            }
        }
    }
}
