package cfg.quad.jump;

import cfg.BasicBlock;
import cfg.Function;
import cfg.quad.Quadruple;
import statics.assembly.AssemblyType;
import statics.io.OutputHandler;

import java.io.IOException;

public class UnconditionalJump extends Quadruple {
    public BasicBlock b;

    public UnconditionalJump(BasicBlock block) {
        super(AssemblyType.JUMP);
        this.b = block;
    }

    @Override
    public void changeNextBlock(BasicBlock b) {
        this.b = b;
    }

    @Override
    public BasicBlock getJumpBlock() {
        return b;
    }

    @Override
    public void print() throws IOException {
        OutputHandler.getInstance().writeln("JUMP TO " + b.tag);
    }

    @Override
    public void assemble(Function f) throws IOException {
        OutputHandler.getInstance().writeln("j " + b.tag);
    }
}
