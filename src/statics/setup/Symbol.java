package statics.setup;

import cfg.quad.calc.Multiply;
import cfg.quad.calc.Plus;
import cfg.quad.func.Assign;
import statics.exception.CompException;
import statics.io.OutputHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static cfg.CFGBuilder.CFG_BUILDER;
import static cfg.quad.QuadUtil.genIRVar;

public class Symbol {
    public static final Symbol SYMBOL = new Symbol();
    private final HashMap<String, LValSymbol> globalVars = new HashMap<>();
    private final HashMap<String, LValSymbol> constVars = new HashMap<>();
    private final HashMap<String, String> constStrings = new HashMap<>();
    private final HashMap<String, FuncSymbol> funcs = new HashMap<>();
    private final HashMap<String, LinkedList<LValSymbol>> vars = new HashMap<>();
    private final LinkedList<HashSet<String>> symbolStack = new LinkedList<>();
    private int cnt;

    public Symbol() {
        cnt = 0;
    }

    public void clear() {
        funcs.clear();
        vars.clear();
        globalVars.clear();
        constVars.clear();
        constStrings.clear();
        cnt = 0;
    }

    public boolean addFunc(List<Integer> params, String name, boolean isVoid) {
        if (funcs.containsKey(name)) {
            return false;
        }
        funcs.put(name, new FuncSymbol(params, name, isVoid));
        return true;
    }

    public int addVar(boolean isConst, String name,
                      List<Integer> dims, List<Integer> initVals) {
        HashSet<String> cur = symbolStack.getLast();
        if (cur.contains(name)) {
            return 0;
        }
        int r = ++cnt;
        LValSymbol lvs = new LValSymbol(isConst, symbolStack.size() == 1, r, dims, initVals);
        if (symbolStack.size() == 1) {
            globalVars.put(genIRVar(name, r), lvs);
        }
        cur.add(name);
        if (!vars.containsKey(name)) {
            vars.put(name, new LinkedList<>());
        }
        vars.get(name).add(lvs);
        return r;
    }

    public LValSymbol getVar(String name) {
        if (!vars.containsKey(name)) {
            return null;
        }
        return vars.get(name).getLast();
    }

    public FuncSymbol getFunc(String name) {
        if (!funcs.containsKey(name)) {
            return null;
        }
        return funcs.get(name);
    }

    public void startBlock() {
        symbolStack.offer(new HashSet<>());
    }

    public void finishBlock() {
        HashSet<String> cur = symbolStack.getLast();
        for (String s : cur) {
            if (vars.get(s).getLast().isConst) {
                int id = vars.get(s).getLast().id;
                constVars.put(genIRVar(s, id), vars.get(s).getLast());
            }
            vars.get(s).removeLast();
            if (vars.get(s).isEmpty()) {
                vars.remove(s);
            }
        }
        symbolStack.removeLast();
    }

    public void printGlobalVars() throws IOException { // NO IR INCLUDED
        for (var v : globalVars.entrySet()) {
            OutputHandler.getInstance().write(v.getKey() + " ");
            v.getValue().print();
        }
        for (var v : constVars.entrySet()) {
            OutputHandler.getInstance().write(v.getKey() + " ");
            v.getValue().print();
        }
        for (var v : constStrings.entrySet()) {
            OutputHandler.getInstance()
                    .writeln(v.getKey() + ": .asciiz \"" + v.getValue() + "\"");
        }
    }

    public int getStackSize() {
        return symbolStack.size();
    }

//    public void addConstString(String str) {
//        constStrings.put(genIRStr(), str);
//    }

    public void addConstString(String l, String str) {
        constStrings.put(l, str);
    }

    public static class LValSymbol {
        public boolean isConst, isGlobal;
        public int id;
        public List<Integer> dims;
        public List<Integer> initVals;

        public LValSymbol(boolean isConst, boolean isGlobal, int id,
                          List<Integer> dims, List<Integer> initVals) {
            this.isConst = isConst;
            this.isGlobal = isGlobal;
            this.id = id;
            this.dims = dims;
            this.initVals = initVals;
        }

        public String getOffsetInMIPS(List<String> indices) {
            var r = CFG_BUILDER.tempVar();
            String b = CFG_BUILDER.tempVar();
            String t = "1";
            CFG_BUILDER.insert(new Assign(b, t));
            for (int i = dims.size() - 1; i >= indices.size(); i--) {
                t = dims.get(i).toString();
                CFG_BUILDER.insert(new Multiply(b, b, t));
            }
            t = "0";
            CFG_BUILDER.insert(new Assign(r, t));
            for (int i = indices.size() - 1; i >= 0; i--) {
                t = CFG_BUILDER.tempVar();
                CFG_BUILDER.insert(new Multiply(t, indices.get(i), b));
                CFG_BUILDER.insert(new Plus(r, r, t));
                t = dims.get(i).toString();
                CFG_BUILDER.insert(new Multiply(b, b, t));
            }
            return r;
        }

        private int getOffset(List<Integer> indices) {
            if (indices.isEmpty()) {
                return 0;
            }
            int base = 1, ret = 0;
            for (int i = dims.size() - 1; i >= indices.size(); i--) {
                base *= dims.get(i);
            }
            for (int i = indices.size() - 1; i >= 0; i--) {
                ret += base * indices.get(i);
                base *= dims.get(i);
            }
            return ret;
        }

        public int getVal(List<Integer> indices) {
            int offset = getOffset(indices);
            if (initVals.isEmpty()) {
                return 0;
            }
            if (offset >= initVals.size()) {
                System.out.println("Index out of bound");
                return -1;
            }
            return initVals.get(offset);
        }

        public void print() throws IOException {
            int s = 1;
            for (int d : dims) {
                s *= d;
            }
            OutputHandler.getInstance().write(" : .word ");
            if (initVals.isEmpty()) {
                OutputHandler.getInstance().writeln("0: " + s);
            } else {
                for (int i : initVals) {
                    OutputHandler.getInstance().write(i + " ");
                }
                OutputHandler.getInstance().writeln("");
            }
        }
    }

    public static class FuncSymbol {
        public List<Integer> dims;// dimensions of parameters
        public String name;
        public boolean isVoid;

        public FuncSymbol(List<Integer> dims, String name, boolean isVoid) {
            this.dims = dims;
            this.name = name;
            this.isVoid = isVoid;
        }

        public CompException.Exception matchParams(List<Integer> args) {
            if (args.size() != dims.size()) {
                return CompException.ExcDesc.UNMATCHED_PARAM_NUM.toCode();
            }
            for (int i = 0; i < dims.size(); i++) {
                if (!Objects.equals(dims.get(i), args.get(i))) {
                    return CompException.ExcDesc.UNMATCHED_PARAM_TYPE.toCode();
                }
            }
            return CompException.ExcDesc.CORRECT.toCode();
        }
    }
}
