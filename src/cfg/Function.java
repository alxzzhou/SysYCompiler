package cfg;

import cfg.quad.Quadruple;
import cfg.quad.mem.NP;
import statics.io.OutputHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static statics.assembly.AssemblyType.CREATE_POINTER;

public class Function {
    final HashMap<String, Integer>
            v2m = new HashMap<>(),
            a2m = new HashMap<>();
    public int addr = 0;
    String tag;
    BasicBlock head, tail;
    HashMap<Integer, Integer> r2m = new HashMap<>();
    int paramNum;

    public Function(String tag, int pn) {
        this.tag = tag;
        this.paramNum = pn;
    }

    public void print() throws IOException {
        OutputHandler.getInstance().writeln(tag + ":");
        for (BasicBlock b = head; b != null; b = b.next) {
            b.print();
        }
    }

    public void assemble() throws IOException {
        for (BasicBlock b = head; b != null; b = b.next) {
            b.aliasify(v2m, a2m);
        }
        // TODO:
        OutputHandler.getInstance().writeln(tag + ":");
        addr += paramNum > 4 ? paramNum * 4 - 16 : 0;
        OutputHandler.getInstance().writeln("subi $sp, $sp, " + addr);
        for (Map.Entry<Integer, Integer> vr : r2m.entrySet()) {
            OutputHandler.getInstance()
                    .writeln("sw $" + vr.getKey() + ", " + vr.getValue() + "($sp)");
        }
        for (BasicBlock b = head; b != null; b = b.next) {
            b.assemble(this);
        }
    }

    public void ralloc() {
        for (BasicBlock b = head; b != null; b = b.next) {
            for (Quadruple q = b.head; q != null; q = q.next) {
                String d = q.getDefine();
                if (d != null) {
                    if (q.type == CREATE_POINTER) {
                        a2m.put(d, addr);
                        addr += ((NP) q).s * 4;
                    } else {
                        v2m.put(d, addr);
                        addr += 4;
                    }
                }
            }
        }
        r2m.put(31, addr);
        addr += 4;
    }

    public void addBasicBlock(BasicBlock b) {
        if (head == null) {
            head = tail = b;
        } else {
            tail.next = b;
            b.prev = tail;
            tail = b;
        }
    }

    public void _return() throws IOException {
        for (Map.Entry<Integer, Integer> v : r2m.entrySet()) {
            OutputHandler.getInstance().writeln("lw $" + v.getKey() + ", " + v.getValue() + "($sp)");
        }
        OutputHandler.getInstance().writeln("addi $sp, $sp, " + addr);
        OutputHandler.getInstance().writeln("jr $31");
    }
}
