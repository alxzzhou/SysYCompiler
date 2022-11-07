package cfg.quad;

import statics.io.OutputHandler;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuadUtil {
    public static int str_index = 0;

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

}
