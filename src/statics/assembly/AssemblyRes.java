package statics.assembly;

import java.util.List;

public class AssemblyRes {
    public String res;
    public String param;
    public List<String > args;
    public List<String > init;
    public boolean isArray;

    public void clear() {
        param = null;
        args.clear();
        init.clear();
    }

    public AssemblyRes() {
    }
}
