package nodes.statement;

import lexer.Token;
import nodes.Node;

public class TokenNode extends Node {
    public String content;

    public TokenNode(Token token) {
        this.set(token.type, token.line);
        content = token.content;
    }
}
