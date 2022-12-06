package cfg.quad;

import statics.io.OutputHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuadUtil {
    public static int str_index = 0;

    public static final int validRegister = 18;
    public static final int offset = 8;
    public static final HashMap<Integer, String> registerUsage = new HashMap<Integer, String>() {{
        for (int i = 0; i < validRegister; i++) {
            put(i + offset, "");
        }
    }};

    public static int ralloc(String name) {
        int ret = -1;
        for (Map.Entry<Integer, String> entry : registerUsage.entrySet()) {
            if (entry.getValue().length() == 0) {
                ret = entry.getKey();
                break;
            }
        }
        if (ret != -1) {
            registerUsage.put(ret, name);
        }
        return ret;
    }

    public static String genIRVar(String name, int id) {
        return "var_" + name + "_" + id;
    }

    public static String genIRStr() {
        return "str_" + str_index++;
    }

    public static String genIRFunc(String name) {
        return "func_" + name;
    }

    public static boolean isHex(String s) {
        return s.length() >= 3 && (s.charAt(1) == 'x' || s.charAt(1) == 'X');
    }

    public static boolean isDec(String s) {
        try {
            Integer.parseInt(s);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean isPowerOf2(int i) {
        return (i & -i) == i;
    }

    public static boolean isReg(String s) {
        return s.charAt(0) == '$';
    }

    public static String pureHex(String s) {
        return s.substring(2);
    }

    public static boolean isNumberFormat(String s) {
        Pattern pattern = Pattern.compile("^[+-]*\\d+$");
        Matcher matcher = pattern.matcher(s);
        return matcher.find();
    }

    public static void li(String reg, String i) throws IOException {
        if (Math.abs(Integer.parseInt(i)) < 32768) {
            OutputHandler.getInstance()
                    .writeln("addi " + reg + ", $0, " + i);
        } else {
            OutputHandler.getInstance()
                    .writeln("li " + reg + ", " + i);
        }
    }

    public static void resetRegisterUsage() {
        registerUsage.clear();
        for (int i = 0; i < validRegister; i++) {
            registerUsage.put(i + offset, "");
        }
    }
}
