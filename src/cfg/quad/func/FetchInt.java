package cfg.quad.func;

import cfg.Function;
import cfg.quad.Quadruple;
import statics.assembly.AssemblyType;
import statics.io.OutputHandler;

import java.io.IOException;

import static cfg.quad.QuadUtil.isReg;
import static cfg.quad.QuadUtil.pureHex;

public class FetchInt extends Quadruple {
    String target;

    public FetchInt(String target) {
        super(AssemblyType.FETCH_INT);
        this.target = target;
    }

    public String getDefine() {
        return target;
    }

    @Override
    public void setDefine(String d) {
        target = d;
    }

    public void assemble(Function f) throws IOException {
        if (isReg(target)) {
            OutputHandler.getInstance().writeln("move " + target + ", $v0");
        } else {
            OutputHandler.getInstance().writeln("sw $v0, " + pureHex(target) + "($sp)");
        }
    }
}
