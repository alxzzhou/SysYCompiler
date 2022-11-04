package cfg;

import cfg.quad.Quadruple;

import java.util.HashSet;

public class CFGBuilder {
    public static final boolean OPTIMIZE = false;

    public static final CFGBuilder CFG_BUILDER =
            new CFGBuilder();

    HashSet<Function> funcs = new HashSet<>();
    Function cf;
    public BasicBlock cbb;
    int bbcnt = 0, temp = 0;

    public CFGBuilder() {
    }

    public void ralloc() {
        funcs.forEach(Function::ralloc);
    }

    public void insert(Quadruple q) {
        cbb.insert(q);
    }

    public void switchBlock(BasicBlock b) {
        if (cbb != b) {
            cbb = b;
            cf.addBasicBlock(b);
        }
    }

    public void switchFunc(Function f) {
        cf = f;
        funcs.add(f);
    }

    public BasicBlock createBB() {
        ++bbcnt;
        return new BasicBlock(bbcnt);
    }

    public String tempVar() {
        temp++;
        return "TEMP_" + temp;
    }
}
