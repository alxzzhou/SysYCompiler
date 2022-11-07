package cfg.quad.calc;

import cfg.Function;
import cfg.quad.Quadruple;
import statics.assembly.AssemblyType;
import statics.io.OutputHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static cfg.quad.QuadUtil.isReg;

public class Negate extends Quadruple {
    String target, v;

    public Negate(String t, String v) {
        super(AssemblyType.NEGATE_UNI);
        this.target = t;
        this.v = v;
    }

    @Override
    public void assemble(Function f) throws IOException {
        if (!isReg(v)) {
            OutputHandler.getInstance().writeln("lw $27, " + v.substring(2) + "($sp)");
            v = "$27";
        }
        if (isReg(target)) {
            OutputHandler.getInstance().writeln("subu " + target + ", $0, " + v);
        } else {
            OutputHandler.getInstance().writeln("subu $27, $0, " + v);
            OutputHandler.getInstance().writeln("sw $27, " + target.substring(2) + "($sp)");
        }
    }

    @Override
    public String getDefine() {
        return target;
    }

    public void setDefine(String d) {
        target = d;
    }

    @Override
    public Set<String> getUse() {
        return new HashSet<>() {{
            add(v);
        }};
    }
}
