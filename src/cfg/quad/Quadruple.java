package cfg.quad;

import cfg.BasicBlock;
import cfg.Function;
import statics.assembly.AssemblyType;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public abstract class Quadruple {
    public Quadruple next, prev;
    public BasicBlock owner;
    public AssemblyType type;

    public Quadruple(AssemblyType t) {
        type = t;
    }

    public String getDefine() {
        return null;
    }

    public void setDefine(String d) {
    }

    public Set<String> getUse() {
        return new HashSet<>();
    }

    public void replaceUse(String o, String t) {
    }

    public void print() throws IOException {
    }

    public void assemble(Function f) throws IOException {
    }
}
