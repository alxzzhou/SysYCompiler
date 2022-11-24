package cfg.quad.calc;

import cfg.Function;
import cfg.quad.Quadruple;
import statics.assembly.AssemblyType;
import statics.io.OutputHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static cfg.quad.QuadUtil.isNumberFormat;
import static cfg.quad.QuadUtil.isPowerOf2;
import static cfg.quad.QuadUtil.isReg;

public class Multiply extends Quadruple {
    String target, v1, v2;

    public Multiply(String target, String v1, String v2) {
        super(AssemblyType.PLUS_ARITH);
        this.target = target;
        this.v1 = v1;
        this.v2 = v2;
    }

    @Override
    public void print() throws IOException {
        OutputHandler.getInstance().writeln(target + " = " + v1 + " * " + v2);
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
        boolean fl = false;
        if (isNumberFormat(v1)) {
            fl = true;
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
            // MULTIPLY OPTIMIZE
            if (i2 == 0) {
                if (isReg(target)) {
                    OutputHandler.getInstance().writeln("li " + target + ", 0");
                } else {
                    OutputHandler.getInstance().writeln("li $28, 0");
                    OutputHandler.getInstance()
                            .writeln("sw $28, " + target.substring(2) + "($sp)");
                }
                return;
            }
            if (fl) {
                int i1 = Integer.parseInt(v1);
                if (isReg(target)) {
                    OutputHandler.getInstance().writeln("li " + target + ", " + (i1 * i2));
                } else {
                    OutputHandler.getInstance().writeln("li $28, " + (i1 * i2));
                    OutputHandler.getInstance()
                            .writeln("sw $28, " + target.substring(2) + "($sp)");
                }
                return;
            } else if (isPowerOf2(i2)) {
                i2 = (int) (Math.log(i2) / Math.log(2));
                if (isReg(target)) {
                    OutputHandler.getInstance().writeln("sll " + target + ", " + v1 + ", " + i2);
                } else {
                    OutputHandler.getInstance().writeln("sll $28, " + v1 + ", " + i2);
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

        if (isReg(target)) {
            OutputHandler.getInstance()
                    .writeln("mul " + target + ", " + v1 + ", " + v2);
        } else {
            OutputHandler.getInstance()
                    .writeln("mul $27, " + v1 + ", " + v2);
            OutputHandler.getInstance()
                    .writeln("sw $27, " + target.substring(2) + "($sp)");
        }
    }
}
