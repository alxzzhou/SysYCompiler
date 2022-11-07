package cfg.quad.func;

import cfg.Function;
import cfg.quad.Quadruple;
import statics.assembly.AssemblyType;
import statics.io.OutputHandler;

import java.io.IOException;

public class PrintString extends Quadruple {
    public String s;

    public PrintString(String s) {
        super(AssemblyType.PRINT_STR);
        this.s = s;
    }

    @Override
    public void assemble(Function f) throws IOException {
        OutputHandler.getInstance().writeln("la $a0, " + s);
        OutputHandler.getInstance().writeln("li $v0, 4");
        OutputHandler.getInstance().writeln("syscall");
    }
}
