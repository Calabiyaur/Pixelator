package main.java.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    /**
     * @param caps CAMEL_HOP
     * @return camelHop
     */
    public static String toCamel(String caps) {
        Pattern pattern = Pattern.compile("_(.)");
        Matcher matcher = pattern.matcher(caps.toLowerCase());

        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(result, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(result);

        return result.toString();
    }

    /**
     * @param caps CAMEL_HOP
     * @return CamelHop
     */
    public static String toCamelCap(String caps) {
        String camel = toCamel(caps);
        return camel.substring(0, 1).toUpperCase().concat(camel.substring(1));
    }
}
