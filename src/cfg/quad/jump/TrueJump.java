package cfg.quad.jump;

import cfg.BasicBlock;
import cfg.Function;
import cfg.quad.Quadruple;
import statics.assembly.AssemblyType;
import statics.io.OutputHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static cfg.quad.QuadUtil.isNumberFormat;
import static cfg.quad.QuadUtil.isReg;

public class TrueJump extends Quadruple {
    public BasicBlock bb;
    String val;

    public TrueJump(BasicBlock b, String v) {
        super(AssemblyType.JUMP_TRUE);
        this.bb = b;
        this.val = v;
    }

    @Override
    public void assemble(Function f) throws IOException {
        if (isNumberFormat(val)) {
            int i = Integer.parseInt(val);
            if (i != 0) {
                OutputHandler.getInstance().writeln("j " + bb.tag);
            }
            return;
        } else if (!isReg(val)) {
            OutputHandler.getInstance().writeln("lw $27, " + val.substring(2) + "($sp)");
            val = "$27";
        }
        OutputHandler.getInstance().writeln("bnez " + val + ", " + bb.tag);
    }

    @Override
    public Set<String> getUse() {
        HashSet<String> r = new HashSet<>();
        r.add(val);
        return r;
    }

    @Override
    public void replaceUse(String o, String t) {
        if (o.equals(val)) {
            val = t;
        }
    }
}
