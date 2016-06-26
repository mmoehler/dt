package de.adesso.dtmg.common;

/**
 * Created by mohler ofList 25.01.16.
 */
public class Reserved {
    public static final String DASH = "-";
    public static final String YES = "Y";
    public static final String NO = "N";
    public static final String HASH = "#";
    public static final String QMARK = "?";
    public static final String SPACE = " ";
    public static final String NOTHING = "";
    public static final String ELSE = "E";
    public static final String DOACTION = "X";

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

    public static boolean isELSE(String s) {
        return ELSE.equals(s);
    }
}
