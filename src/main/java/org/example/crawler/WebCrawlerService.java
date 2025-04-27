package org.example.crawler;

import org.example.config.CrawlConfig;
import org.example.models.WebPage;
import org.example.parser.PageParserInterface;
import org.example.util.UrlUtil;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WebCrawlerService implements WebCrawlerServiceInterface {
    private final PageParserInterface parser;
    private final Set<String> visitedUrls;

    public WebCrawlerService(PageParserInterface parser) {
        this.parser = parser;
        this.visitedUrls = new HashSet<>();
    }

    @Override
    public WebPage crawl(CrawlConfig config) {
        validateConfig(config);
        visitedUrls.clear();
        return crawlPage(config.rootUrl(), 0, config);
    }

    private void validateConfig(CrawlConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("CrawlConfig cannot be null");
        }
        if (config.rootUrl() == null) {
            throw new IllegalArgumentException("Root URL cannot be null.");
        }
    }

    private WebPage crawlPage(URL url, int currentDepth, CrawlConfig config) {
        if (url == null) {
            return null;
        }

        String normalizedUrl = UrlUtil.normalizeUrl(url);

        if (visitedUrls.contains(normalizedUrl)) {
            return null;
        }

        visitedUrls.add(normalizedUrl);

        WebPage page = parser.parse(url, currentDepth);

        if (currentDepth >= config.maxDepth() || page.isBroken()) {
            return page;
        }

        List<URL> links = parser.extractLinks(page);
        for (URL link : links) {
            if (link != null && UrlUtil.isAllowedDomain(link, config.allowedDomains())) {
                WebPage childPage = crawlPage(link, currentDepth + 1, config);
                if (childPage != null) {
                    page.addChildPage(childPage);
                }
            }
        }

        return page;
    }
}
