package cfg;

import cfg.quad.Quadruple;
import statics.io.OutputHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public class BasicBlock {
    public String tag;
    public Quadruple head, tail;
    public BasicBlock next, prev;

    public BasicBlock(int id) {
        tag = "LABEL_" + id;
    }

    public BasicBlock(BasicBlock f, BasicBlock t) {
        tag = f.tag + "_" + t.tag;
    }

    public void print() throws IOException {
        OutputHandler.getInstance().writeln(tag + ":");
        for (Quadruple q = head; q != null; q = q.next) {
            q.print();
        }
    }

    public void assemble(Function f) throws IOException {
        OutputHandler.getInstance().writeln(tag + ":");
        for (Quadruple q = head; q != null; q = q.next) {
            q.assemble(f);
        }
    }

    public void aliasify(
            HashMap<String, Integer> v2m,
            HashMap<String, Integer> a2m) {
        for (Quadruple q = tail; q != null; q = q.prev) {
            String d = q.getDefine();
            if (d != null) {
                if (v2m.containsKey(d)) {
                    q.setDefine("sp" + v2m.get(d));
                } else if (a2m.containsKey(d)) {
                    q.setDefine("array" + a2m.get(d));
                }
            }
            Set<String> use = q.getUse();
            for (String u : use) {
                if (v2m.containsKey(u)) {
                    q.replaceUse(u, "sp" + v2m.get(u));
                } else if (a2m.containsKey(u)) {
                    q.replaceUse(u, "array" + a2m.get(u));
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

}
