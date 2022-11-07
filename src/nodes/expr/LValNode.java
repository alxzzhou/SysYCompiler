package nodes.expr;

import cfg.quad.mem.LP;
import cfg.quad.mem.LW;
import nodes.Node;
import nodes.statement.TokenNode;
import statics.assembly.AssemblyInfo;
import statics.assembly.AssemblyRes;
import statics.exception.CompException;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;
import statics.setup.Symbol;
import statics.setup.SyntaxType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static cfg.CFGBuilder.CFG_BUILDER;
import static cfg.quad.QuadUtil.genIRVar;
import static cfg.quad.QuadUtil.isNumberFormat;
import static statics.exception.CompException.ExcDesc.MODIFY_CONST;
import static statics.exception.CompException.ExcDesc.UNDEFINED_IDENT;
import static statics.exception.Errors.errors;
import static statics.setup.Symbol.SYMBOL;
import static statics.setup.SyntaxType.EXPR;

public class LValNode extends Node {
    String r;

    @Override
    public void assemble(AssemblyInfo info, AssemblyRes res) throws IOException {
        Symbol.LValSymbol lvs = null;
        String name = null;
        List<String> indices = new ArrayList<>();
        boolean lval = info.isLVal;
        info.isLVal = false;
        for (Node node : children) {
            if (node.type == SyntaxType.IDENFR) {
                name = ((TokenNode) node).content;
                lvs = SYMBOL.getVar(name);
            }
            node.assemble(info, res);
            if (node.type == EXPR) {
                indices.add(res.res);
            }
        }
        boolean fl = true;
        for (String i : indices) {
            if (!isNumberFormat(i)) {
                fl = false;
            }
        }
        assert lvs != null;
        if ((lvs.isConst && fl) || info.isGlobal) {
            List<Integer> dims = new ArrayList<>();
            for (String i : indices) {
                dims.add(Integer.valueOf(i));
            }
            res.res = r = String.valueOf(lvs.getVal(dims));
            return;
        }
        String ofs = null;
        if (!indices.isEmpty()) {
            ofs = lvs.getOffsetInMIPS(indices);
        } else if (lvs.isGlobal) {
            ofs = "0";
        }
        if (lval) {
            res.res = r = genIRVar(name, lvs.id);
            res.param = ofs;
            res.isArray = ofs != null;
            return;
        }
        if (indices.isEmpty() && !lvs.isGlobal) {
            res.res = r = genIRVar(name, lvs.id);
        } else {
            res.res = r = CFG_BUILDER.tempVar();
            String vn = genIRVar(name, lvs.id);
            if (indices.size() == lvs.dims.size()) {
                CFG_BUILDER.insert(new LW(r, vn, ofs));
            } else {
                CFG_BUILDER.insert(new LP(r, vn, ofs));
            }
        }
    }

    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        Symbol.LValSymbol lvs = null;
        String name = null;
        int cnt = 0;
        for (Node node : children) {
            if (node.type == SyntaxType.IDENFR) {
                name = ((TokenNode) node).content;
                lvs = SYMBOL.getVar(name);
                if (lvs == null) {
                    errors.add(new CompException(UNDEFINED_IDENT.toCode(), node.finishLine));
                } else if (info.isLVal && lvs.isConst) {
                    errors.add(new CompException(MODIFY_CONST.toCode(), node.finishLine));
                }
                info.isLVal = false;
                ExcCheckRes r = new ExcCheckRes();
                node.check(info, r);
                if (node.type == EXPR) {
                    cnt++;
                }
            }
        }
        if (lvs != null) {
            res.dim = lvs.dims.size() - cnt;
        }
    }
}
