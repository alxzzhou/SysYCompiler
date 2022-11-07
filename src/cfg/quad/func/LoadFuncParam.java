package cfg.quad.func;

import cfg.quad.Quadruple;
import statics.assembly.AssemblyType;

public class LoadFuncParam extends Quadruple {
    String s;
    int n;

    public LoadFuncParam(String s, int n) {
        super(AssemblyType.LOAD_PARAM);
        this.s = s;
        this.n = n;
    }

    public String getDefine() {
        return s;
    }

    public void setDefine(String d) {
        s = d;
    }
}
