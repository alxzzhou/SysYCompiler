package cfg.quad.mem;

import cfg.Function;
import cfg.quad.Quadruple;
import statics.io.OutputHandler;

import java.io.IOException;

import static statics.assembly.AssemblyType.CREATE_POINTER;

public class Pointer extends Quadruple {
    public String name;
    public int size;

    public Pointer(String n, int s) {
        super(CREATE_POINTER);
        name = n;
        size = s;
    }

    @Override
    public String getDefine() {
        return name;
    }

    @Override
    public void setDefine(String d) {
        name = d;
    }


    @Override
    public void print() throws IOException {
        OutputHandler.getInstance()
                .writeln("pointer " + name + " size_of " + size + "\n");
    }

    @Override
    public void assemble(Function f) {

    }
}