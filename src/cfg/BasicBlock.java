package cfg;

import cfg.quad.PhiFunc;
import cfg.quad.Quadruple;
import statics.io.OutputHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static statics.assembly.AssemblyType.CREATE_POINTER;
import static statics.assembly.AssemblyType.PHI;

public class BasicBlock {
    String tag;
    public Quadruple head, tail;
    public HashSet<BasicBlock>
            pred = new HashSet<>(),
            succ = new HashSet<>(),
            dominator = new HashSet<>();
    List<BasicBlock> domChildren = new ArrayList<>();
    BasicBlock nearestDominator;
    HashMap<String, PhiFunc> phiNode = new HashMap<>();
    HashSet<String>
            activeIn = new HashSet<>(),
            activeOut = new HashSet<>(),
            varDefine = new HashSet<>(),
            varUse = new HashSet<>();
    int domSize = 0, dfsn = 0;
    public BasicBlock next, prev;

    public BasicBlock(int id) {
        tag = "LABEL_" + id;
    }

    public BasicBlock(BasicBlock f, BasicBlock t) {
        tag = f.tag + "_" + t.tag;
    }

    public void assemble(Function f) throws IOException {
        OutputHandler.getInstance().writeln(tag + ":");
        for (Quadruple q = head; q != null; q = q.next) {
            OutputHandler.getInstance().write("# ");
            q.print();
            q.assemble(f);
        }
    }

    public void aliasify(HashMap<String, Integer> v2r,
                         HashMap<String, Integer> v2m,
                         HashMap<String, Integer> a2m) {
        for (Quadruple q = tail; q != null; q = q.prev) {
            var d = q.getDefine();
            if (d != null) {
                if (v2r.containsKey(d)) {
                    q.setDefine("$" + v2r.get(d));
                } else if (v2m.containsKey(d)) {
                    q.setDefine("sp" + v2m.get(d));
                } else if (a2m.containsKey(d)) {
                    q.setDefine("array" + a2m.get(d));
                }
            }
            var use = q.getUse();
            Set<String> temp = new HashSet<>();
            for (var u : use) {
                if (v2r.containsKey(u)) {
                    temp.add("$" + v2r.get(u));
                    use.remove(u);
                } else if (v2m.containsKey(u)) {
                    temp.add("sp" + v2m.get(u));
                    use.remove(u);
                } else if (a2m.containsKey(u)) {
                    temp.add("array" + a2m.get(u));
                    use.remove(u);
                }
            }
            use.addAll(temp);
        }
    }

    public void calcOutVar() {
        for (var v : varUse) {
            if (!activeIn.contains(v) && !varDefine.contains(v)) {
                activeIn.add(v);
                for (var p : pred) {
                    p.addOutVar(v);
                }
            }
        }
    }

    public void addOutVar(String p) {
        if (!activeOut.contains(p)) {
            activeOut.add(p);
            if (!activeIn.contains(p) && !varDefine.contains(p)) {
                activeIn.add(p);
                for (var pr : pred) {
                    pr.addOutVar(p);
                }
            }
        }
    }

    public void insert(Quadruple q) {
        q.owner = this;
        if (tail == null) {
            head = tail = q;
        } else {
            q.prev = tail;
            tail.next = q;
            tail = q;
        }
    }

    public void insertUse(Quadruple q) {
        if (q.type == PHI) {
            return;
        }
        Set<String> u = q.getUse();
        varUse.addAll(u);
    }

    public void insertDef(Quadruple q) {
        if (q.type == CREATE_POINTER) {
            return;
        }
        String s = q.getDefine();
        varDefine.add(s);
    }
}