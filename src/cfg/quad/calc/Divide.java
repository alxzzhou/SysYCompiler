package cfg.quad.calc;

import cfg.Function;
import cfg.quad.Quadruple;
import statics.assembly.AssemblyType;
import statics.io.OutputHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static cfg.quad.QuadUtil.isNumberFormat;
import static cfg.quad.QuadUtil.isPowerOf2;
import static cfg.quad.QuadUtil.isReg;

public class Divide extends Quadruple {
    String target, v1, v2;

    public Divide(String target, String v1, String v2) {
        super(AssemblyType.DIV_ARITH);
        this.target = target;
        this.v1 = v1;
        this.v2 = v2;
    }

    @Override
    public List<String> getCalcSequence() {
        return new ArrayList<String>() {{
            add(v1);
            add(v2);
        }};
    }

    @Override
    public void print() throws IOException {
        OutputHandler.getInstance().writeln(target + " = " + v1 + " / " + v2);
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
            int i2 = Integer.parseInt(v2);
            // DIVISION OPTIMIZE
            if (i2 == 1) {
                if (isReg(target)) {
                    OutputHandler.getInstance().writeln("move " + target + ", " + v1);
                } else {
                    OutputHandler.getInstance().writeln("move $28, " + v1);
                    OutputHandler.getInstance()
                            .writeln("sw $28, " + target.substring(2) + "($sp)");
                }
                return;
            }
            if (isPowerOf2(i2)) {
                i2 = (int) (Math.log(i2) / Math.log(2));
                if (isReg(target)) {
                    OutputHandler.getInstance().writeln("sra " + target + ", " + v1 + ", " + i2);
                } else {
                    OutputHandler.getInstance().writeln("sra $28, " + v1 + ", " + i2);
                    OutputHandler.getInstance()
                            .writeln("sw $28, " + target.substring(2) + "($sp)");
                }
                return;
            }
            // END
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
