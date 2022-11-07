package cfg.quad.logic;

import cfg.Function;
import cfg.quad.Quadruple;
import statics.assembly.AssemblyType;
import statics.io.OutputHandler;

import java.io.IOException;

import static cfg.quad.QuadUtil.isNumberFormat;
import static cfg.quad.QuadUtil.isReg;

public class NEQ extends Quadruple {
    String target, v1, v2;

    public NEQ(String target, String v1, String v2) {
        super(AssemblyType.EQ_BIN);
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
            OutputHandler.getInstance().writeln(";i $27, " + v1);
            v1 = "$27";
        } else if (!isReg(v1)) {
            OutputHandler.getInstance().writeln("lw $27, " + v1.substring(2) + "($sp)");
            v1 = "$27";
        }
        if (v2.charAt(0) == 's') {
            OutputHandler.getInstance().writeln("lw $28, " + v2.substring(2) + "($sp)");
            v2 = "$28";
        }
        if (isReg(target)) {
            OutputHandler.getInstance().writeln("sne " + target + ", " + v1 + ", " + v2);
        } else {
            OutputHandler.getInstance().writeln("sne $27, " + v1 + ", " + v2);
            OutputHandler.getInstance().writeln("sw $27, " + target.substring(2) + "($sp)");
        }
    }
}
