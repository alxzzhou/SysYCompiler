import lexer.Lexer;
import lexer.Tokens;
import parser.Parser;
import statics.exception.CompException;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;
import statics.io.InputHandler;
import statics.io.OutputHandler;

import static statics.exception.Errors.errors;

public class Compiler {
    public static void main(String[] args) throws Exception {
        InputHandler.initiate("testfile.txt");
        Lexer lexer = new Lexer();
        lexer.tokenize();
        //lexer.printTokens();
        Parser parser = new Parser((new Tokens(lexer.tokens)));
        parser.compileUnit();
        nodes.Node root = parser.getBuilder().getRoot();
        root.check(new ExcCheckInfo(), new ExcCheckRes());
        for (CompException ce : errors) {
            OutputHandler.getInstance().writeln(ce);
        }
        //root.assemble(new AssemblyInfo(), new AssemblyRes());
    }
}
