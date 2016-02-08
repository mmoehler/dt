package de.adesso.tools.util;

import java.util.Locale;

/**
 * Created by mohler on 07.02.16.
 */
public final class OsCheck {
    // cached result of OS detection
    protected static OSType detectedOS;

    ;

    /**
     * detect the operating system from the os.name System property and cache
     * the result
     *
     * @returns - the operating system detected
     */
    public static OSType getOperatingSystemType() {
        if (detectedOS == null) {
            String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
            if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0)) {
                detectedOS = OSType.MAC_OS;
            } else if (OS.indexOf("win") >= 0) {
                detectedOS = OSType.WINDOWS;
            } else if (OS.indexOf("nux") >= 0) {
                detectedOS = OSType.LINUX;
            } else {
                detectedOS = OSType.OTHER;
            }
        }
        return detectedOS;
    }

    /**
     * types of Operating Systems
     */
    public enum OSType {
        WINDOWS, MAC_OS, LINUX, OTHER
    }
}