package ua.fitness.util;

class FormatHelper {

    private FormatHelper() {
    }

    static String normalize(String str) {
        if (str==null) {
            return null;
        }
        return str.trim().toUpperCase();
    }
}