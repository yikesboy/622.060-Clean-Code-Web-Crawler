package io.github.yikesboy.util;

import java.net.URL;
import java.util.Set;

public class UrlUtil {
    private UrlUtil() {
    }

    public static boolean isAllowedDomain(URL url, Set<String> allowedDomains) {
        String host = url.getHost();

        if (allowedDomains.contains(host)) {
            return true;
        }

        for (String domain : allowedDomains) {
            if (host.endsWith("." + domain)) {
                return true;
            }
        }

        return false;
    }

    public static String normalizeUrl(URL url) {
        String urlStr = url.toString();

        if (urlStr.endsWith("/")) {
            urlStr = urlStr.substring(0, urlStr.length() - 1);
        }

        return urlStr.toLowerCase();
    }
}
