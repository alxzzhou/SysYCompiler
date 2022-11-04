package lexer;

import static statics.io.InputHandler.CODE;
import static statics.io.InputHandler.textCnt;

public class Cursor {
    private final int CODE_LEN = CODE.length();
    private int pos;

    public Cursor() {
        pos = 0;
    }

    public char previous() {
        return (pos == 0) ? (char) -1 : CODE.charAt(pos - 1);
    }

    public char cur() {
        return reachEnd() ? (char) -1 : CODE.charAt(pos);
    }

    public char nextNthChar(int n) {
        return (pos + n >= CODE_LEN) ? (char) -1 : CODE.charAt(pos + n);
    }

    public boolean reachEnd() {
        return pos == CODE_LEN - 1;
    }

    public void move() {
        pos++;
    }

    public void moveWhileSpace() {
        while (!reachEnd() && " \n\t".contains(String.valueOf(CODE.charAt(pos)))) {
            move();
        }
    }

    public void moveWhileDigit() {
        while (!reachEnd() && Character.isDigit(CODE.charAt(pos))) {
            move();
        }
    }

    public void moveWhileLine() {
        while (!reachEnd() && CODE.charAt(pos) != '\n') {
            move();
        }
    }

    public void moveWhileBlock() {
        while (!reachEnd() && CODE.charAt(pos) != '/') {
            move();
        }
    }

    public int getPosition() {
        return pos;
    }

    public int getLine() {
        int r = 0;
        while (textCnt.get(r) <= pos) {
            r++;
        }
        return r;
    }
}
