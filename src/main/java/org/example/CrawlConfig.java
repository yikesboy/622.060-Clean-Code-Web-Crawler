package org.example;

import java.net.URL;
import java.util.Set;

/**
 * Configuration settings for the web crawler.
 *
 * @param rootUrl The starting URL for the crawler.
 * @param maxDepth Maximum depth of links to follow from root URL.
 * @param allowedDomains Set of domains the crawler is allowed to visit.
 */
public record CrawlConfig(URL rootUrl, int maxDepth, Set<String> allowedDomains) {
    public CrawlConfig {
        if (rootUrl == null) {
            throw new IllegalArgumentException("Root URL cannot be null");
        }
        if (maxDepth < 0) {
            throw new IllegalArgumentException("Max depth cannot be negative.");
        }
        if (allowedDomains == null || allowedDomains.isEmpty()) {
            throw new IllegalArgumentException("At least one domain must be allowed.");
        }

        allowedDomains = Set.copyOf(allowedDomains);
    }
}
