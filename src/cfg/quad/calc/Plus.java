package cfg.quad.calc;

import cfg.Function;
import cfg.quad.QuadUtil;
import cfg.quad.Quadruple;
import statics.assembly.AssemblyType;
import statics.io.OutputHandler;

import java.io.IOException;

import static cfg.quad.QuadUtil.isNumberFormat;
import static cfg.quad.QuadUtil.isReg;

public class Plus extends Quadruple {
    String target, v1, v2;

    public Plus(String target, String v1, String v2) {
        super(AssemblyType.PLUS_BIN);
        this.target = target;
        this.v1 = v1;
        this.v2 = v2;
    }

    @Override
    public String getDefine() {
        return target;
    }

    public void setDefine(String s) {
        target = s;
    }

    @Override
    public void assemble(Function f) throws IOException {
        if (isNumberFormat(v1)) {
            QuadUtil.li("$27", v1);
            v1 = "$27";
        } else if (!isReg(v1)) {
            OutputHandler.getInstance()
                    .writeln("lw $27, " + v1.substring(2) + "($sp)");
            v1 = "$27";
        }
        if (v2.charAt(0) == 's') {
            OutputHandler.getInstance()
                    .writeln("lw $28, " + v2 + "($sp)");
        }
        if (isReg(target)) {
            OutputHandler.getInstance()
                    .writeln("addu " + target + ", " + v1 + ", " + v2);
        } else {
            OutputHandler.getInstance()
                    .writeln("addu $27, " + v1 + ", " + v2);
            OutputHandler.getInstance()
                    .writeln("sw $27, " + target.substring(2) + "($sp)");
        }
    }
}
