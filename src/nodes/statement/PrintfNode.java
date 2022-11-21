package nodes.statement;

import cfg.quad.func.PrintInteger;
import cfg.quad.func.PrintString;
import nodes.Node;
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
import static statics.exception.CompException.ExcDesc.INVALID_CHAR;
import static statics.exception.CompException.ExcDesc.UNMATCHED_PRINT_PARAMS;
import static statics.exception.Errors.errors;
import static statics.setup.Symbol.SYMBOL;
import static statics.setup.SyntaxType.EXPR;
import static statics.setup.SyntaxType.STRCON;

public class PrintfNode extends Node {
    int cnt = 0;

    @Override
    public void assemble(AssemblyInfo info, AssemblyRes res) throws IOException {
        List<String> args = new ArrayList<>();
        String fs = null;
        for (Node node : children) {
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
        for (int i = 1, j = 0; i < Objects.requireNonNull(fs).length() - 1; i++) {
            char ch = fs_array[i];
            if (ch == '%' && fs_array[i + 1] == 'd') {
                String l = genIRStr();
                SYMBOL.addConstString(l, cur.toString());
                if (cur.length() > 0) {
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
        if (cur.length() > 0) {
            CFG_BUILDER.insert(new PrintString(l));
        }
    }

    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        int line = 0;
        cnt = 0;
        String formatStr = null;
        for (Node node : children) {
            node.check(info, res);
            if (node.type == EXPR) {
                cnt++;
            } else if (node.type == STRCON) {
                formatStr = ((TokenNode) node).content;
                line = node.finishLine;
            }
        }

        assert formatStr != null;
        if (fsError(formatStr)) {
            errors.add(new CompException(INVALID_CHAR.toCode(), line));
        }
        if (cnt != 0) {
            errors.add(new CompException(UNMATCHED_PRINT_PARAMS.toCode(), this.startLine));
        }
    }

    private boolean fsError(String fs) {
        int len = fs.length();
        boolean ret = false;
        if (fs.charAt(len - 2) == '\\') {
            return true;
        }
        for (int i = 1; i < len - 1; i++) {
            char c = fs.charAt(i), l = fs.charAt(i - 1);
            if (c != 32 && c != 33 && c != '%' && (c < 40 || c > 126)) {
                ret = true;
            } else if (l == '\\' && c != 'n') {
                ret = true;
            } else if (l == '%' && c != 'd') {
                ret = true;
            }
            if (l == '%' && c == 'd') {
                cnt--;
            }
        }
        return ret;
    }
}
