package nodes.func;

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

import static cfg.quad.QuadUtil.genIRVar;
import static cfg.quad.QuadUtil.isNumberFormat;
import static statics.exception.CompException.ExcDesc.MULTIPLE_DEFINITION;
import static statics.exception.Errors.errors;
import static statics.setup.Symbol.SYMBOL;

public class FuncParamNode extends Node {
    public void assemble(AssemblyInfo info, AssemblyRes res) throws IOException {
        String name = null;
        List<Integer> dims = new ArrayList<>();
        res.clear();
        int v = 0;
        for (Node node : children) {
            if (node.type == SyntaxType.IDENFR) {
                name = ((TokenNode) node).content;
            }
            node.assemble(info, res);
            if (node.type == SyntaxType.RBRACK) {
                dims.add(v);
            } else if (node.type == SyntaxType.EXPR) {
                v = isNumberFormat(res.res) ? Integer.parseInt(res.res) : 0;
            }
        }
        res.param = genIRVar(name, SYMBOL.addVar(false, name, dims, new ArrayList<>()));
    }

    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        int line = 0;
        String name = null;
        List<Integer> dims = new ArrayList<>();
        boolean f = false;
        for (Node node : children) {
            if (node.type == SyntaxType.IDENFR) {
                name = ((TokenNode) node).content;
                line = node.finishLine;
            }
            ExcCheckRes r = new ExcCheckRes();
            node.check(info, r);
            if (f) {
                f = false;
                dims.add(r.val);
            }
            if (node.type == SyntaxType.LBRACK) {
                f = true;
            }
        }
        res.dim = dims.size();
        if (SYMBOL.addVar(false, name, dims, new ArrayList<>()) == 0) {
            errors.add(new CompException(MULTIPLE_DEFINITION.toCode(), line));
        }
    }
}
