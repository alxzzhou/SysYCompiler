package cfg.quad.calc;

import cfg.Function;
import cfg.quad.Quadruple;
import statics.assembly.AssemblyType;
import statics.io.OutputHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static cfg.quad.QuadUtil.isNumberFormat;
import static cfg.quad.QuadUtil.isReg;

public class Divide extends Quadruple {
    String target, v1, v2;

    public Divide(String target, String v1, String v2) {
        super(AssemblyType.PLUS_ARITH);
        this.target = target;
        this.v1 = v1;
        this.v2 = v2;
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
    public String getDefine() {
        return target;
    }

    public void setDefine(String s) {
        target = s;
    }

    @Override
    public void assemble(Function f) throws IOException {
        if (isNumberFormat(v1)) {
            OutputHandler.getInstance()
                    .writeln("li $27, " + v1);
            v1 = "$27";
        } else if (!isReg(v1)) {
            OutputHandler.getInstance()
                    .writeln("lw $27, " + v1.substring(2) + "($sp)");
            v1 = "$27";
        }

        if (isNumberFormat(v2)) {
            /*
            var val2 = Long.parseLong(v2);
            var l = (int) Math.max(Math.ceil(Math.log(Math.abs(val2) / Math.log(2))), 1);
            var m = (1 + (1L << (32 + l - 1)) / Math.abs(val2));
            var mm = m - (1L << 32);
            var d = val2 < 0 ? -1 : 0;
            var sh = l - 1;
            OutputHandler.getInstance().writeln("li $28, " + mm);
            OutputHandler.getInstance().writeln("mult $28, " + v1);
            OutputHandler.getInstance().writeln("mfhi $28");
            OutputHandler.getInstance().writeln("addu $28, " + v1 + ", $28");
            OutputHandler.getInstance().writeln("sra $28, $28, " + sh);
            OutputHandler.getInstance().writeln("slt $27, " + v1 + ", $0");
            OutputHandler.getInstance().writeln("addu $28, $28, $27");
            OutputHandler.getInstance().writeln("xori $28, $28, " + d);
            if (isReg(target)) {
                OutputHandler.getInstance().writeln("subiu " + target + ", $28, " + d);
            } else {
                OutputHandler.getInstance().writeln("subiu $28, $28, " + d);
                OutputHandler.getInstance().writeln("sw $28, " + target.substring(2) + "($sp)");
            }
            return;
            */
            OutputHandler.getInstance().writeln("li $28, " + v2);
            v2 = "$28";
        } else if (!isReg(v2)) {
            OutputHandler.getInstance().writeln("lw $28, " + v2.substring(2) + "($sp)");
            v2 = "$28";
        }

        OutputHandler.getInstance().writeln("div " + v1 + ", " + v2);
        if (isReg(target)) {
            OutputHandler.getInstance()
                    .writeln("mflo " + target);
        } else {
            OutputHandler.getInstance()
                    .writeln("mflo $28");
            OutputHandler.getInstance()
                    .writeln("sw $28, " + target.substring(2) + "($sp)");
        }
    }
}
