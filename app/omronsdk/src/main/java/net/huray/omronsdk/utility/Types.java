package net.huray.omronsdk.utility;

public class Types {
    @SuppressWarnings("unchecked")
    public static <T> T autoCast(Object obj) {
        return (T) obj;
    }
}
