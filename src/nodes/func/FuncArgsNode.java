package nodes.func;

import nodes.Node;
import statics.assembly.AssemblyInfo;
import statics.assembly.AssemblyRes;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static statics.setup.SyntaxType.EXPR;

public class FuncArgsNode extends Node {
    @Override
    public void assemble(AssemblyInfo info, AssemblyRes res) throws IOException {
        List<String> args = new ArrayList<>();
        for (Node node : children) {
            node.assemble(info, res);
            if (node.type == EXPR) {
                args.add(res.res);
            }
        }
        res.args = args;
    }

    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        for (Node node : children) {
            ExcCheckRes r = new ExcCheckRes();
            node.check(info, r);
            if (node.type == EXPR) {
                res.args.add(r.dim);
            }
        }
    }
}
