package statics.exception;

public class ExcCheckInfo {
    public boolean inVoid = false;
    public boolean afterFuncDef = false;
    public boolean isLVal = false;
    public boolean isConst = false;
    public boolean isGlobal = false;
    public int loop = 0;

    public ExcCheckInfo() {
    }
}
