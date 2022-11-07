package cfg.quad.mem;

import cfg.quad.Quadruple;
import statics.assembly.AssemblyType;

public class NP extends Quadruple {
    public int s;
    String name;

    public NP(String name, int s) {
        super(AssemblyType.CREATE_POINTER);
        this.name = name;
        this.s = s;
    }

    @Override
    public String getDefine() {
        return name;
    }

    public void setDefine(String d) {
        name = d;
    }
}
