package cfg.quad.func;

import cfg.Function;
import cfg.quad.Quadruple;
import statics.io.OutputHandler;

import java.io.IOException;

import static statics.assembly.AssemblyType.GET_INT;

public class GetInt extends Quadruple {
    public GetInt() {
        super(GET_INT);
    }

    public void print() throws IOException {
        OutputHandler.getInstance().writeln("GET_INT");
    }

    public void assemble(Function f) throws IOException {
        OutputHandler.getInstance().writeln("li $v0, 5");
        OutputHandler.getInstance().writeln("syscall");
    }
}
