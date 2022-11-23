package cfg.quad.func;

import cfg.Function;
import cfg.quad.Quadruple;
import statics.assembly.AssemblyType;
import statics.io.OutputHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class PrintString extends Quadruple {
    public String s;

    @Override
    public void print() throws IOException {
        OutputHandler.getInstance().writeln("PRINT_STRING " + s);
    }

    public PrintString(String s) {
        super(AssemblyType.PRINT_STR);
        this.s = s;
    }

    @Override
    public Set<String> getUse() {
        HashSet<String> r = new HashSet<>();
        r.add(s);
        return r;
    }

    @Override
    public void replaceUse(String o, String t) {
        if (o.equals(s)) {
            s = t;
        }
    }

    @Override
    public void assemble(Function f) throws IOException {
        OutputHandler.getInstance().writeln("la $a0, " + s);
        OutputHandler.getInstance().writeln("li $v0, 4");
        OutputHandler.getInstance().writeln("syscall");
    }
}
