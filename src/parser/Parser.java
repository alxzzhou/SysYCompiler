package parser;

import lexer.Tokens;
import nodes.expr.ExprNode;
import nodes.expr.LValNode;
import nodes.expr.NumberNode;
import nodes.expr.OmniExpr;
import nodes.expr.PrimaryExprNode;
import nodes.expr.UnaryExprNode;
import nodes.expr.UnaryPrefixOpNode;
import nodes.func.FuncArgsNode;
import nodes.func.FuncDefNode;
import nodes.func.FuncExprNode;
import nodes.func.FuncParamNode;
import nodes.func.FuncParamsNode;
import nodes.func.FuncTypeNode;
import nodes.statement.AssignStatementNode;
import nodes.statement.BlockNode;
import nodes.statement.BreakNode;
import nodes.statement.CompUnitNode;
import nodes.statement.ContinueNode;
import nodes.statement.ExprStatementNode;
import nodes.statement.IfNode;
import nodes.statement.PrintfNode;
import nodes.statement.ReturnNode;
import nodes.statement.StatementNode;
import nodes.statement.WhileNode;
import nodes.var.ConstDeclNode;
import nodes.var.ConstDefNode;
import nodes.var.ConstInitValNode;
import nodes.var.InitValNode;
import nodes.var.VarDeclNode;
import nodes.var.VarDefNode;
import statics.exception.CompException;
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

    void check(SyntaxType t, CompException.Exception e) {
        if (tokens.curToken().type != t) {
            error(e);
        } else {
            terminate();
        }
    }

    void terminate() {
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
    }

    void funcType() {
        builder.initNode(FUNC_TYPE);
        SyntaxType type = tokens.curToken().type;
        if (type != VOIDTK && type != INTTK) {
            error(UNDEFINED.toCode());
            return;
        }
        terminate();
        builder.parseNode(new FuncTypeNode());
    }

    void funcFParams() throws IOException {
        builder.initNode(FUNC_PARAMS);
        funcFParam();
        while (tokens.curToken().type == COMMA) {
            terminate();
            funcFParam();
        }
        builder.parseNode(new FuncParamsNode());
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
    }

    void constExpr() throws IOException {
        expr();
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
        if (EXPR_PREFIX.contains(type)) {
            int p = builder.getChildrenSize();
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
        expr();
        check(RPARENT, LACK_PARENT.toCode());
        statement();
        if (tokens.curToken().type == ELSETK) {
            terminate();
            statement();
        }
        builder.parseNode(new IfNode());
    }

    void whileStatement() throws IOException {
        builder.initNode(WHILE_STMT);
        check(WHILETK, UNDEFINED.toCode());
        check(LPARENT, UNDEFINED.toCode());
        expr();
        check(RPARENT, LACK_PARENT.toCode());
        statement();
        builder.parseNode(new WhileNode());
    }

    void expr() throws IOException {
        builder.initNode(EXPR);
        omniExpr(0);
        builder.parseNode(new ExprNode());
    }

    void omniExpr(int cp) throws IOException {
        int p = builder.getChildrenSize();
        unaryExpr();
        SyntaxType t = tokens.curToken().type;
        while (OPERAND_PRIORITY.containsKey(t)) {
            int pri = OPERAND_PRIORITY.get(t);
            if (pri > cp) {
                builder.initNodeAt(OMNI_EXPR, p);
                terminate();
                omniExpr(pri);
                builder.parseNode(new OmniExpr());
            } else {
                break;
            }
            t = tokens.curToken().type;
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
    }

    private void unaryPrefixOp() throws IOException {
        builder.initNode(PREFIX_OP);
        SyntaxType t = tokens.curToken().type;
        while (t == NOT || t == PLUS || t == MINU) {
            unaryOp();
            t = tokens.curToken().type;
        }
        builder.parseNode(new UnaryPrefixOpNode());
    }

    void unaryOp() {
        terminate();
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
    }

    void number() throws IOException {
        builder.initNode(NUMBER);
        check(INTCON, UNDEFINED.toCode());
        builder.parseNode(new NumberNode());
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
            check(RPARENT, LACK_PARENT.toCode());
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
    }
}
