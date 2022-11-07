package nodes;

import statics.assembly.AssemblyInfo;
import statics.assembly.AssemblyRes;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;
import statics.setup.SyntaxType;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

public abstract class Node {
    public SyntaxType type;
    int startLine;
    int finishLine;
    LinkedList<Node> children;

    public Node() {
        children = new LinkedList<>();
    }

    public void printTree() {
        if (children.isEmpty()) {
            System.out.println(type);
            return;
        }
        Iterator<Node> iter = children.descendingIterator();
        while (iter.hasNext()) {
            iter.next().printTree();
        }
        System.out.println(type);
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
        for (var node : children) {
        }
    }

    public void check(ExcCheckInfo info, ExcCheckRes res) {
    }

    public void assemble(AssemblyInfo info, AssemblyRes res) throws IOException {
    }
}
