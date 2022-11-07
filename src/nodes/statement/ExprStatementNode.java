package nodes.statement;

import nodes.Node;
import statics.assembly.AssemblyInfo;
import statics.assembly.AssemblyRes;
import statics.exception.ExcCheckInfo;
import statics.exception.ExcCheckRes;

import java.io.IOException;

public class ExprStatementNode extends Node {
    @Override
    public void check(ExcCheckInfo info, ExcCheckRes res) {

    }

    public void assemble(AssemblyInfo info, AssemblyRes res) throws IOException {
        children.getFirst().assemble(info, res);
    }
}
