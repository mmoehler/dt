package de.adesso.tools.common;

/**
 * Created by mohler on 25.01.16.
 */
public class Reserved {
    public static final String DASH = "-";
    public static final String YES = "Y";
    public static final String NO = "N";
    public static final String HASH = "#";
    public static final String QMARK = "?";
    public static final String SPACE = " ";
    public static final String NOTHING = "";

    public static boolean isDASH(String s) {
        return DASH.equals(s);
    }

    public static boolean isYES(String s) {
        return YES.equals(s);
    }

    public static boolean isNO(String s) {
        return NO.equals(s);
    }

    public static boolean isHASH(String s) {
        return HASH.equals(s);
    }
}
