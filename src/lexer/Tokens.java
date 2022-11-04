package lexer;

import statics.io.OutputHandler;

import java.io.IOException;
import java.util.List;

public class Tokens {
    private final List<Token> tokens;
    private int p;

    public Tokens(List<Token> t) {
        tokens = t;
        p = 0;
    }

    public Token curToken() {
        return tokens.get(p);
    }

    public Token nextNToken(int n) {
        if (p + n >= tokens.size()) {
            return null;
        }
        return tokens.get(p + n);
    }

    public int prevLine() {
        if (p == 0) {
            return 0;
        }
        return nextNToken(-1).line;
    }

    public void bump() {
        p++;
    }

    public void printCurToken() throws IOException {
        OutputHandler.getInstance().writeln(tokens.get(p));
    }
}
