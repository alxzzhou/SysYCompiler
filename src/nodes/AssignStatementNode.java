package nodes;

import cfg.quad.Assign;
import cfg.quad.FetchInt;
import cfg.quad.GetInt;
import cfg.quad.SW;
import statics.assembly.AssemblyInfo;
import statics.assembly.AssemblyRes;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;

import java.io.IOException;

import static cfg.CFGBuilder.CFG_BUILDER;
import static statics.setup.SyntaxType.EXPR;
import static statics.setup.SyntaxType.GETINTTK;

public class AssignStatementNode extends Node {
    public void assemble(AssemblyInfo info, AssemblyRes res) throws IOException {
        info.isLVal = true;
        children.getFirst().assemble(info, res);
        var getInt = false;
        var isArray = res.isArray;
        var lval = res.res;
        var offset = res.param;
        res.param = null;
        String i = null;
        for (int k = 1; k < children.size(); k++) {
            var node = children.get(k);
            node.assemble(info, res);
            if (node.type == EXPR) {
                i = res.res;
            } else if (node.type == GETINTTK) {
                i = CFG_BUILDER.tempVar();
                CFG_BUILDER.insert(new GetInt());
                CFG_BUILDER.insert(new FetchInt(i));
                getInt = true;
            }
        }
        if (isArray) {
            CFG_BUILDER.insert(new SW(i, lval, offset));
        } else {
            CFG_BUILDER.insert(new Assign(lval, i));
        }
    }

    public void check(ExcCheckInfo info, ExcCheckRes res) {
        if (children.getFirst().type == EXPR) {
            info.isLVal = true;
            children.getFirst().check(info, res);
            info.isLVal = false;
        }
        for (int i = 1; i < children.size(); i++) {
            children.get(i).check(info, res);
        }
    }
}
