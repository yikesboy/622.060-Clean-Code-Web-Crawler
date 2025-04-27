package org.example.crawler;

import org.example.config.CrawlConfig;
import org.example.models.WebPage;
import org.example.parser.PageParser;
import org.example.util.UrlUtil;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WebCrawlerService implements WebCrawlerServiceInterface {
    private final PageParser parser;
    private final Set<String> visitedUrls = new HashSet<>();

    public WebCrawlerService(PageParser parser) {
        this.parser = parser;
    }

    @Override
    public WebPage crawl(CrawlConfig config) {
        return crawlPage(config.rootUrl(), 0, config);
    }

    private WebPage crawlPage(URL url, int currentDepth, CrawlConfig config) {
        String normalizedUrl = UrlUtil.normalizeUrl(url);

        if (visitedUrls.contains(normalizedUrl)) {
            return null;
        }

        visitedUrls.add(normalizedUrl);

        WebPage page = parser.parse(url, currentDepth);

        if (currentDepth >= config.maxDepth() || page.isBroken()) {
            return page;
        }

        List<URL> links = parser.extractLinks(url);
        for (URL link : links) {
            if (UrlUtil.isAllowedDomain(link, config.allowedDomains())) {
                WebPage childPage = crawlPage(link, currentDepth + 1, config);
                if (childPage != null) {
                    page.addChildPage(childPage);
                }
            }
        }

        return page;
    }
}
