package nodes;

import statics.assembly.AssemblyInfo;
import statics.assembly.AssemblyRes;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;
import statics.setup.SyntaxType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FuncParamsNode extends Node {
    public void assemble(AssemblyInfo info, AssemblyRes res) throws IOException {
        res.clear();
        List<String> args = new ArrayList<>();
        for (var node : children) {
            node.assemble(info, res);
            if (node.type == SyntaxType.FUNC_PARAM) {
                args.add(res.param);
            }
        }
        res.args = args;
    }

    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        for (Node node : children) {
            ExcCheckRes r = new ExcCheckRes();
            node.check(info, r);
            if (node.type == SyntaxType.FUNC_PARAM) {
                res.args.add(r.dim);
            }
        }
    }
}
