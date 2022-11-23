package cfg.quad.mem;

import cfg.Function;
import cfg.quad.Quadruple;
import statics.assembly.AssemblyType;
import statics.io.OutputHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static cfg.quad.QuadUtil.isDec;
import static cfg.quad.QuadUtil.isReg;

public class SW extends Quadruple {
    String offset, integer, pointer;

    @Override
    public void print() throws IOException {
        OutputHandler.getInstance().writeln("SAVE_WORD " + integer + " -> " + pointer + "(" + offset + ")");
    }

    public SW(String i, String pointer, String offset) {
        super(AssemblyType.SW);
        this.integer = i;
        this.offset = offset;
        this.pointer = pointer;
    }

    public Set<String> getUse() {
        HashSet<String> r = new HashSet<>();
        r.add(offset);
        r.add(integer);
        r.add(pointer);
        return r;
    }

    @Override
    public void replaceUse(String o, String t) {
        if (o.equals(offset)) {
            offset = t;
        }
        if (o.equals(pointer)) {
            pointer = t;
        }
        if (integer.equals(o)) {
            integer = t;
        }
    }

    public void assemble(Function f) throws IOException {
        String os = null;
        switch (pointer.charAt(0)) {
            case 'v': {
                os = "la $27, " + pointer;
                pointer = "$27";
                break;
            }
            case 'a':// ARRAY
            {
                os = "addi $27, $sp, " + pointer.substring(5);
                pointer = "$27";
                break;
            }
            case 's': {
                os = "lw $27, " + pointer.substring(2) + "($sp)";
                pointer = "$27";
                break;
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
        } else {
            if (integer.charAt(0) == 's') {
                OutputHandler.getInstance().writeln("lw $28, " + integer.substring(2) + "($sp)");
            } else {
                OutputHandler.getInstance().writeln("li $28, " + integer);
            }
            OutputHandler.getInstance().writeln("sw $28, " + offset + "(" + pointer + ")");
        }
    }
}
