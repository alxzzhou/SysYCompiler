package cfg.quad;

import cfg.Function;
import statics.assembly.AssemblyType;
import statics.io.OutputHandler;

import java.io.IOException;

import static cfg.quad.QuadUtil.isReg;
import static cfg.quad.QuadUtil.pureHex;

public class FetchInt extends Quadruple {
    String target;

    public FetchInt(String target) {
        super(AssemblyType.GETINT);
        this.target = target;
    }

    public void assemble(Function f) throws IOException {
        if (isReg(target)) {
            OutputHandler.getInstance().writeln("move " + target + ", $v0");
        } else {
            OutputHandler.getInstance().writeln("sw $v0, " + pureHex(target) + "($sp)");
        }
    }
}
