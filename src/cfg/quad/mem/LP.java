package cfg.quad.mem;

import cfg.Function;
import cfg.quad.Quadruple;
import statics.assembly.AssemblyType;
import statics.io.OutputHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static cfg.quad.QuadUtil.isNumberFormat;
import static cfg.quad.QuadUtil.isReg;

public class LP extends Quadruple {
    String target, addr, ofs;

    public LP(String target, String addr, String ofs) {
        super(AssemblyType.LOAD_ADDRESS);
        this.target = target;
        this.addr = addr;
        this.ofs = ofs;
    }

    @Override
    public void print() throws IOException {
        OutputHandler.getInstance().writeln("LOAD_POINTER " + target + " <- " + addr + "(" + ofs + ")");
    }

    @Override
    public void assemble(Function f) throws IOException {
        switch (addr.charAt(0)) {
            case 'a': {
                OutputHandler.getInstance().writeln("addi $27, $sp, " + addr.substring(5));
                addr = "$27";
                break;
            }
            case 'v': {
                OutputHandler.getInstance().writeln("la $27, " + addr);
                addr = "$27";
                break;
            }
            case 's': {
                OutputHandler.getInstance().writeln("lw $27, " + addr.substring(2) + "($sp)");
                addr = "$27";
                break;
            }
        }
        if (isNumberFormat(ofs)) {
            ofs = String.valueOf(Integer.parseInt(ofs));
        } else {
            if (!isReg(ofs)) {
                OutputHandler.getInstance().writeln("lw $28, " + ofs.substring(2) + "($sp)");
                ofs = "$28";
            }
            OutputHandler.getInstance().writeln("sll $28, " + ofs + ", 2");
            ofs = "$28";
        }
        if (isReg(target)) {
            OutputHandler.getInstance().writeln("add " + target + ", " + addr + ", " + ofs);
        } else {
            OutputHandler.getInstance().writeln("add $27, " + addr + ", " + ofs);
            OutputHandler.getInstance().writeln("sw $27, " + target.substring(2) + "($sp)");
        }
    }

    @Override
    public String getDefine() {
        return target;
    }

    public void setDefine(String d) {
        target = d;
    }

    @Override
    public Set<String> getUse() {
        HashSet<String> r = new HashSet<>();
        r.add(ofs);
        r.add(addr);
        return r;
    }

    @Override
    public void replaceUse(String o, String t) {
        if (o.equals(ofs)) {
            ofs = t;
        }
        if (o.equals(addr)) {
            addr = t;
        }
    }
}
