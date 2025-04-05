package org.example;

import java.net.URL;
import java.util.Set;

public class CrawlConfig {
    private final URL rootUrl;
    private final int maxDepth;
    private final Set<String> allowedDomains;


    public CrawlConfig(URL rootUrl, int maxDepth, Set<String> allowedDomains) {
        this.rootUrl = rootUrl;
        this.maxDepth = maxDepth;
        this.allowedDomains = allowedDomains;
    }

    public URL getRootUrl() {
        return rootUrl;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public Set<String> getAllowedDomains() {
        return allowedDomains;
    }
}
