package nodes.var;

import nodes.Node;
import statics.assembly.AssemblyInfo;
import statics.assembly.AssemblyRes;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static statics.setup.SyntaxType.EXPR;
import static statics.setup.SyntaxType.INIT_VAL;

public class InitValNode extends Node {
    @Override
    public void assemble(AssemblyInfo info, AssemblyRes res) throws IOException {
        List<String> initVal = new ArrayList<>();
        for (Node node : children) {
            node.assemble(info, res);
            if (node.type == EXPR) {
                initVal.add(res.res);
            } else if (node.type == INIT_VAL) {
                initVal.addAll(res.init);
            }
        }
        res.init = initVal;
    }

    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
    }
}
