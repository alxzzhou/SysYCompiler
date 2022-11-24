package cfg.quad.func;

import cfg.Function;
import cfg.quad.Quadruple;
import statics.io.OutputHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static cfg.quad.QuadUtil.isReg;
import static statics.assembly.AssemblyType.RETURN;

public class Return extends Quadruple {
    String res;

    public Return(String r) {
        super(RETURN);
        this.res = r;
    }

    @Override
    public void print() throws IOException {
        OutputHandler.getInstance().writeln("RETURN " + res);
    }

    @Override
    public void assemble(Function f) throws IOException {
        if (res.equals("") || res.equals("-")) {
            f._return();
            return;
        }
        if (isReg(res)) {
            OutputHandler.getInstance().writeln("move $v0, " + res);
        } else if (res.charAt(0) == 's') {
            OutputHandler.getInstance().writeln("lw $v0, " + res.substring(2) + "($sp)");
        } else {
            OutputHandler.getInstance().writeln("li $v0, " + res);
        }
        f._return();
    }

    public Set<String> getUse() {
        HashSet<String> r = new HashSet<>();
        r.add(res);
        return r;
    }

    @Override
    public void replaceUse(String o, String t) {
        if (o.equals(res)) {
            res = t;
        }
    }
}
