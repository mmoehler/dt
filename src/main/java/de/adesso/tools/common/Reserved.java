package de.adesso.tools.common;

/**
 * Created by mohler on 25.01.16.
 */
public class Reserved {
    public static final String DASH = "-";
    public static final String YES = "YES";
    public static final String NO = "NO";

    public static boolean isDASH(String s) {
        return DASH.equals(s);
    }

    public static boolean isYES(String s) {
        return YES.equals(s);
    }

    public static boolean isNO(String s) {
        return NO.equals(s);
    }


}
