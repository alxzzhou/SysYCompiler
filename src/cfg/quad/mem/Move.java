package cfg.quad.mem;

import cfg.Function;
import cfg.quad.Quadruple;
import statics.io.OutputHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static statics.assembly.AssemblyType.MOVE;

public class Move extends Quadruple {
    String source, target;

    public Move(String source, String target) {
        super(MOVE);
        this.source = source;
        this.target = target;
    }

    @Override
    public void print() throws IOException {
        OutputHandler.getInstance().writeln("MOVE " + target + " <- " + source);
    }

    @Override
    public void assemble(Function f) throws IOException {
        OutputHandler.getInstance().writeln("move " + target + ", " + source);
    }

    @Override
    public Set<String> getUse() {
        return new HashSet<String>() {{
            add(source);
        }};
    }

    @Override
    public void replaceUse(String o, String t) {
        if (o.equals(source)) {
            source = t;
        }
    }

    @Override
    public String getDefine() {
        return target;
    }

    public void setDefine(String d) {
        target = d;
    }
}
