package cfg.quad.logic;

import cfg.Function;
import cfg.quad.Quadruple;
import statics.io.OutputHandler;

import java.io.IOException;

import static cfg.quad.QuadUtil.isNumberFormat;
import static cfg.quad.QuadUtil.isReg;
import static statics.assembly.AssemblyType.OR_BIN;

public class AND extends Quadruple {
    String target, v1, v2;

    public AND(String target, String v1, String v2) {
        super(OR_BIN);
        this.target = target;
        this.v1 = v1;
        this.v2 = v2;
    }

    @Override
    public void assemble(Function f) throws IOException {
        if (isNumberFormat(v1)) {
            OutputHandler.getInstance().writeln("li $27, " + v1);
            v1 = "$27";
        } else if (!isReg(v1)) {
            OutputHandler.getInstance().writeln("lw $27, " + v1.substring(2) + "($sp)");
            v1 = "$27";
        }
        if (v2.charAt(0) == 's') {
            OutputHandler.getInstance().writeln("lw $28, " + v2.substring(2) + "($sp)");
            v2 = "$28";
        }
        String op = isReg(v2) ? "and " : "andi ";
        if (isReg(target)) {
            OutputHandler.getInstance().writeln(op + target + ", " + v1 + ", " + v2);
        } else {
            OutputHandler.getInstance().writeln(op + "$27, " + v1 + ", " + v2);
            OutputHandler.getInstance().writeln("sw $27, " + target.substring(2) + "($sp)");
        }
    }
}
