package cfg.quad.calc;

import cfg.Function;
import cfg.quad.Quadruple;
import statics.io.OutputHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static cfg.quad.QuadUtil.isReg;
import static statics.assembly.AssemblyType.SINGLETON;

public class Singleton extends Quadruple {
    String target, val;

    public Singleton(String target, String val) {
        super(SINGLETON);
        this.target = target;
        this.val = val;
    }

    @Override
    public void print() throws IOException {
        OutputHandler.getInstance().writeln(target + " = " + val);
    }

    @Override
    public String getDefine() {
        return target;
    }

    @Override
    public void setDefine(String d) {
        target = d;
    }

    @Override
    public Set<String> getUse() {
        HashSet<String> r = new HashSet<>();
        r.add(val);
        return r;
    }

    @Override
    public void replaceUse(String o, String t) {
        if (o.equals(val)) {
            val = t;
        }
    }

    @Override
    public void assemble(Function f) throws IOException {
        if (val.charAt(0) == 's') {
            OutputHandler.getInstance()
                    .writeln("lw $27, " + val.substring(2) + "($sp)");
            val = "$27";
        } else if (!isReg(val)) {
            OutputHandler.getInstance()
                    .writeln(("lw $27, " + val));
            val = "$27";
        }
        if (isReg(target)) {
            OutputHandler.getInstance()
                    .writeln("move " + target + ", " + val);
        } else {
            OutputHandler.getInstance()
                    .writeln("sw $27, " + target.substring(2) + "($sp)");
        }
    }
}
