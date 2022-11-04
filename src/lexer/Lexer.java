package lexer;

import statics.exception.CompException;
import statics.io.OutputHandler;
import statics.setup.SyntaxType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static statics.exception.Errors.errors;
import static statics.setup.SyntaxType.BCOMMENT;
import static statics.setup.SyntaxType.LCOMMENT;

public class Lexer {
    public final List<Token> tokens = new ArrayList<>();
    private final Cursor cursor;

    public Lexer() {
        cursor = new Cursor();
    }

    public void printTokens() throws IOException {
        for (Token token : tokens) {
            if (token.type != BCOMMENT && token.type != SyntaxType.LCOMMENT) {
                OutputHandler.getInstance().writeln(token);
                OutputHandler.getInstance().debug(token.type + " " + token.content);
            }
        }
    }

    public void tokenize() {
        Token token = nextToken();
        while (!token.isEOF()) {
            if (token.isError()) {
                continue;
            } else if (token.type != BCOMMENT && token.type != LCOMMENT) {
                tokens.add(token);
            }
            token = nextToken();
        }
        tokens.add(token);
    }

    private Token nextToken() {
        cursor.moveWhileSpace();
        int start = cursor.getPosition();
        SyntaxType type = getTokenType();
        int len = cursor.getPosition() - start;
        return new Token(type, start, len);
    }

    private SyntaxType getTokenType() {
        char c = cursor.cur();
        if (c == (char) -1) {
            return SyntaxType.EOF;
        }
        cursor.move();
        switch (c) {
            case '/':
                if (cursor.cur() == '/') {
                    consumeLineComment();
                    return SyntaxType.LCOMMENT;
                }
                if (cursor.cur() == '*') {
                    consumeBlockComment();
                    return BCOMMENT;
                }
                return SyntaxType.DIV;
            case ',':
                return SyntaxType.COMMA;
            case ';':
                return SyntaxType.SEMICN;
            case '+':
                return SyntaxType.PLUS;
            case '-':
                return SyntaxType.MINU;
            case '%':
                return SyntaxType.MOD;
            case '*':
                return SyntaxType.MULT;
            case '(':
                return SyntaxType.LPARENT;
            case ')':
                return SyntaxType.RPARENT;
            case '[':
                return SyntaxType.LBRACK;
            case ']':
                return SyntaxType.RBRACK;
            case '{':
                return SyntaxType.LBRACE;
            case '}':
                return SyntaxType.RBRACE;
            case '!':
                if (cursor.cur() == '=') {
                    cursor.move();
                    return SyntaxType.NEQ;
                }
                return SyntaxType.NOT;
            case '=':
                if (cursor.cur() == '=') {
                    cursor.move();
                    return SyntaxType.EQL;
                }
                return SyntaxType.ASSIGN;
            case '<':
                if (cursor.cur() == '=') {
                    cursor.move();
                    return SyntaxType.LEQ;
                }
                return SyntaxType.LSS;
            case '>':
                if (cursor.cur() == '=') {
                    cursor.move();
                    return SyntaxType.GEQ;
                }
                return SyntaxType.GRE;
            case '&':
                if (cursor.cur() == '&') {
                    cursor.move();
                    return SyntaxType.AND;
                }
                return SyntaxType.ERR;
            case '|':
                if (cursor.cur() == '|') {
                    cursor.move();
                    return SyntaxType.OR;
                }
                return SyntaxType.ERR;
            case '"':
                consumeString();
                return SyntaxType.STRCON;
            default:
                if (Character.isDigit(c)) {
                    cursor.moveWhileDigit();
                    return SyntaxType.INTCON;
                }
                if (Character.isAlphabetic(c) || c == '_') {
                    StringBuilder id = new StringBuilder();
                    id.append(c);
                    while (Character.isDigit(cursor.cur()) ||
                            Character.isAlphabetic(cursor.cur()) || cursor.cur() == '_') {
                        id.append(cursor.cur());
                        cursor.move();
                    }
                    return SyntaxType.checkKeyWords(id.toString());
                }
                return SyntaxType.ERR;
        }
    }

    private void consumeString() {
        boolean afterBackSlash = false;
        while (true) {
            char c = cursor.cur();
            switch (c) {
                case (char) -1:
                    return;
                case '"':
                    cursor.move();
                    if (afterBackSlash) {
                        afterBackSlash = false;
                    } else {
                        return;
                    }
                    break;
                case '\\':
                    if (cursor.nextNthChar(1) != 'n') {
                        errors.add(new CompException(CompException.Exception.a, cursor.getLine()));
                    }
                    cursor.move();
                    afterBackSlash = !afterBackSlash;
                    break;
                case '%':
                    if (cursor.nextNthChar(1) != 'd') {
                        errors.add(new CompException(CompException.Exception.a, cursor.getLine()));
                    }
                    cursor.move();
                    break;
                default:
                    int cint = c;
                    if (!(cint == 32 || cint == 33 || (cint >= 40 && cint <= 126))) {
                        errors.add(new CompException(CompException.Exception.a, cursor.getLine()));
                    }
                    cursor.move();
                    afterBackSlash = false;
            }
        }
    }

    private void consumeLineComment() {
        cursor.moveWhileLine();
        cursor.move();
    }

    private void consumeBlockComment() {
        cursor.move();
        cursor.move();
        cursor.moveWhileBlock();
        while (cursor.previous() != '*' && cursor.cur() != (char) -1) {
            cursor.move();
            cursor.moveWhileBlock();
        }
        cursor.move();
    }
}
