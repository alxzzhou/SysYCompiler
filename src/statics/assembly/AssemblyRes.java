package statics.assembly;

import java.util.ArrayList;
import java.util.List;

public class AssemblyRes {
    public String res;
    public String param;
    public List<String> args = new ArrayList<>();
    public List<String> init = new ArrayList<>();
    public boolean isArray;

    public AssemblyRes() {
    }

    public void clear() {
        param = null;
        args.clear();
        init.clear();
    }
}
