package nodes.expr;

import cfg.quad.Quadruple;
import cfg.quad.calc.Divide;
import cfg.quad.calc.Minus;
import cfg.quad.calc.Mod;
import cfg.quad.calc.Multiply;
import cfg.quad.calc.Plus;
import cfg.quad.func.Assign;
import cfg.quad.logic.EQ;
import cfg.quad.logic.GEQ;
import cfg.quad.logic.GRE;
import cfg.quad.logic.LEQ;
import cfg.quad.logic.LSS;
import cfg.quad.logic.NEQ;
import nodes.Node;
import statics.assembly.AssemblyInfo;
import statics.assembly.AssemblyRes;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;
import statics.setup.SyntaxType;

import java.io.IOException;

import static cfg.CFGBuilder.CFG_BUILDER;
import static cfg.quad.QuadUtil.isNumberFormat;

public class OmniExpr extends Node {

    @Override
    public void assemble(AssemblyInfo info, AssemblyRes res) throws IOException {
        if (children.size() == 1) {
            AssemblyRes r = new AssemblyRes();
            children.getFirst().assemble(info, r);
            if (isNumberFormat(r.res)) {
                val = r.res;
            } else {
                val = CFG_BUILDER.tempVar();
                CFG_BUILDER.insert(new Assign(val, r.res));
            }
        } else if (children.size() == 3) {
            if (children.get(1).type == SyntaxType.OR) {
                shortCircuitOR(info, res);
            } else if (children.get(1).type == SyntaxType.AND) {
                shortCircuitAND(info, res);
            } else {
                AssemblyRes r1 = new AssemblyRes(), r2 = new AssemblyRes();
                Node op = children.get(1);
                children.getFirst().assemble(info, r1);
                children.getLast().assemble(info, r2);
                if (isNumberFormat(r1.res) && isNumberFormat(r2.res)) {
                    int v1 = Integer.parseInt(r1.res);
                    int v2 = Integer.parseInt(r2.res);
                    int value;
                    switch (op.type) {
                        case PLUS:
                            value = v1 + v2;
                            break;
                        case MINU:
                            value = v1 - v2;
                            break;
                        case MULT:
                            value = v1 * v2;
                            break;
                        case DIV:
                            value = v1 / v2;
                            break;
                        case MOD:
                            value = v1 % v2;
                            break;
                        case GEQ:
                            value = eq(v1 >= v2);
                            break;
                        case LEQ:
                            value = eq(v1 <= v2);
                            break;
                        case GRE:
                            value = eq(v1 > v2);
                            break;
                        case LSS:
                            value = eq(v1 < v2);
                            break;
                        case EQL:
                            value = eq(v1 == v2);
                            break;
                        case NEQ:
                            value = eq(v1 != v2);
                            break;
                        default:
                            value = 0;
                    }
                    val = String.valueOf(value);
                } else {
                    val = CFG_BUILDER.tempVar();
                    Quadruple insert = null;
                    switch (op.type) {
                        case PLUS:
                            insert = new Plus(val, r1.res, r2.res);
                            break;
                        case MINU:
                            insert = new Minus(val, r1.res, r2.res);
                            break;
                        case MULT:
                            insert = new Multiply(val, r1.res, r2.res);
                            break;
                        case DIV:
                            insert = new Divide(val, r1.res, r2.res);
                            break;
                        case MOD:
                            insert = new Mod(val, r1.res, r2.res);
                            break;
                        case GEQ:
                            insert = new GEQ(val, r1.res, r2.res);
                            break;
                        case LEQ:
                            insert = new LEQ(val, r1.res, r2.res);
                            break;
                        case GRE:
                            insert = new GRE(val, r1.res, r2.res);
                            break;
                        case LSS:
                            insert = new LSS(val, r1.res, r2.res);
                            break;
                        case EQL:
                            insert = new EQ(val, r1.res, r2.res);
                            break;
                        case NEQ:
                            insert = new NEQ(val, r1.res, r2.res);
                            break;

                    }
                    CFG_BUILDER.insert(insert);
                }
            }
        }
        res.res = val;
    }

    int eq(boolean b) {
        return b ? 1 : 0;
    }

    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        for (Node node : children) {
            node.check(info, res);
        }
    }
}
