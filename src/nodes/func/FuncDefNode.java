package nodes.func;

import cfg.Function;
import cfg.quad.func.LoadFuncParam;
import cfg.quad.func.Return;
import nodes.Node;
import nodes.statement.TokenNode;
import statics.assembly.AssemblyInfo;
import statics.assembly.AssemblyRes;
import statics.exception.CompException;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static cfg.CFGBuilder.CFG_BUILDER;
import static cfg.quad.QuadUtil.genIRFunc;
import static statics.exception.CompException.ExcDesc.LACK_RETURN;
import static statics.exception.CompException.ExcDesc.MULTIPLE_DEFINITION;
import static statics.exception.Errors.errors;
import static statics.setup.Symbol.SYMBOL;
import static statics.setup.SyntaxType.BLOCK;
import static statics.setup.SyntaxType.FUNC_PARAMS;
import static statics.setup.SyntaxType.FUNC_TYPE;
import static statics.setup.SyntaxType.IDENFR;
import static statics.setup.SyntaxType.LPARENT;
import static statics.setup.SyntaxType.MAINTK;

public class FuncDefNode extends Node {
    public void assemble(AssemblyInfo info, AssemblyRes res) throws IOException {
        String name = null;
        List<String> params = new ArrayList<>();
        for (Node node : children) {
            if (node.type == MAINTK || node.type == IDENFR) {
                name = ((TokenNode) node).content;
            } else if (node.type == LPARENT) {
                SYMBOL.startBlock();
            } else if (node.type == BLOCK) {
                SYMBOL.addFunc(new ArrayList<>(),
                        name, false);
                String fn = genIRFunc(name);
                CFG_BUILDER.switchFunc(new Function(fn, params.size()));
                CFG_BUILDER.switchBlock(CFG_BUILDER.createBB());
                for (int i = 0; i < params.size(); i++) {
                    CFG_BUILDER.insert(new LoadFuncParam(params.get(i), i));
                }
                info.inFuncDef = true;
            }
            node.assemble(info, res);
            if (node.type == BLOCK) {
                CFG_BUILDER.insert(new Return(""));
            } else if (node.type == FUNC_PARAMS) {
                params = res.args;
            }
        }
    }

    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        String name = null;
        boolean inVoid = true;
        int line = 0;
        List<Integer> params = new ArrayList<>();
        for (Node node : children) {
            if (node.type == IDENFR || node.type == MAINTK) {
                name = ((TokenNode) node).content;
                line = node.finishLine;
            } else if (node.type == LPARENT) {
                SYMBOL.startBlock();
            } else if (node.type == BLOCK) {
                if (!SYMBOL.addFunc(params, name, inVoid)) {
                    errors.add(new CompException(MULTIPLE_DEFINITION.toCode(), line));
                }
                info.inVoid = inVoid;
                info.afterFuncDef = true;
            }
            ExcCheckRes r = new ExcCheckRes();
            node.check(info, r);
            if (node.type == FUNC_TYPE) {
                inVoid = r.isVoid;
            } else if (node.type == FUNC_PARAMS) {
                params = r.args;
            } else if (node.type == BLOCK) {
                res.isReturn = r.isReturn;
            }
        }
        if (!res.isReturn && !inVoid) {
            errors.add(new CompException(LACK_RETURN.toCode(), finishLine));
        }
        info.inVoid = false;
    }
}
