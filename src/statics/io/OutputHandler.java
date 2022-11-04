package statics.io;

import lexer.Token;
import statics.exception.CompException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import static statics.io.OutputHandler.IOConfig.PARSER;
import static statics.io.OutputHandler.IOConfig.TOKEN;

public class OutputHandler {
    public enum IOConfig {
        MIPS, ERROR, TOKEN, PARSER
    }

    public static final IOConfig CONFIG = IOConfig.MIPS;
    public static final boolean PRINT_TOKEN = CONFIG == TOKEN;
    public static final boolean PRINT_PARSER = CONFIG == PARSER;
    public static final boolean PRINT_ERROR = CONFIG == IOConfig.ERROR;
    public static final boolean PRINT_MIPS = CONFIG == IOConfig.MIPS;
    public static final boolean DEBUG = false;

    private static final OutputHandler INSTANCE;

    static {
        try {
            INSTANCE = new OutputHandler();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final BufferedWriter OUTPUT;

    static {
        try {
            OUTPUT = new BufferedWriter(new FileWriter("output.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final BufferedWriter MIPS;

    static {
        try {
            MIPS = new BufferedWriter(new FileWriter("mips.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final BufferedWriter ERROR;

    static {
        try {
            ERROR = new BufferedWriter(new FileWriter("error.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private OutputHandler() throws IOException {
    }

    public static OutputHandler getInstance() {
        return INSTANCE;
    }

    public void write(String s) throws IOException {
        if (PRINT_PARSER) {
            OUTPUT.write(s + "");
            OUTPUT.flush();
        }
        if (PRINT_MIPS) {
            MIPS.write(s + "");
            MIPS.flush();
        }
    }

    public void writeln(String s) throws IOException {
        if (PRINT_PARSER) {
            OUTPUT.write(s + "\n");
            OUTPUT.flush();
        }
        if (PRINT_MIPS) {
            MIPS.write(s + "\n");
            MIPS.flush();
        }
    }

    public void writeln(Token token) throws IOException {
        if (PRINT_TOKEN || PRINT_PARSER) {
            OUTPUT.write(token.type + " " + token.content + "\n");
            OUTPUT.flush();
        }
    }

    public void writeln(CompException ce) throws IOException {
        if (PRINT_ERROR) {
            ERROR.write(ce.line + " " + ce.code);
            ERROR.flush();
        }
    }

    public void debug(String s) throws IOException {
        if (DEBUG) {
            System.out.println(s);
        }
    }
}