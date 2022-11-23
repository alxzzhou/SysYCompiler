package parser;

import lexer.Token;
import nodes.ErrorNode;
import nodes.Node;
import nodes.statement.TokenNode;
import statics.exception.CompException;
import statics.io.OutputHandler;
import statics.setup.Pair;
import statics.setup.SyntaxType;

import java.util.LinkedList;

import static statics.exception.CompException.ExcDesc.UNDEFINED;

public class TreeBuilder {
    private final LinkedList<Pair<SyntaxType, Integer>> parents;
    private final LinkedList<Node> children;

    public TreeBuilder() {
        parents = new LinkedList<>();
        children = new LinkedList<>();
    }

    public void initNode(SyntaxType t) {
        parents.addLast(new Pair<>(t, children.size()));
        OutputHandler.getInstance().debug("START NODE: " + t + children.size());
    }

    public void initNodeAt(SyntaxType t, int n) {
        parents.offer(new Pair<>(t, n));
        OutputHandler.getInstance().debug("START NODE: " + t + n);
    }

    public void parseNode(Node node) {
        Pair<SyntaxType, Integer> p = parents.removeLast();
        while (children.size() > p.second) {
            node.addChild(children.removeLast());
        }
        node.set(p.first);
        children.offer(node);
    }

    public void terminate(Token token) {
        Node n = new TokenNode(token);
        children.offer(n);
    }

    public void error(CompException ce) {
        Node n = new ErrorNode(ce);
        n.set(SyntaxType.ERR, ce.line);
        if (ce.code != UNDEFINED.toCode()) {
            children.offer(n);
            return;
        }
        Pair<SyntaxType, Integer> parent = parents.removeLast();
        while (children.size() > parent.second) {
            n.addChild(children.removeLast());
        }
        children.offer(n);
    }

    public int getChildrenSize() {
        return children.size();
    }

    public Node getRoot() {
        return children.getLast();
    }
}
