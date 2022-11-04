package cfg.quad;

import java.util.HashSet;
import java.util.Set;

import static statics.assembly.AssemblyType.RETURN;

public class Return extends Quadruple {
    String res;

    public Return(String r) {
        super(RETURN);
        this.res = r;
    }

    public Set<String> getUse() {
        return new HashSet<>() {{
            add(res);
        }};
    }
}
