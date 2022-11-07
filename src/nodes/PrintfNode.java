package nodes;

import cfg.quad.func.PrintInteger;
import cfg.quad.func.PrintString;
import statics.assembly.AssemblyInfo;
import statics.assembly.AssemblyRes;
import statics.exception.CompException;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cfg.CFGBuilder.CFG_BUILDER;
import static cfg.quad.QuadUtil.genIRStr;
import static statics.exception.CompException.ExcDesc.UNMATCHED_PRINT_PARAMS;
import static statics.exception.Errors.errors;
import static statics.setup.Symbol.SYMBOL;
import static statics.setup.SyntaxType.EXPR;
import static statics.setup.SyntaxType.STRCON;

public class PrintfNode extends Node {
    @Override
    public void assemble(AssemblyInfo info, AssemblyRes res) throws IOException {
        List<String> args = new ArrayList<>();
        String fs = null;
        for (var node : children) {
            if (node.type == EXPR) {
                node.assemble(info, res);
                args.add(res.res);
            } else if (node.type == STRCON) {
                fs = ((TokenNode) node).content;
            }
        }
        assert fs != null;
        char[] fs_array = fs.toCharArray();
        StringBuilder cur = new StringBuilder();
        for (int i = 0, j = 0; i < Objects.requireNonNull(fs).length(); i++) {
            char ch = fs_array[i];
            if (ch == '%' && fs_array[i + 1] == 'd') {
                String l = genIRStr();
                SYMBOL.addConstString(l, cur.toString());
                if (!cur.isEmpty()) {
                    cur = new StringBuilder();
                    CFG_BUILDER.insert(new PrintString(l));
                }
                CFG_BUILDER.insert(new PrintInteger(args.get(j)));
                j++;
                i++;
            } else {
                cur.append(ch);
            }
        }
        String l = genIRStr();
        SYMBOL.addConstString(l, cur.toString());
        if (!cur.isEmpty()) {
            CFG_BUILDER.insert(new PrintString(l));
        }
    }

    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        int cnt = 0, line = 0;
        String formatStr = null;
        for (var node : children) {
            node.check(info, res);
            if (node.type == EXPR) {
                cnt++;
            } else if (node.type == STRCON) {
                formatStr = ((TokenNode) node).content;
                line = node.finishLine;
            }
        }
        if (cnt != 0) {
            errors.add(new CompException(UNMATCHED_PRINT_PARAMS.toCode(), line));
        }
    }
}
