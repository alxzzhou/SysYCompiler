package parser;

import lexer.Tokens;
import nodes.AddExprNode;
import nodes.AssignStatementNode;
import nodes.BlockNode;
import nodes.BreakNode;
import nodes.CompUnitNode;
import nodes.ConstDeclNode;
import nodes.ConstDefNode;
import nodes.ConstInitValNode;
import nodes.ContinueNode;
import nodes.EqExprNode;
import nodes.ExprNode;
import nodes.ExprStatementNode;
import nodes.FuncArgsNode;
import nodes.FuncDefNode;
import nodes.FuncExprNode;
import nodes.FuncParamNode;
import nodes.FuncParamsNode;
import nodes.FuncTypeNode;
import nodes.IfNode;
import nodes.InitValNode;
import nodes.LAndExprNode;
import nodes.LOrExprNode;
import nodes.LValNode;
import nodes.MulExprNode;
import nodes.NumberNode;
import nodes.PrimaryExprNode;
import nodes.PrintfNode;
import nodes.RelExprNode;
import nodes.ReturnNode;
import nodes.StatementNode;
import nodes.UnaryExprNode;
import nodes.UnaryPrefixOpNode;
import nodes.VarDeclNode;
import nodes.VarDefNode;
import nodes.WhileNode;
import statics.exception.CompException;
import statics.io.OutputHandler;
import statics.setup.SyntaxType;

import java.io.IOException;

import static statics.exception.CompException.ExcDesc.LACK_BRACKET;
import static statics.exception.CompException.ExcDesc.LACK_PARENT;
import static statics.exception.CompException.ExcDesc.LACK_SEMICN;
import static statics.exception.CompException.ExcDesc.UNDEFINED;
import static statics.setup.SyntaxType.*;

public class Parser {
    private final Tokens tokens;
    private final TreeBuilder builder;

    public Parser(Tokens t) {
        tokens = t;
        builder = new TreeBuilder();
    }

    public TreeBuilder getBuilder() {
        return builder;
    }

    void next() throws IOException {
        tokens.printCurToken();
        tokens.bump();
    }

    void check(SyntaxType t, CompException.Exception e) throws IOException {
        if (tokens.curToken().type != t) {
            error(e);
        }
        terminate();
    }

    void terminate() throws IOException {
        builder.terminate(tokens.curToken());
        //OutputHandler.getInstance().writeln(tokens.curToken());
        tokens.bump();
    }

    void error(CompException.Exception e) {
        builder.error(new CompException(e, tokens.prevLine()));
    }

    public void compileUnit() throws IOException {
        SyntaxType type = tokens.curToken().type;
        builder.initNode(COMP_UNIT);
        while (type != EOF) {
            if (type == VOIDTK) {
                funcDef();
            } else if (type == CONSTTK) {
                constDecl();
            } else if (type == INTTK) {
                if (tokens.nextNToken(2).type == LPARENT) {
                    funcDef();
                } else {
                    varDecl();
                }
            } else {
                error(UNDEFINED.toCode());
                return;
            }
            type = tokens.curToken().type;
        }
        builder.parseNode(new CompUnitNode());
        OutputHandler.getInstance().writeln("<CompUnit>");
    }

    void funcDef() throws IOException {
        builder.initNode(FUNC_DEF);
        funcType();
        SyntaxType type = tokens.curToken().type;
        if (type != IDENFR && type != MAINTK) {
            error(UNDEFINED.toCode());
            return;
        }
        terminate();
        check(LPARENT, UNDEFINED.toCode());
        while (tokens.curToken().type == INTTK) {
            funcFParams();
        }
        check(RPARENT, LACK_PARENT.toCode());
        block();
        builder.parseNode(new FuncDefNode());
        OutputHandler.getInstance().writeln((type == MAINTK) ? "<MainFuncDef>" : "<FuncDef>");
    }

    void funcType() throws IOException {
        builder.initNode(FUNC_TYPE);
        SyntaxType type = tokens.curToken().type;
        if (type != VOIDTK && type != INTTK) {
            error(UNDEFINED.toCode());
            return;
        }
        terminate();
        builder.parseNode(new FuncTypeNode());
        OutputHandler.getInstance().writeln("<FuncType>");
    }

    void funcFParams() throws IOException {
        builder.initNode(FUNC_PARAMS);
        funcFParam();
        while (tokens.curToken().type == COMMA) {
            terminate();
            funcFParam();
        }
        builder.parseNode(new FuncParamsNode());
        OutputHandler.getInstance().writeln("<FuncFParams>");
    }

    void funcFParam() throws IOException {
        builder.initNode(FUNC_PARAM);
        check(INTTK, UNDEFINED.toCode());
        check(IDENFR, UNDEFINED.toCode());
        if (tokens.curToken().type == LBRACK) {
            terminate();
            check(RBRACK, LACK_BRACKET.toCode());
            while (tokens.curToken().type == LBRACK) {
                terminate();
                constExpr();
                check(RBRACK, LACK_BRACKET.toCode());
            }
        }
        builder.parseNode(new FuncParamNode());
        OutputHandler.getInstance().writeln("<FuncFParam>");
    }

    void constExpr() throws IOException {
        addExpr();
        OutputHandler.getInstance().writeln("<ConstExp>");
    }

    void block() throws IOException {
        builder.initNode(BLOCK);
        check(LBRACE, UNDEFINED.toCode());
        while (true) {
            SyntaxType type = tokens.curToken().type;
            if (type == CONSTTK || type == INTTK) {
                decl();
            } else if (STMT_PREFIX.contains(type)) {
                statement();
            } else {
                break;
            }
        }
        check(RBRACE, UNDEFINED.toCode());
        builder.parseNode(new BlockNode());
        OutputHandler.getInstance().writeln("<Block>");
    }

    void decl() throws IOException {
        SyntaxType t = tokens.curToken().type;
        if (t == CONSTTK) {
            constDecl();
        } else {
            varDecl();
        }
    }

    void statement() throws IOException {
        builder.initNode(STMT);
        SyntaxType type = tokens.curToken().type;
        /*if (type == IDENFR) {
            int i = 1;
            boolean lval = false;
            while (tokens.nextNToken(i).type != SEMICN) {
                if (tokens.nextNToken(i).type == ASSIGN) {
                    lval = true;
                    break;
                }
                i++;
            }
            if (lval) {
                lval();
                next();
                if (tokens.curToken().type == GETINTTK) {
                    next();
                    next();
                    next();
                } else {
                    expr();
                }
            } else {
                expr();
            }
            // TODO: check SEMICN
            next();
        } else if (EXPR_PREFIX.contains(type)) {
            expr();
            // TODO: check SEMICN
            next();}*/
        if (EXPR_PREFIX.contains(type)) {
            int p = builder.getChildrenSize();
            int i = 1;
            boolean lval = false;
            while (tokens.nextNToken(i).type != SEMICN && i <= 3) {
                if (tokens.nextNToken(i).type == ASSIGN) {
                    lval = true;
                    break;
                }
                i++;
            }
            if (lval) {
                lval();
            } else {
                expr();
            }
            type = tokens.curToken().type;
            if (type == ASSIGN) {
                assignStatement(p);
            } else {
                exprStatement(p);
            }
        } else if (type == SEMICN) {
            builder.initNode(EXPR_STMT);
            terminate();
            builder.parseNode(new ExprStatementNode());
        } else if (type == LBRACE) {
            block();
        } else if (type == IFTK) {
            ifStatement();
        } else if (type == WHILETK) {
            whileStatement();
        } else if (type == BREAKTK) {
            breakStatement();
        } else if (type == CONTINUETK) {
            continueStatement();
        } else if (type == RETURNTK) {
            returnStatement();
        } else if (type == PRINTFTK) {
            printfStatement();
        } else {
            error(UNDEFINED.toCode());
            return;
        }
        builder.parseNode(new StatementNode());
        OutputHandler.getInstance().writeln("<Stmt>");
    }

    private void exprStatement(int p) throws IOException {
        builder.initNodeAt(EXPR_STMT, p);
        check(SEMICN, LACK_SEMICN.toCode());
        builder.parseNode(new ExprStatementNode());
    }

    private void assignStatement(int p) throws IOException {
        builder.initNodeAt(ASSIGN_STMT, p);
        terminate();
        SyntaxType type = tokens.curToken().type;
        if (type == GETINTTK) {
            terminate();
            check(LPARENT, UNDEFINED.toCode());
            check(RPARENT, LACK_PARENT.toCode());
        } else if (EXPR_PREFIX.contains(type)) {
            expr();
        } else {
            error(UNDEFINED.toCode());
            return;
        }
        check(SEMICN, LACK_SEMICN.toCode());
        builder.parseNode(new AssignStatementNode());
    }

    void printfStatement() throws IOException {
        builder.initNode(PRINTF_STMT);
        check(PRINTFTK, UNDEFINED.toCode());
        check(LPARENT, UNDEFINED.toCode());
        check(STRCON, UNDEFINED.toCode());
        while (tokens.curToken().type == COMMA) {
            terminate();
            expr();
        }
        check(RPARENT, LACK_PARENT.toCode());
        check(SEMICN, LACK_SEMICN.toCode());
        builder.parseNode(new PrintfNode());
    }

    void returnStatement() throws IOException {
        builder.initNode(RETURN_STMT);
        check(RETURNTK, UNDEFINED.toCode());
        if (EXPR_PREFIX.contains(tokens.curToken().type)) {
            expr();
        }
        check(SEMICN, LACK_SEMICN.toCode());
        builder.parseNode(new ReturnNode());
    }

    void continueStatement() throws IOException {
        builder.initNode(CONTINUE_STMT);
        check(CONTINUETK, UNDEFINED.toCode());
        check(SEMICN, LACK_SEMICN.toCode());
        builder.parseNode(new ContinueNode());
    }

    void breakStatement() throws IOException {
        builder.initNode(BREAK_STMT);
        check(BREAKTK, UNDEFINED.toCode());
        check(SEMICN, LACK_SEMICN.toCode());
        builder.parseNode(new BreakNode());
    }

    void ifStatement() throws IOException {
        builder.initNode(IF_STMT);
        check(IFTK, UNDEFINED.toCode());
        check(LPARENT, UNDEFINED.toCode());
        cond();
        check(RPARENT, LACK_PARENT.toCode());
        statement();
        if (tokens.curToken().type == ELSETK) {
            terminate();
            statement();
        }
        builder.parseNode(new IfNode());
    }

    void cond() throws IOException {
        lOrExpr();
        OutputHandler.getInstance().writeln("<Cond>");
    }

    void lOrExpr() throws IOException {
        builder.initNode(LOR_EXPR);
        lAndExpr();
        lOrExpr_();
        builder.parseNode(new LOrExprNode());
        OutputHandler.getInstance().writeln("<LOrExp>");
    }

    void lOrExpr_() throws IOException {
        if (tokens.curToken().type == OR) {
            OutputHandler.getInstance().writeln("<LOrExp>");
            terminate();
            builder.initNode(LOR_EXPR);
            lAndExpr();
            lOrExpr_();
            builder.parseNode(new LOrExprNode());
        }
    }

    void lAndExpr() throws IOException {
        builder.initNode(LAND_EXPR);
        eqExpr();
        lAndExpr_();
        builder.parseNode(new LAndExprNode());
        OutputHandler.getInstance().writeln("<LAndExp>");
    }

    void lAndExpr_() throws IOException {
        if (tokens.curToken().type == AND) {
            OutputHandler.getInstance().writeln("<LAndExp>");
            terminate();
            builder.initNode(LAND_EXPR);
            eqExpr();
            lAndExpr_();
            builder.parseNode(new LAndExprNode());
        }
    }

    void eqExpr() throws IOException {
        builder.initNode(EQ_EXPR);
        relExpr();
        eqExpr_();
        builder.parseNode(new EqExprNode());
        OutputHandler.getInstance().writeln("<EqExp>");
    }

    void eqExpr_() throws IOException {
        if (tokens.curToken().type == EQL || tokens.curToken().type == NEQ) {
            OutputHandler.getInstance().writeln("<EqExp>");
            terminate();
            builder.initNode(EQ_EXPR);
            relExpr();
            eqExpr_();
            builder.parseNode(new EqExprNode());
        }
    }

    void relExpr() throws IOException {
        builder.initNode(REL_EXPR);
        addExpr();
        relExpr_();
        builder.parseNode(new RelExprNode());
        OutputHandler.getInstance().writeln("<RelExp>");
    }

    void relExpr_() throws IOException {
        SyntaxType t = tokens.curToken().type;
        if (t == GRE || t == GEQ || t == LSS || t == LEQ) {
            OutputHandler.getInstance().writeln("<RelExp>");
            terminate();
            builder.initNode(REL_EXPR);
            addExpr();
            relExpr_();
            builder.parseNode(new RelExprNode());
        }
    }

    void whileStatement() throws IOException {
        builder.initNode(WHILE_STMT);
        check(WHILETK, UNDEFINED.toCode());
        check(LPARENT, UNDEFINED.toCode());
        cond();
        check(RPARENT, LACK_PARENT.toCode());
        statement();
        builder.parseNode(new WhileNode());
    }

    void expr() throws IOException {
        builder.initNode(EXPR);
        addExpr();
        builder.parseNode(new ExprNode());
        OutputHandler.getInstance().writeln("<Exp>");
    }

    void addExpr() throws IOException {
        builder.initNode(ADD_EXPR);
        mulExpr();
        addExpr_();
        builder.parseNode(new AddExprNode());
        OutputHandler.getInstance().writeln("<AddExp>");
    }

    void addExpr_() throws IOException {
        if (tokens.curToken().type == PLUS || tokens.curToken().type == MINU) {
            OutputHandler.getInstance().writeln("<AddExp>");
            terminate();
            builder.initNode(ADD_EXPR);
            mulExpr();
            addExpr_();
            builder.parseNode(new AddExprNode());
        }
    }

    void mulExpr() throws IOException {
        builder.initNode(MUL_EXPR);
        unaryExpr();
        mulExpr_();
        builder.parseNode(new MulExprNode());
        OutputHandler.getInstance().writeln("<MulExp>");
    }

    void mulExpr_() throws IOException {
        SyntaxType t = tokens.curToken().type;
        if (t == MULT || t == DIV || t == MOD) {
            OutputHandler.getInstance().writeln("<MulExp>");
            terminate();
            builder.initNode(MUL_EXPR);
            unaryExpr();
            mulExpr_();
            builder.parseNode(new MulExprNode());
        }
    }

    void unaryExpr() throws IOException {
        SyntaxType t = tokens.curToken().type;
        builder.initNode(UNARY_EXPR);
        if (t == NOT || t == PLUS || t == MINU) {
            unaryPrefixOp();
        }
        t = tokens.curToken().type;
        if (t == IDENFR && tokens.nextNToken(1).type == LPARENT) {
            funcExpr();
        } else if (t == IDENFR || t == INTCON || t == LPARENT) {
            primaryExpr();
        }
        builder.parseNode(new UnaryExprNode());
        OutputHandler.getInstance().writeln("<UnaryExp>");
    }

    private void unaryPrefixOp() throws IOException {
        builder.initNode(PREFIX_OP);
        var t = tokens.curToken().type;
        while (t == NOT || t == PLUS || t == MINU) {
            unaryOp();
            t = tokens.curToken().type;
        }
        builder.parseNode(new UnaryPrefixOpNode());
    }

    void unaryOp() throws IOException {
        terminate();
        OutputHandler.getInstance().writeln("<UnaryOp>");
    }

    void primaryExpr() throws IOException {
        SyntaxType t = tokens.curToken().type;
        builder.initNode(PRIMARY_EXPR);
        if (t == LPARENT) {
            terminate();
            expr();
            check(RPARENT, LACK_PARENT.toCode());
        } else if (t == IDENFR) {
            lval();
        } else {
            number();
        }
        builder.parseNode(new PrimaryExprNode());
        OutputHandler.getInstance().writeln("<PrimaryExp>");
    }

    void number() throws IOException {
        builder.initNode(NUMBER);
        check(INTCON, UNDEFINED.toCode());
        builder.parseNode(new NumberNode());
        OutputHandler.getInstance().writeln("<Number>");
    }

    void lval() throws IOException {
        builder.initNode(LVAL);
        if (tokens.curToken().type != IDENFR) {
            error(UNDEFINED.toCode());
            return;
        }
        terminate();
        while (tokens.curToken().type == LBRACK) {
            terminate();
            expr();
            check(RBRACK, LACK_BRACKET.toCode());
        }
        builder.parseNode(new LValNode());
        OutputHandler.getInstance().writeln("<LVal>");
    }

    void funcExpr() throws IOException {
        builder.initNode(FUNC_EXPR);
        terminate();
        if (tokens.curToken().type != LPARENT) {
            error(UNDEFINED.toCode());
            return;
        }
        terminate();
        if (EXPR_PREFIX.contains(tokens.curToken().type)) {
            funcArgs();
            check(RPARENT, LACK_PARENT.toCode());
        } else if (tokens.curToken().type == RPARENT) {
            terminate();
        } else {
            error(UNDEFINED.toCode());
            return;
        }
        builder.parseNode(new FuncExprNode());
    }

    void funcArgs() throws IOException {
        builder.initNode(FUNC_ARGS);
        expr();
        while (tokens.curToken().type == COMMA) {
            terminate();
            expr();
        }
        builder.parseNode(new FuncArgsNode());
        OutputHandler.getInstance().writeln("<FuncRParams>");
    }

    void initVal() throws IOException {
        builder.initNode(INIT_VAL);
        SyntaxType type = tokens.curToken().type;
        if (EXPR_PREFIX.contains(type)) {
            expr();
        } else if (type == LBRACE) {
            terminate();
            type = tokens.curToken().type;
            if (type == LBRACE || EXPR_PREFIX.contains(type)) {
                initVal();
                while (tokens.curToken().type == COMMA) {
                    terminate();
                    initVal();
                }
                check(RBRACE, UNDEFINED.toCode());
            } else if (type == RBRACE) {
                terminate();
            } else {
                error(UNDEFINED.toCode());
            }
        } else {
            error(UNDEFINED.toCode());
            return;
        }
        builder.parseNode(new InitValNode());
        OutputHandler.getInstance().writeln("<InitVal>");
    }

    void varDef() throws IOException {
        builder.initNode(VAR_DEF);
        check(IDENFR, UNDEFINED.toCode());
        while (tokens.curToken().type == LBRACK) {
            terminate();
            constExpr();
            check(RBRACK, LACK_BRACKET.toCode());
        }
        if (tokens.curToken().type == ASSIGN) {
            terminate();
            initVal();
        }
        builder.parseNode(new VarDefNode());
        OutputHandler.getInstance().writeln("<VarDef>");
    }

    void varDecl() throws IOException {
        builder.initNode(VAR_DECL);
        check(INTTK, UNDEFINED.toCode());
        varDef();
        while (tokens.curToken().type == COMMA) {
            terminate();
            varDef();
        }
        check(SEMICN, LACK_SEMICN.toCode());
        builder.parseNode(new VarDeclNode());
        OutputHandler.getInstance().writeln("<VarDecl>");
    }

    void constDecl() throws IOException {
        builder.initNode(CONST_DECL);
        terminate();
        check(INTTK, UNDEFINED.toCode());
        constDef();
        while (tokens.curToken().type == COMMA) {
            terminate();
            constDef();
        }
        check(SEMICN, LACK_SEMICN.toCode());
        builder.parseNode(new ConstDeclNode());
        OutputHandler.getInstance().writeln("<ConstDecl>");
    }

    void constDef() throws IOException {
        builder.initNode(CONST_DEF);
        check(IDENFR, UNDEFINED.toCode());
        while (tokens.curToken().type == LBRACK) {
            terminate();
            constExpr();
            check(RBRACK, LACK_BRACKET.toCode());
        }
        terminate();
        constInitVal();
        builder.parseNode(new ConstDefNode());
        OutputHandler.getInstance().writeln("<ConstDef>");
    }

    void constInitVal() throws IOException {
        builder.initNode(CONST_INIT_VAL);
        if (tokens.curToken().type == LBRACE) {
            terminate();
            SyntaxType t = tokens.curToken().type;
            if (t == RBRACE) {
                terminate();
            } else {
                constInitVal();
                while (tokens.curToken().type == COMMA) {
                    terminate();
                    constInitVal();
                }
                check(RBRACE, UNDEFINED.toCode());
            }
        } else {
            constExpr();
        }
        builder.parseNode(new ConstInitValNode());
        OutputHandler.getInstance().writeln("<ConstInitVal>");
    }
}
