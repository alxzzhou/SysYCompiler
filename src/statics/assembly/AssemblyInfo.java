package statics.assembly;

import cfg.BasicBlock;
import nodes.ExprNode;

public class AssemblyInfo {
    public String res;
    public ExprNode cond;
    public BasicBlock continueBlock, breakBlock, elseBlock;
    public boolean isConst = false;
    public boolean isLVal = false;
    public boolean isGlobal = false;
    public boolean needJump = false;
    public boolean inLoop = false;
    public boolean inFuncDef = false;

    public AssemblyInfo() {
    }
}
