package cfg.quad.calc;

import cfg.Function;
import cfg.quad.Quadruple;
import statics.assembly.AssemblyType;
import statics.io.OutputHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static cfg.quad.QuadUtil.isReg;

public class Not extends Quadruple {
    String target, v;

    @Override
    public void print() throws IOException {
        OutputHandler.getInstance().writeln(target + " = ! " + v);
    }

    public Not(String target, String v) {
        super(AssemblyType.NOT_LOGIC);
        this.target = target;
        this.v = v;
    }

    @Override
    public void assemble(Function f) throws IOException {
        if (!isReg(v)) {
            OutputHandler.getInstance().writeln("lw $27, " + v.substring(2) + "($sp)");
            v = "$27";
        }
        if (isReg(target)) {
            OutputHandler.getInstance().writeln("seq " + target + ", $0, " + v);
        } else {
            OutputHandler.getInstance().writeln("seq $27, $0, " + v);
            OutputHandler.getInstance().writeln("sw $27, " + target.substring(2) + "($sp)");
        }
    }

    public String getDefine() {
        return target;
    }

    public void setDefine(String d) {
        target = d;
    }

    @Override
    public void replaceUse(String o, String t) {
        if (o.equals(v)) {
            v = t;
        }
    }

    @Override
    public Set<String> getUse() {
        HashSet<String> r = new HashSet<>();
        r.add(v);
        return r;
    }
}
