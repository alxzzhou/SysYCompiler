package cfg.quad.logic;

import cfg.Function;
import cfg.quad.Quadruple;
import statics.io.OutputHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static cfg.quad.QuadUtil.isNumberFormat;
import static cfg.quad.QuadUtil.isReg;
import static statics.assembly.AssemblyType.AND_LOGIC;

public class AND extends Quadruple {
    String target, v1, v2;

    public AND(String target, String v1, String v2) {
        super(AND_LOGIC);
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
    public Set<String> getUse() {
        HashSet<String> r = new HashSet<>();
        if (!isNumberFormat(v1)) {
            r.add(v1);
        }
        if (!isNumberFormat(v2)) {
            r.add(v2);
        }
        return r;
    }

    @Override
    public void replaceUse(String o, String t) {
        if (o.equals(v1)) {
            v1 = t;
        }
        if (o.equals(v2)) {
            v2 = t;
        }
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
