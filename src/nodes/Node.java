package nodes;

import cfg.BasicBlock;
import cfg.quad.func.Assign;
import cfg.quad.jump.FalseJump;
import cfg.quad.jump.TrueJump;
import statics.assembly.AssemblyInfo;
import statics.assembly.AssemblyRes;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;
import statics.setup.SyntaxType;

import java.io.IOException;
import java.util.LinkedList;

import static cfg.CFGBuilder.CFG_BUILDER;
import static cfg.quad.QuadUtil.isNumberFormat;

public abstract class Node {
    public SyntaxType type;
    public int startLine;
    public int finishLine;
    public LinkedList<Node> children;
    public String val;

    public Node() {
        children = new LinkedList<>();
    }

    public void shortCircuitOR(AssemblyInfo info, AssemblyRes res) throws IOException {
        BasicBlock block = null;
        boolean fl = false;
        AssemblyRes r = new AssemblyRes();
        children.getFirst().assemble(info, r);
        if (isNumberFormat(r.res)) {
            if (Integer.parseInt(r.res) > 0) {
                res.res = val = "1";
                return;
            } else {
                fl = true;
            }
        } else {
            block = CFG_BUILDER.createBB();
            res.res = val = CFG_BUILDER.tempVar();
            CFG_BUILDER.insert(new Assign(val, r.res));
            CFG_BUILDER.insert(new TrueJump(block, val));
            CFG_BUILDER.switchBlock(CFG_BUILDER.createBB());
        }
        r = new AssemblyRes();
        children.getLast().assemble(info, r);
        if (fl) {
            if (isNumberFormat(r.res)) {
                res.res = val = Integer.parseInt(r.res) > 0 ? "1" : "0";
                return;
            }
            res.res = val = CFG_BUILDER.tempVar();
        }
        CFG_BUILDER.insert(new Assign(val, r.res));
        if (!fl) {
            CFG_BUILDER.switchBlock(block);
        }
    }

    public void shortCircuitAND(AssemblyInfo info, AssemblyRes res) throws IOException {
        BasicBlock block = null;
        boolean fl = false;
        AssemblyRes r = new AssemblyRes();
        children.getFirst().assemble(info, r);
        if (isNumberFormat(r.res)) {
            if (Integer.parseInt(r.res) == 0) {
                res.res = val = "0";
                return;
            } else {
                fl = true;
            }
        } else {
            block = CFG_BUILDER.createBB();
            res.res = val = CFG_BUILDER.tempVar();
            CFG_BUILDER.insert(new Assign(val, r.res));
            CFG_BUILDER.insert(new FalseJump(block, val));
            CFG_BUILDER.switchBlock(CFG_BUILDER.createBB());
        }
        r = new AssemblyRes();
        children.getLast().assemble(info, r);
        if (fl) {
            if (isNumberFormat(r.res)) {
                res.res = val = Integer.parseInt(r.res) > 0 ? "1" : "0";
                return;
            }
            res.res = val = CFG_BUILDER.tempVar();
        }
        CFG_BUILDER.insert(new Assign(val, r.res));
        if (!fl) {
            CFG_BUILDER.switchBlock(block);
        }
    }

    public void set(SyntaxType type) {
        this.type = type;
        startLine = children.getFirst().startLine;
        finishLine = children.getLast().finishLine;
    }

    public void set(SyntaxType type, int line) {
        this.type = type;
        startLine = line;
        finishLine = line;
    }

    public void addChild(Node child) {
        children.offerFirst(child);
    }

    public void print() {
    }

    public void check(ExcCheckInfo info, ExcCheckRes res) {
    }

    public void assemble(AssemblyInfo info, AssemblyRes res) throws IOException {
    }
}
