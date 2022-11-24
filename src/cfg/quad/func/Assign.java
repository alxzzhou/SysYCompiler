package cfg.quad.func;

import cfg.Function;
import cfg.quad.Quadruple;
import statics.assembly.AssemblyType;
import statics.io.OutputHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static cfg.quad.QuadUtil.isNumberFormat;

public class Assign extends Quadruple {
    String target, integer;

    public Assign(String target, String integer) {
        super(AssemblyType.ASSIGN);
        this.target = target;
        this.integer = integer;
    }

    @Override
    public void print() throws IOException {
        OutputHandler.getInstance().writeln("ASSIGN " + target + " = " + integer);
    }

    public void assemble(Function f) throws IOException {
        if (integer.charAt(0) == 's') {
            OutputHandler.getInstance()
                    .writeln("lw $27, " + integer.substring(2) + "($sp)");
            integer = "$27";
        }
        if (target.charAt(0) == '$') {
            OutputHandler.getInstance()
                    .writeln(
                            isNumberFormat(integer) ?
                                    "li " + target + ", " + integer :
                                    "move " + target + ", " + integer
                    );
        } else if (target.charAt(0) == 's') {
            if (isNumberFormat(integer)) {
                OutputHandler.getInstance().writeln("li $27, " + integer);
                integer = "$27";
            }
            OutputHandler.getInstance()
                    .writeln("sw " + integer + ", " + target.substring(2) + "($sp)");
        }
    }

    public String getDefine() {
        return target;
    }

    public void setDefine(String d) {
        target = d;
    }

    @Override
    public void replaceUse(String o, String t) {
        if (o.equals(integer)) {
            integer = t;
        }
    }

    public Set<String> getUse() {
        HashSet<String> r = new HashSet<>();
        r.add(integer);
        return isNumberFormat(integer) ?
                new HashSet<>() : r;
    }
}
