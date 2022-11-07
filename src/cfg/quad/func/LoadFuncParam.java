package cfg.quad.func;

import cfg.Function;
import cfg.quad.Quadruple;
import statics.assembly.AssemblyType;
import statics.io.OutputHandler;

import java.io.IOException;

import static cfg.quad.QuadUtil.isReg;

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

    @Override
    public void assemble(Function f) throws IOException {
        if (n < 4) {
            if (isReg(s)) {
                OutputHandler.getInstance().writeln("move " + s + ", $a" + n);
            } else {
                OutputHandler.getInstance()
                        .writeln("sw $a" + n + ", " + s.substring(2) + "($sp)");
            }
        } else {
            if (isReg(s)) {
                OutputHandler.getInstance()
                        .writeln("lw " + s + ", " + (f.addr - (n - 3) * 4) + "($sp)");
            } else {
                OutputHandler.getInstance()
                        .writeln("lw $27, " + (f.addr - 4 * (n - 3)) + "($sp)");
                OutputHandler.getInstance().writeln("sw $27, " + s.substring(2) + "($sp)");
            }
        }
    }
}
