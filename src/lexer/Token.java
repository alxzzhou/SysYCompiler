package lexer;

import statics.setup.SyntaxType;

import static statics.io.InputHandler.CODE;
import static statics.io.InputHandler.textCnt;

public class Token {
    public SyntaxType type;
    public String content;
    public int line;

    public Token(SyntaxType t, int start, int len) {
        content = CODE.substring(start, start + len);
        line = getLine(start);
        type = t;
    }

    int getLine(int pos) {
        int r = 0;
        while (textCnt.get(r) <= pos) {
            r++;
        }
        return r;
    }

    boolean isEOF() {
        return type == SyntaxType.EOF;
    }

    boolean isError() {
        return type == SyntaxType.ERR;
    }

    void printDebug() {
        System.out.println(type + " " + content);
    }
}
