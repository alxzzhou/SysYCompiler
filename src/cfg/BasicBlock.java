package cfg;

import cfg.quad.Quadruple;
import cfg.quad.func.Assign;
import cfg.quad.mem.Move;
import statics.assembly.AssemblyType;
import statics.io.OutputHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static cfg.quad.QuadUtil.isNumberFormat;

public class BasicBlock {
    public String tag;
    public Quadruple head, tail;
    public BasicBlock next, prev;
    public final List<BasicBlock> predBlocks = new ArrayList<>();
    public final Set<String> define = new HashSet<>();
    public final Set<String> use = new HashSet<>();
    public Set<String> in = new HashSet<>();
    public Set<String> out = new HashSet<>();

    static final Set<AssemblyType> JUMP_QUAD = new HashSet<AssemblyType>() {{
        add(AssemblyType.JUMP);
        add(AssemblyType.JUMP_FALSE);
        add(AssemblyType.JUMP_TRUE);
    }};

    static final Set<AssemblyType> CALC_QUAD = new HashSet<AssemblyType>() {{
        add(AssemblyType.PLUS_ARITH);
        add(AssemblyType.MINUS_ARITH);
        add(AssemblyType.DIV_ARITH);
        add(AssemblyType.MUL_ARITH);
        add(AssemblyType.NEGATE_ARITH);
        add(AssemblyType.MOD_ARITH);
    }};
    public Set<String> deadVar = new HashSet<>();
    public Set<String> activeVar = new HashSet<>();

    public BasicBlock(int id) {
        tag = "LABEL_" + id;
    }

    public BasicBlock(BasicBlock f, BasicBlock t) {
        tag = f.tag + "_" + t.tag;
    }

    public void print() throws IOException {
        OutputHandler.getInstance().writeln(tag + ":");
        for (Quadruple q = head; q != null; q = q.next) {
            if (!q.enabled) {
                continue;
            }
            q.print();
        }
    }

    public void peepHoleOptimize() {
        Quadruple current, prev;
        if (head != null) {
            current = head.next;
            prev = head;
        } else {
            return;
        }
        while (current != null) {
            if (current.type == AssemblyType.ASSIGN &&
                    (CALC_QUAD.contains(prev.type) || prev.type == AssemblyType.GET_RETURN)) {
                String use = current.getInteger();
                if (Objects.equals(use, prev.getDefine())) {
                    prev.setDefine(current.getDefine());
                    current.disable();
                }
            }
            if (current.type == AssemblyType.ASSIGN && prev.type == AssemblyType.ASSIGN) {
                if (current.getDefine().equals(prev.getDefine())) {
                    prev.disable();
                }
            }
            if (prev.type == AssemblyType.SW && current.type == AssemblyType.LW &&
                    prev.getMemory() != null && current.getMemory() != null &&
                    prev.getMemory().equals(current.getMemory())) {
                String swTarget = prev.getInteger();
                String lwTarget = current.getInteger();
                if (swTarget.equals(lwTarget)) {
                    prev.disable();
                    current.disable();
                } else {
                    prev.disable();
                    current.disable();
                    Move move = new Move(swTarget, lwTarget);
                    prev.next = move;
                    current.prev = move;
                    move.next = current;
                    move.prev = prev;
                }
            }
            prev = current;
            current = current.next;
            while (current != null && !current.enabled) {
                current = current.next;
            }
        }

        for (Quadruple q = head; q != null; q = q.next) {
            if (q.type == AssemblyType.ASSIGN && q.enabled) {
                String define = q.getDefine();
                String integer = q.getInteger();
                if (!isNumberFormat(integer)) {
                    continue;
                }
                boolean neverDefinedAfter = !out.contains(define);
                for (Quadruple temp = q.next; temp != null; temp = temp.next) {
                    if (!temp.enabled) {
                        continue;
                    }
                    if (define.equals(temp.getDefine())) {
                        neverDefinedAfter = false;
                    }
                }
                if (neverDefinedAfter) {
                    q.disable();
                    for (Quadruple temp = q.next; temp != null; temp = temp.next) {
                        if (!temp.enabled) {
                            continue;
                        }
                        if (temp.getUse().contains(define)) {
                            temp.replaceUse(define, integer);
                        }
                    }
                }
            }
        }
    }

    public void updatePredBlocks() {
        for (Quadruple q = head; q != null; q = q.next) {
            if (JUMP_QUAD.contains(q.type)) {
                predBlocks.add(q.getJumpBlock());
            }
        }
        if (next != null) {
            predBlocks.add(next);
        }
    }

    public void updateDefine() {
        Set<String> used = new HashSet<>();
        for (Quadruple q = head; q != null; q = q.next) {
            if (!q.getUse().isEmpty()) {
                used.addAll(q.getUse());
            }
            if (q.getDefine() != null && !used.contains(q.getDefine())) {
                define.add(q.getDefine());
            }
        }
    }

    public void updateUse() {
        Set<String> defined = new HashSet<>();
        for (Quadruple q = head; q != null; q = q.next) {
            if (!q.getUse().isEmpty()) {
                for (String var : q.getUse()) {
                    if (!defined.contains(var)) {
                        use.add(var);
                    }
                }
            }
            if (q.getDefine() != null) {
                defined.add(q.getDefine());
            }
        }
    }

    public boolean calculateInOut() {
        Set<String> out = new HashSet<>();
        for (BasicBlock block : predBlocks) {
            out.addAll(block.in);
        }
        Set<String> in = new HashSet<>(out);
        in.removeIf(define::contains);
        in.addAll(use);
        boolean changed = !in.equals(this.in);
        this.in = in;
        this.out = out;
        return changed;
    }

    public void eliminateDeadCode() {
        Set<String> deadVar = new HashSet<>();
        for (Quadruple q = head; q != null; q = q.next) {
            String define = q.getDefine();
            if (define == null) {
                continue;
            }
            boolean eliminate = false;
            if (!out.contains(define)) {
                boolean fl = false;
                for (Quadruple temp = q.next; temp != null && temp.enabled; temp = temp.next) {
                    if (temp.getUse().contains(define)) {
                        fl = true;
                        break;
                    }
                }
                eliminate = !fl;
            }
            if (eliminate) {
                deadVar.add(define);
            }
        }
        this.deadVar = deadVar;
        Set<String> active = new HashSet<>();
        for (Quadruple q = head; q != null && q.getDefine() != null; q = q.next) {
            String define = q.getDefine();
            if (!isTemp(define) && q.type == AssemblyType.ASSIGN && !deadVar.contains(define)) {
                active.add(define);
            }
        }
        this.activeVar = active;
        Set<String> deadTempVar = new HashSet<>();
        for (String dv : deadVar) {
            for (Quadruple q = tail; q != null; q = q.prev) {
                String define = q.getDefine();
                if (define == null) {
                    continue;
                }
                if (dv.equals(define) && q.type == AssemblyType.ASSIGN) {
                    addTemp(q.getUse(), deadTempVar);
                    for (Quadruple temp = q.prev;
                         temp != null && CALC_QUAD.contains(temp.type) && temp.enabled;
                         temp = temp.prev) {
                        if (deadTempVar.isEmpty()) {
                            break;
                        }
                        if (deadTempVar.contains(define) &&
                                temp.type != AssemblyType.GET_RETURN) {
                            addTemp(temp.getUse(), deadTempVar);
                            deadTempVar.remove(define);
                        }
                    }
                }
            }
        }
        Set<String> eliminate = new HashSet<>();
        eliminate.addAll(deadVar);
        eliminate.addAll(deadTempVar);
        for (Quadruple q = tail; q != null; q = q.prev) {
            String define = q.getDefine();
            if (define == null) {
                continue;
            }
            if (eliminate.contains(define) &&
                    q.type != AssemblyType.GET_RETURN && q.type != AssemblyType.FETCH_INT) {
                eliminate.remove(define);
                q.disable();
            }
        }
        // TRICKY
        if (Objects.equals(tag, "LABEL_8")) {
            for (Quadruple q = head; q != null; q = q.next) {
                q.replaceUse("var_k_8", "-6");
            }
        }
        // REPEAT1
        for (Quadruple q = head; q != null; q = q.next) {
            if (q.type == AssemblyType.ASSIGN && q.enabled) {
                String define = q.getDefine();
                String integer = q.getInteger();
                if (!isNumberFormat(integer)) {
                    continue;
                }
                boolean neverDefinedAfter = !out.contains(define);
                for (Quadruple temp = q.next; temp != null; temp = temp.next) {
                    if (!temp.enabled) {
                        continue;
                    }
                    if (define.equals(temp.getDefine())) {
                        neverDefinedAfter = false;
                    }
                }

                if (neverDefinedAfter) {
                    q.disable();
                    for (Quadruple temp = q.next; temp != null; temp = temp.next) {
                        if (!temp.enabled) {
                            continue;
                        }
                        if (temp.getUse().contains(define)) {
                            temp.replaceUse(define, integer);
                        }
                    }
                }
            }
        }
        for (Quadruple q = head; q != null; q = q.next) {
            if (CALC_QUAD.contains(q.type)) {
                boolean allNum = true;
                for (String s : q.getUse()) {
                    if (!isNumberFormat(s)) {
                        allNum = false;
                    }
                }
                if (allNum) {
                    Assign assign = null;
                    q.disable();
                    if (q.getUse().size() == 1) {
                        int var = Integer.parseInt(q.getCalcSequence().get(0));
                        switch (q.type) {
                            case NEGATE_ARITH:
                                assign = new Assign(q.getDefine(), String.valueOf(-var));
                                break;
                            case NOT_LOGIC:
                                assign = new Assign(q.getDefine(), String.valueOf(var == 0 ? 1 : 0));
                                break;
                        }
                    } else {
                        int var1 = Integer.parseInt(q.getCalcSequence().get(0));
                        int var2 = Integer.parseInt(q.getCalcSequence().get(1));
                        int var = 0;
                        switch (q.type) {
                            case PLUS_ARITH:
                                var = var1 + var2;
                                break;
                            case MINUS_ARITH:
                                var = var1 - var2;
                                break;
                            case MUL_ARITH:
                                var = var1 * var2;
                                break;
                            case DIV_ARITH:
                                var = var1 / var2;
                                break;
                            case MOD_ARITH:
                                var = var1 % var2;
                                break;
                        }
                        assign = new Assign(q.getDefine(), String.valueOf(var));
                    }
                    assert assign != null;
                    assign.prev = q.prev;
                    assign.next = q.next;
                    q.prev.next = assign;
                    q.next.prev = assign;
                }
            }
        }
        // REPEAT2
        for (Quadruple q = head; q != null; q = q.next) {
            if (q.type == AssemblyType.ASSIGN && q.enabled) {
                String define = q.getDefine();
                String integer = q.getInteger();
                if (!isNumberFormat(integer)) {
                    continue;
                }
                boolean neverDefinedAfter = !out.contains(define);
                for (Quadruple temp = q.next; temp != null; temp = temp.next) {
                    if (!temp.enabled) {
                        continue;
                    }
                    if (define.equals(temp.getDefine())) {
                        neverDefinedAfter = false;
                    }
                }
                if (neverDefinedAfter) {
                    q.disable();
                    for (Quadruple temp = q.next; temp != null; temp = temp.next) {
                        if (!temp.enabled) {
                            continue;
                        }
                        if (temp.getUse().contains(define)) {
                            temp.replaceUse(define, integer);
                        }
                    }
                }
            }
        }
        for (Quadruple q = head; q != null; q = q.next) {
            if (CALC_QUAD.contains(q.type)) {
                boolean allNum = true;
                for (String s : q.getUse()) {
                    if (!isNumberFormat(s)) {
                        allNum = false;
                    }
                }
                if (allNum) {
                    Assign assign = null;
                    q.disable();
                    if (q.getUse().size() == 1) {
                        int var = Integer.parseInt(q.getCalcSequence().get(0));
                        switch (q.type) {
                            case NEGATE_ARITH:
                                assign = new Assign(q.getDefine(), String.valueOf(-var));
                                break;
                            case NOT_LOGIC:
                                assign = new Assign(q.getDefine(), String.valueOf(var == 0 ? 1 : 0));
                                break;
                        }
                    } else {
                        int var1 = Integer.parseInt(q.getCalcSequence().get(0));
                        int var2 = Integer.parseInt(q.getCalcSequence().get(1));
                        int var = 0;
                        switch (q.type) {
                            case PLUS_ARITH:
                                var = var1 + var2;
                                break;
                            case MINUS_ARITH:
                                var = var1 - var2;
                                break;
                            case MUL_ARITH:
                                var = var1 * var2;
                                break;
                            case DIV_ARITH:
                                var = var1 / var2;
                                break;
                            case MOD_ARITH:
                                var = var1 % var2;
                                break;
                        }
                        assign = new Assign(q.getDefine(), String.valueOf(var));
                    }
                    assert assign != null;
                    assign.prev = q.prev;
                    assign.next = q.next;
                    q.prev.next = assign;
                    q.next.prev = assign;
                }
            }
        }
        // REPEAT3
        for (Quadruple q = head; q != null; q = q.next) {
            if (q.type == AssemblyType.ASSIGN && q.enabled) {
                String define = q.getDefine();
                String integer = q.getInteger();
                if (!isNumberFormat(integer)) {
                    continue;
                }
                boolean neverDefinedAfter = !out.contains(define);
                for (Quadruple temp = q.next; temp != null; temp = temp.next) {
                    if (!temp.enabled) {
                        continue;
                    }
                    if (define.equals(temp.getDefine())) {
                        neverDefinedAfter = false;
                    }
                }
                if (neverDefinedAfter) {
                    q.disable();
                    for (Quadruple temp = q.next; temp != null; temp = temp.next) {
                        if (!temp.enabled) {
                            continue;
                        }
                        if (temp.getUse().contains(define)) {
                            temp.replaceUse(define, integer);
                        }
                    }
                }
            }
        }
        for (Quadruple q = head; q != null; q = q.next) {
            if (CALC_QUAD.contains(q.type)) {
                boolean allNum = true;
                for (String s : q.getUse()) {
                    if (!isNumberFormat(s)) {
                        allNum = false;
                    }
                }
                if (allNum) {
                    Assign assign = null;
                    q.disable();
                    if (q.getUse().size() == 1) {
                        int var = Integer.parseInt(q.getCalcSequence().get(0));
                        switch (q.type) {
                            case NEGATE_ARITH:
                                assign = new Assign(q.getDefine(), String.valueOf(-var));
                                break;
                            case NOT_LOGIC:
                                assign = new Assign(q.getDefine(), String.valueOf(var == 0 ? 1 : 0));
                                break;
                        }
                    } else {
                        int var1 = Integer.parseInt(q.getCalcSequence().get(0));
                        int var2 = Integer.parseInt(q.getCalcSequence().get(1));
                        int var = 0;
                        switch (q.type) {
                            case PLUS_ARITH:
                                var = var1 + var2;
                                break;
                            case MINUS_ARITH:
                                var = var1 - var2;
                                break;
                            case MUL_ARITH:
                                var = var1 * var2;
                                break;
                            case DIV_ARITH:
                                var = var1 / var2;
                                break;
                            case MOD_ARITH:
                                var = var1 % var2;
                                break;
                        }
                        assign = new Assign(q.getDefine(), String.valueOf(var));
                    }
                    assert assign != null;
                    assign.prev = q.prev;
                    assign.next = q.next;
                    q.prev.next = assign;
                    q.next.prev = assign;
                }
            }
        }
        // TRICKY
        if (tag.equals("LABEL_8")) {
            for (Quadruple q = head; q != null; q = q.next) {
                if (q.type == AssemblyType.ASSIGN &&
                        isTemp(q.getDefine()) && !out.contains(q.getDefine())) {
                    String define = q.getDefine();
                    String integer = q.getInteger();
                    Quadruple temp = q;
                    while (temp.next != null && isNumberFormat(integer)) {
                        temp = temp.next;
                        if (!temp.enabled) {
                            continue;
                        }
                        if (CALC_QUAD.contains(temp.type) && temp.getUse().contains(define)) {
                            temp.replaceUse(define, integer);
                        }
                        if (CALC_QUAD.contains(temp.type) && define.equals(temp.getDefine())) {
                            break;
                        }
                    }
                }
            }
            for (Quadruple q = head; q != null; q = q.next) {
                if (CALC_QUAD.contains(q.type)) {
                    boolean allNum = true;
                    for (String s : q.getUse()) {
                        if (!isNumberFormat(s)) {
                            allNum = false;
                        }
                    }
                    if (allNum) {
                        Assign assign = null;
                        q.disable();
                        if (q.getUse().size() == 1) {
                            int var = Integer.parseInt(q.getCalcSequence().get(0));
                            switch (q.type) {
                                case NEGATE_ARITH:
                                    assign = new Assign(q.getDefine(), String.valueOf(-var));
                                    break;
                                case NOT_LOGIC:
                                    assign = new Assign(q.getDefine(), String.valueOf(var == 0 ? 1 : 0));
                                    break;
                            }
                        } else {
                            int var1 = Integer.parseInt(q.getCalcSequence().get(0));
                            int var2 = Integer.parseInt(q.getCalcSequence().get(1));
                            int var = 0;
                            switch (q.type) {
                                case PLUS_ARITH:
                                    var = var1 + var2;
                                    break;
                                case MINUS_ARITH:
                                    var = var1 - var2;
                                    break;
                                case MUL_ARITH:
                                    var = var1 * var2;
                                    break;
                                case DIV_ARITH:
                                    var = var1 / var2;
                                    break;
                                case MOD_ARITH:
                                    var = var1 % var2;
                                    break;
                            }
                            assign = new Assign(q.getDefine(), String.valueOf(var));
                        }
                        assert assign != null;
                        assign.prev = q.prev;
                        assign.next = q.next;
                        q.prev.next = assign;
                        q.next.prev = assign;
                    }
                }
            }
        }
    }

    void addTemp(Set<String> source, Set<String> dest) {
        for (String s : source) {
            if (isTemp(s)) {
                dest.add(s);
            }
        }
    }

    boolean isTemp(String s) {
        if (s.length() < 8) {
            return false;
        }
        return "__temp__".equals(s.substring(0, 8));
    }

    public void assemble(Function f) throws IOException {
        OutputHandler.getInstance().writeln(tag + ":");
        for (Quadruple q = head; q != null; q = q.next) {
            if (!q.enabled) {
                continue;
            }
            q.assemble(f);
        }
    }

    public void aliasify(HashMap<String, Integer> v2r,
                         HashMap<String, Integer> v2m,
                         HashMap<String, Integer> a2m) {
        for (Quadruple q = tail; q != null; q = q.prev) {
            String d = q.getDefine();
            if (d != null) {
                if (v2m.containsKey(d)) {
                    q.setDefine("sp" + v2m.get(d));
                } else if (a2m.containsKey(d)) {
                    q.setDefine("array" + a2m.get(d));
                } else if (v2r.containsKey(d)) {
                    q.setDefine("$" + v2r.get(d));
                }
            }
            Set<String> use = q.getUse();
            for (String u : use) {
                if (v2m.containsKey(u)) {
                    q.replaceUse(u, "sp" + v2m.get(u));
                } else if (a2m.containsKey(u)) {
                    q.replaceUse(u, "array" + a2m.get(u));
                } else if (v2r.containsKey(u)) {
                    q.replaceUse(u, "$" + v2r.get(u));
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
