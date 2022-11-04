package nodes;

import statics.assembly.AssemblyInfo;
import statics.assembly.AssemblyRes;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;
import statics.io.OutputHandler;

import java.io.IOException;

import static cfg.CFGBuilder.CFG_BUILDER;
import static cfg.CFGBuilder.OPTIMIZE;
import static statics.setup.Symbol.SYMBOL;
import static statics.setup.SyntaxType.FUNC_DEF;

public class CompUnitNode extends Node {
    public void assemble(AssemblyInfo info, AssemblyRes res) throws IOException {
        SYMBOL.startBlock();
        info.isGlobal = true;
        for (var node : children) {
            info.isGlobal = (node.type != FUNC_DEF);
            node.assemble(info, res);
        }
        if (OPTIMIZE) {
            // TODO: optimize
        }
        CFG_BUILDER.ralloc();
        OutputHandler.getInstance().writeln(".data");
        SYMBOL.printGlobalVars();
        OutputHandler.getInstance().writeln(
                """
                        .text
                        jal func_main
                        li $v0, 10
                        syscall"""
        );
        SYMBOL.finishBlock();
    }

    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        SYMBOL.startBlock();
        for (Node node : children) {
            node.check(info, res);
            info.isGlobal = false;
        }
        SYMBOL.finishBlock();
        SYMBOL.clear();
    }
}
