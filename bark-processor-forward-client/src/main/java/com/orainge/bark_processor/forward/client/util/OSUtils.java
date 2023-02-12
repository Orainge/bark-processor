package com.orainge.bark_processor.forward.client.util;

import com.orainge.bark_processor.forward.client.enums.OSPlatform;

public class OSUtils {
    private static final String OS = System.getProperty("os.name").toLowerCase();
    private static OSPlatform platform;

    static {
        OSPlatform platform;

        if (OS.contains("linux")) {
            platform = OSPlatform.Linux;
        } else if (OS.contains("mac") && OS.indexOf("os") > 0 && !OS.contains("x")) {
            platform = OSPlatform.MAC_OS;
        } else if (OS.contains("mac") && OS.indexOf("os") > 0 && OS.indexOf("x") > 0) {
            platform = OSPlatform.MAC_OS_X;
        } else if (OS.contains("windows")) {
            platform = OSPlatform.Windows;
        } else if (OS.contains("aix")) {
            platform = OSPlatform.AIX;
        } else if (OS.contains("digital") && OS.indexOf("unix") > 0) {
            platform = OSPlatform.Digital_Unix;
        } else if (OS.contains("freebsd")) {
            platform = OSPlatform.FreeBSD;
        } else if (OS.contains("hp-ux")) {
            platform = OSPlatform.HP_UX;
        } else if (OS.contains("irix")) {
            platform = OSPlatform.Irix;
        } else if (OS.contains("mpe/ix")) {
            platform = OSPlatform.MPEiX;
        } else if (OS.contains("netware")) {
            platform = OSPlatform.NetWare_411;
        } else if (OS.contains("openvms")) {
            platform = OSPlatform.OpenVMS;
        } else if (OS.contains("os/2")) {
            platform = OSPlatform.OS2;
        } else if (OS.contains("os/390")) {
            platform = OSPlatform.OS390;
        } else if (OS.contains("osf1")) {
            platform = OSPlatform.OSF1;
        } else if (OS.contains("solaris")) {
            platform = OSPlatform.Solaris;
        } else if (OS.contains("sunos")) {
            platform = OSPlatform.SunOS;
        } else {
            platform = OSPlatform.Others;
        }

        OSUtils.platform = platform;
    }

    private OSUtils() {
    }

    public static boolean isMacOS() {
        return OSPlatform.MAC_OS.equals(OSUtils.platform);
    }

    public static boolean isMacOSX() {
        return OSPlatform.MAC_OS_X.equals(OSUtils.platform);
    }

    public static boolean isWindows() {
        return OSPlatform.Windows.equals(OSUtils.platform);
    }

    public static boolean isOS2() {
        return OSPlatform.OS2.equals(OSUtils.platform);
    }

    public static boolean isSolaris() {
        return OSPlatform.Solaris.equals(OSUtils.platform);
    }

    public static boolean isSunOS() {
        return OSPlatform.SunOS.equals(OSUtils.platform);
    }

    public static boolean isMPEiX() {
        return OSPlatform.MPEiX.equals(OSUtils.platform);
    }

    public static boolean isHPUX() {
        return OSPlatform.HP_UX.equals(OSUtils.platform);
    }

    public static boolean isAix() {
        return OSPlatform.AIX.equals(OSUtils.platform);
    }

    public static boolean isOS390() {
        return OSPlatform.OS390.equals(OSUtils.platform);
    }

    public static boolean isFreeBSD() {
        return OSPlatform.FreeBSD.equals(OSUtils.platform);
    }

    public static boolean isIrix() {
        return OSPlatform.Irix.equals(OSUtils.platform);
    }

    public static boolean isDigitalUnix() {
        return OSPlatform.Digital_Unix.equals(OSUtils.platform);
    }

    public static boolean isNetWare() {
        return OSPlatform.NetWare_411.equals(OSUtils.platform);
    }

    public static boolean isOSF1() {
        return OSPlatform.OSF1.equals(OSUtils.platform);
    }

    public static boolean isOpenVMS() {
        return OSPlatform.OpenVMS.equals(OSUtils.platform);
    }

    public static OSPlatform getPlatform() {
        return OSUtils.platform;
    }
}