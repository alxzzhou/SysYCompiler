package cfg.quad.func;

import cfg.Function;
import cfg.quad.Quadruple;
import statics.assembly.AssemblyType;
import statics.io.OutputHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static cfg.quad.QuadUtil.isReg;

public class PrintInteger extends Quadruple {
    String i;

    public PrintInteger(String i) {
        super(AssemblyType.PRINT_INT);
        this.i = i;
    }

    @Override
    public void print() throws IOException {
        OutputHandler.getInstance().writeln("PRINT_INT " + i);
    }

    @Override
    public Set<String> getUse() {
        HashSet<String> r = new HashSet<>();
        r.add(i);
        return r;
    }

    @Override
    public void replaceUse(String o, String t) {
        if (o.equals(i)) {
            i = t;
        }
    }

    @Override
    public void assemble(Function f) throws IOException {
        if (isReg(i)) {
            OutputHandler.getInstance().writeln("move $a0, " + i);
        } else if (i.charAt(0) == 's') {
            OutputHandler.getInstance()
                    .writeln("lw $a0, " + i.substring(2) + "($sp)");
        } else {
            OutputHandler.getInstance().writeln("li $a0, " + i);
        }
        OutputHandler.getInstance().writeln("li $v0, 1");
        OutputHandler.getInstance().writeln("syscall");
    }
}
