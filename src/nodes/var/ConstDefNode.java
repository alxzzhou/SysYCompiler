package nodes.var;

import cfg.quad.func.Assign;
import cfg.quad.mem.NP;
import nodes.Node;
import nodes.statement.TokenNode;
import statics.assembly.AssemblyInfo;
import statics.assembly.AssemblyRes;
import statics.exception.CompException;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;
import statics.setup.SyntaxType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static cfg.CFGBuilder.CFG_BUILDER;
import static cfg.quad.QuadUtil.genIRVar;
import static statics.exception.CompException.ExcDesc.MULTIPLE_DEFINITION;
import static statics.exception.Errors.errors;
import static statics.setup.Symbol.SYMBOL;
import static statics.setup.SyntaxType.CONST_INIT_VAL;
import static statics.setup.SyntaxType.EXPR;

public class ConstDefNode extends Node {
    @Override
    public void assemble(AssemblyInfo info, AssemblyRes res) throws IOException {
        boolean con = info.isConst;
        List<Integer> dims = new ArrayList<>();
        List<Integer> initVal = new ArrayList<>();
        List<String> init = new ArrayList<>();
        String name = null;
        boolean f = con || SYMBOL.getStackSize() == 1;
        res.clear();
        for (Node node : children) {
            if (node.type == SyntaxType.IDENFR) {
                name = ((TokenNode) node).content;
            }
            node.assemble(info, res);
            if (node.type == EXPR) {
                dims.add(Integer.parseInt(res.res));
            } else if (node.type == CONST_INIT_VAL) {
                if (f) {
                    res.init.forEach(v -> initVal.add(Integer.valueOf(v)));
                } else {
                    init = res.init;
                }
            }
        }
        int id = SYMBOL.addVar(con, name, dims, initVal);
        if (f) {
            return;
        }
        if (!dims.isEmpty()) {
            String p = genIRVar(name, id);
            int s = 1;
            for (int i : dims) {
                s *= i;
            }
            CFG_BUILDER.insert(new NP(p, s));
        } else {
            String v = genIRVar(name, id);
            CFG_BUILDER.insert(new Assign(v, init.isEmpty() ? "0" : init.get(0)));
        }
    }

    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        String name = null;
        int line = 0;
        List<Integer> lim = new ArrayList<>();
        for (Node node : children) {
            if (node.type == SyntaxType.IDENFR) {
                name = ((TokenNode) node).content;
                line = node.finishLine;
            }
            ExcCheckRes r = new ExcCheckRes();
            node.check(info, r);
            if (node.type == SyntaxType.EXPR) {
                lim.add(r.val);
            }
        }
        if (SYMBOL.addVar(info.isConst, name, lim, new ArrayList<>()) == 0) {
            errors.add(new CompException(MULTIPLE_DEFINITION.toCode(), line));
        }
    }
}
