package nodes;

import cfg.quad.func.Call;
import cfg.quad.func.GetReturn;
import cfg.quad.func.PushParam;
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
import static cfg.quad.QuadUtil.genIRFunc;
import static statics.exception.CompException.ExcDesc.CORRECT;
import static statics.exception.CompException.ExcDesc.UNDEFINED;
import static statics.exception.Errors.errors;
import static statics.setup.Symbol.SYMBOL;

public class FuncExprNode extends Node {
    String r;

    @Override
    public void assemble(AssemblyInfo info, AssemblyRes res) throws IOException {
        String n = null;
        Symbol.FuncSymbol fs = null;
        List<String> args = new ArrayList<>();
        for (var node : children) {
            if (node.type == SyntaxType.IDENFR) {
                n = ((TokenNode) node).content;
                fs = SYMBOL.getFunc(n);
            }
            node.assemble(info, res);
            if (node.type == SyntaxType.FUNC_ARGS) {
                args = res.args;
            }
        }
        assert fs != null;
        r = fs.isVoid ? "-" : CFG_BUILDER.tempVar();
        String irn = genIRFunc(n);
        for (int i = 0; i < args.size(); i++) {
            CFG_BUILDER.insert(new PushParam(args.get(i), i));
        }
        CFG_BUILDER.insert(new Call(irn));
        if (!fs.isVoid) {
            CFG_BUILDER.insert(new GetReturn(r));
        }
        res.res = r;
    }

    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        Symbol.FuncSymbol fs = null;
        int line = 0;
        boolean fl = false;
        for (var node : children) {
            if (node.type == SyntaxType.IDENFR) {
                var funcName = ((TokenNode) node).content;
                fs = SYMBOL.getFunc(funcName);
                if (fs == null) {
                    errors.add(new CompException(UNDEFINED.toCode(), node.finishLine));
                } else if (fs.isVoid) {
                    res.dim = -1;
                }
            }
            var r = new ExcCheckRes();
            node.check(info, r);
            if (node.type == SyntaxType.IDENFR) {
                line = ((TokenNode) node).finishLine;
            } else if (fs != null &&
                    (node.type == SyntaxType.FUNC_ARGS ||
                            (!fl && node.type == SyntaxType.RPARENT))) {
                fl = true;
                CompException.Exception e = fs.matchParams(r.args);
                if (e != CORRECT.toCode()) {
                    errors.add(new CompException(e, line));
                }
            }
        }
    }
}
