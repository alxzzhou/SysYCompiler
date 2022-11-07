package cfg.quad.func;

import cfg.Function;
import cfg.quad.Quadruple;
import statics.assembly.AssemblyType;
import statics.io.OutputHandler;

import java.io.IOException;

import static cfg.quad.QuadUtil.isReg;

public class GetReturn extends Quadruple {
    String target;

    public GetReturn(String t) {
        super(AssemblyType.GET_RETURN);
        this.target = t;
    }

    @Override
    public void assemble(Function f) throws IOException {
        if (isReg(target)) {
            OutputHandler.getInstance().writeln("move " + target + ", $v0");
        } else {
            OutputHandler.getInstance().writeln("sw $v0, " + target.substring(2) + "($sp)");
        }
    }

    @Override
    public String getDefine() {
        return target;
    }

    public void setDefine(String d) {
        target = d;
    }

}
