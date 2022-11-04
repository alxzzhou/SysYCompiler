package cfg.quad;

import cfg.Function;
import statics.assembly.AssemblyType;
import statics.io.OutputHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static cfg.quad.QuadUtil.isDec;
import static cfg.quad.QuadUtil.isReg;

public class SW extends Quadruple {
    String offset, integer, pointer;

    public SW(String i, String pointer, String offset) {
        super(AssemblyType.SW);
        this.integer = i;
        this.offset = offset;
        this.pointer = pointer;
    }

    public Set<String> getUse() {
        return new HashSet<>() {{
            add(integer);
            add(offset);
            add(pointer);
        }};
    }

    public void assemble(Function f) throws IOException {
        String os = null;
        switch (pointer.charAt(0)) {
            case 'v' -> {
                os = "la $27, " + pointer;
                pointer = "$27";
            }
            case 'a' ->// ARRAY
            {
                os = "addi $27, $sp, " + pointer.substring(5);
                pointer = "$27";
            }
            case 's' -> {
                os = "lw $27, " + pointer.substring(2) + "($sp)";
                pointer = "$27";
            }
        }
        OutputHandler.getInstance().writeln(os);
        if (isDec(offset)) {
            offset = String.valueOf(Integer.parseInt(offset) * 4);
        } else {
            if (!isReg(offset)) {
                OutputHandler.getInstance()
                        .writeln("lw $28, " + offset.substring(2) + "($sp)");
                offset = "$28";
            }
            OutputHandler.getInstance().writeln("sll $28, " + offset + ", 2");
            OutputHandler.getInstance().writeln("add $27, $28, " + pointer);
            pointer = "$27";
            offset = "0";
        }
        if (isReg(integer)) {
            OutputHandler.getInstance()
                    .writeln("sw " + integer + ", " + offset + "(" + pointer + ")");
        }
    }
}
