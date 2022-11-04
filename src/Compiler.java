import lexer.Lexer;
import lexer.Tokens;
import parser.Parser;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;
import statics.io.InputHandler;

public class Compiler {
    public static void main(String[] args) throws Exception {
        InputHandler.initiate("testfile.txt");
        Lexer lexer = new Lexer();
        lexer.tokenize();
        lexer.printTokens();
        Parser parser = new Parser((new Tokens(lexer.tokens)));
        parser.compileUnit();
//        var root = parser.getBuilder().getRoot();
//        root.check(new ExcCheckInfo(), new ExcCheckRes());
    }
}
