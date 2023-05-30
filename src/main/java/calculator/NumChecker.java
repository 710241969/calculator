package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumChecker {
    // 数字判断 正则表达式
    private static final Pattern NUM_PATTERN = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");
    public static final String ADD = "+";
    public static final String SUBTRACT = "-";
    public static final String MULTIPLY = "*";
    public static final String DIVIDE = "/";

    /**
     * 正则匹判断配数字
     *
     * @param str
     * @return true 是数字， false 不是数字
     */
    public static boolean isNumeric(String str) {
        // 通过 Matcher 进行字符串匹配
        Matcher m = NUM_PATTERN.matcher(str);
        return m.matches();
    }

    public static boolean isOperator(String str) {
        if (!str.equals(ADD) && !str.equals(SUBTRACT)
                && !str.equals(MULTIPLY) && !str.equals(DIVIDE)) {
            return false;
        }
        return true;
    }
}
