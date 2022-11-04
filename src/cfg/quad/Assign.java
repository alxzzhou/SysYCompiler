package cfg.quad;

import cfg.Function;
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

    public Set<String> getUse() {
        return isNumberFormat(integer) ?
                new HashSet<>() :
                new HashSet<>() {{
                    add(integer);
                }};
    }
}
