package com.orainge.bark_processor.forward.client.enums;

public enum OSPlatform {
    Any("any"),
    Linux("Linux"),
    MAC_OS("Mac OS"),
    MAC_OS_X("Mac OS X"),
    Windows("Windows"),
    OS2("OS/2"),
    Solaris("Solaris"),
    SunOS("SunOS"),
    MPEiX("MPE/iX"),
    HP_UX("HP-UX"),
    AIX("AIX"),
    OS390("OS/390"),
    FreeBSD("FreeBSD"),
    Irix("Irix"),
    Digital_Unix("Digital Unix"),
    NetWare_411("NetWare"),
    OSF1("OSF1"),
    OpenVMS("OpenVMS"),
    Others("Others");

    private final String description;

    OSPlatform(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}