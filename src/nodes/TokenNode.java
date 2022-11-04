package nodes;

import lexer.Token;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;

public class TokenNode extends Node {
    public String content;

    public TokenNode(Token token) {
        this.set(token.type, token.line);
        content = token.content;
    }

    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {

    }
}
