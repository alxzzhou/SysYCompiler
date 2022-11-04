package statics.exception;

import java.util.ArrayList;
import java.util.List;

public class ExcCheckRes {
    public final List<Integer> args = new ArrayList<>();
    public int val = 0, dim = 0;
    public boolean isConst = false;
    public boolean isReturn = false;
    public boolean isVoid = false;

    public ExcCheckRes() {
    }
}
