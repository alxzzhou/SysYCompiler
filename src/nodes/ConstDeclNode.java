package nodes;

import statics.assembly.AssemblyInfo;
import statics.assembly.AssemblyRes;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;

import java.io.IOException;

public class ConstDeclNode extends Node {
    public void assemble(AssemblyInfo info, AssemblyRes res) throws IOException {
        for (var node : children) {
            info.isConst = true;
            node.assemble(info, res);
        }
        info.isConst = false;
    }
    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {
        info.isConst = true;
        for (var node : children) {
            node.check(info, res);
        }
        info.isConst = false;
    }
}
