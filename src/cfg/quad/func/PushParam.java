package cfg.quad.func;

import cfg.Function;
import cfg.quad.Quadruple;
import statics.assembly.AssemblyType;
import statics.io.OutputHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static cfg.quad.QuadUtil.isReg;

public class PushParam extends Quadruple {
    String param;
    int n;

    @Override
    public void print() throws IOException {
        OutputHandler.getInstance().writeln("PUSH_PARAM " + param + " -> PARAM_" + n);
    }

    public PushParam(String param, int n) {
        super(AssemblyType.PUSH_PARAM);
        this.param = param;
        this.n = n;
    }

    @Override
    public Set<String> getUse() {
        return new HashSet<String>() {{
            add(param);
        }};
    }

    @Override
    public void replaceUse(String o, String t) {
        if (o.equals(param)) {
            param = t;
        }
    }

    @Override
    public void assemble(Function f) throws IOException {
        if (n < 4) {
            if (isReg(param)) {
                OutputHandler.getInstance().writeln("move $a" + n + ", " + param);
            } else if (param.charAt(0) == 's') {
                OutputHandler.getInstance().writeln("lw $a" + n + ", " + param.substring(2) + "($sp)");
            } else if (param.charAt(0) == 'a') {
                OutputHandler.getInstance().writeln("addi $a" + n + ", $sp, " + param.substring(5));
            } else {
                OutputHandler.getInstance().writeln("li $a" + n + ", " + param);
            }
        } else {
            if (isReg(param)) {
                OutputHandler.getInstance().writeln("sw " + param + ", " + (-(n - 3) * 4) + "($sp)");
            } else if (param.charAt(0) == 's') {
                OutputHandler.getInstance().writeln("lw $27, " + param.substring(2) + "($sp)");
            } else if (param.charAt(0) == 'a') {
                OutputHandler.getInstance().writeln("addi $27, $sp, " + param.substring(5));
            } else {
                OutputHandler.getInstance().writeln("li $27, " + param);
            }
            if (!isReg(param)) {
                OutputHandler.getInstance().writeln("sw $27, " + (-(n - 3) * 4) + "($sp)");
            }
        }
    }
}
