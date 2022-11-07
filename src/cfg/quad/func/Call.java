package cfg.quad.func;

import cfg.Function;
import cfg.quad.Quadruple;
import statics.assembly.AssemblyType;
import statics.io.OutputHandler;

import java.io.IOException;

public class Call extends Quadruple {
    String name;

    public Call(String name) {
        super(AssemblyType.FUNC_CALL);
        this.name = name;
    }

    @Override
    public void assemble(Function f) throws IOException {
        OutputHandler.getInstance().writeln("jal " + name);
    }
}
