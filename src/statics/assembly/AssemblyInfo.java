package statics.assembly;

import cfg.BasicBlock;
import nodes.expr.LOrExprNode;

public class AssemblyInfo {
    public String res;
    public LOrExprNode cond;
    public BasicBlock continueBlock, breakBlock, condFinalBlock;
    public boolean isConst = false;
    public boolean isLVal = false;
    public boolean isGlobal = false;
    public boolean needJump = false;
    public boolean inLoop = false;
    public boolean inFuncDef = false;

    public AssemblyInfo() {
    }
}
