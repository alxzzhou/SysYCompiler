package nodes.var;

import nodes.Node;
import statics.assembly.AssemblyInfo;
import statics.assembly.AssemblyRes;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;

import java.io.IOException;

public class VarDeclNode extends Node {
    public void assemble(AssemblyInfo info, AssemblyRes res) throws IOException {
        for (Node node : children) {
            info.isConst = false;
            node.assemble(info, res);
        }
        info.isConst = false;
    }

    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        info.isConst = false;
        for (Node node : children) {
            node.check(info, res);
        }
        info.isConst = false;
    }
}
