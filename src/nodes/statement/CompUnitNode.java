package nodes.statement;

import cfg.CFGBuilder;
import cfg.Optimize;
import nodes.Node;
import statics.assembly.AssemblyInfo;
import statics.assembly.AssemblyRes;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;
import statics.io.OutputHandler;

import java.io.IOException;

import static cfg.CFGBuilder.CFG_BUILDER;
import static statics.setup.Symbol.SYMBOL;
import static statics.setup.SyntaxType.FUNC_DEF;

public class CompUnitNode extends Node {
    public void assemble(AssemblyInfo info, AssemblyRes res) throws IOException {
        SYMBOL.startBlock();
        info.isGlobal = true;
        for (Node node : children) {
            info.isGlobal = (node.type != FUNC_DEF);
            node.assemble(info, res);
        }
        if (Optimize.OPTIMAL) {
            CFG_BUILDER.updatePredBlocks();
            CFG_BUILDER.updateDefine();
            CFG_BUILDER.updateUse();
            CFG_BUILDER.activityAnalysis();
            CFG_BUILDER.eliminateDeadCode();
            CFG_BUILDER.peepHoleOptimize();
        }
        if (OutputHandler.CONFIG == OutputHandler.IOConfig.IR) {
            OutputHandler.getInstance().writeln("GLOBAL VAR:");
            SYMBOL.printGlobalVars();
            CFG_BUILDER.print();
        } else {
            OutputHandler.getInstance().writeln(".data");
            SYMBOL.printGlobalVars();
            OutputHandler.getInstance().writeln(
                    ".text\njal func_main\nli $v0, 10\nsyscall"
            );
            CFG_BUILDER.assemble();
        }
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
