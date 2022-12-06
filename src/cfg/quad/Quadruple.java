package cfg.quad;

import cfg.BasicBlock;
import cfg.Function;
import statics.assembly.AssemblyType;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Quadruple {
    public Quadruple next, prev;
    public BasicBlock owner;
    public AssemblyType type;
    public boolean enabled = true;

    public Quadruple(AssemblyType t) {
        type = t;
    }

    public void disable() {
        this.enabled = false;
    }

    public String getInteger(){return null;}

    public String getMemory() {
        return null;
    }

    public void changeNextBlock(BasicBlock b) {
    }

    public List<String > getCalcSequence() {
        return null;
    }

    public BasicBlock getJumpBlock() {
        return null;
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
