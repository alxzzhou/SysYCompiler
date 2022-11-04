package cfg.quad;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuadUtil {
    public static int str_index = 0;

    public static String genIRVar(String name, int id) {
        return "V_" + name + "_" + id;
    }

    public static String genIRStr() {
        return "S_" + str_index++;
    }

    public static String genIRFunc(String name) {
        return "F_" + name;
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
        Pattern pattern = Pattern.compile("[+-]*\\d+");
        Matcher matcher = pattern.matcher(s);
        return matcher.find();
    }

}
