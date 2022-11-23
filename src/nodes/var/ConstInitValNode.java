package nodes.var;

import nodes.Node;
import statics.assembly.AssemblyInfo;
import statics.assembly.AssemblyRes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static statics.setup.SyntaxType.CONST_INIT_VAL;
import static statics.setup.SyntaxType.EXPR;

public class ConstInitValNode extends Node {
    @Override
    public void assemble(AssemblyInfo info, AssemblyRes res) throws IOException {
        List<String> initVal = new ArrayList<>();
        for (Node node : children) {
            node.assemble(info, res);
            if (node.type == EXPR) {
                initVal.add(res.res);
            } else if (node.type == CONST_INIT_VAL) {
                initVal.addAll(res.init);
            }
        }
        res.init = initVal;
    }
}
