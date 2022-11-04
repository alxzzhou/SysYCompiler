package nodes;

import statics.assembly.AssemblyInfo;
import statics.assembly.AssemblyRes;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;
import statics.setup.SyntaxType;

import java.io.IOException;

import static statics.setup.Symbol.SYMBOL;

public class BlockNode extends Node {
    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        if (info.afterFuncDef) {
            info.afterFuncDef = false;
        } else {
            SYMBOL.startBlock();
        }
        boolean f = false;
        for (Node node : children) {
            if (node.type == SyntaxType.STMT) {
                f = true;
            }
            node.check(info, res);
        }
        if (!f) { // No statement
            res.isReturn = false;
        }
        SYMBOL.finishBlock();
    }

    public void assemble(AssemblyInfo info, AssemblyRes res) throws IOException {
        if (info.inFuncDef) {
            info.inFuncDef = false;
        } else {
            SYMBOL.startBlock();
        }
        for (var node : children) {
            node.assemble(info, res);
        }
        SYMBOL.finishBlock();
    }
}
