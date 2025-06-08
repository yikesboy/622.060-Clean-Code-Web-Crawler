package io.github.yikesboy.crawler;

import io.github.yikesboy.config.CrawlConfig;
import io.github.yikesboy.models.CrawlError;
import io.github.yikesboy.models.WebPage;
import io.github.yikesboy.parser.PageParserInterface;
import io.github.yikesboy.util.UrlUtil;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.RecursiveTask;

public class CrawlTask extends RecursiveTask<WebPage> {
    private final URL url;
    private final int currentDepth;
    private final CrawlConfig config;
    private final PageParserInterface parser;
    private final Set<String> visitedUrls;
    private final Queue<CrawlError> errors;

    public CrawlTask(URL url, int currentDepth, CrawlConfig config, PageParserInterface parser, Set<String> visitedUrls, Queue<CrawlError> errors) {
        this.url = url;
        this.currentDepth = currentDepth;
        this.config = config;
        this.parser = parser;
        this.visitedUrls = visitedUrls;
        this.errors = errors;
    }

    public WebPage compute() {
        if (url == null || currentDepth > config.maxDepth()) {
            return null;
        }

        String normalizedUrl = UrlUtil.normalizeUrl(url);

        if (visitedUrls.contains(normalizedUrl)) {
            return null;
        }

        visitedUrls.add(normalizedUrl);

        WebPage page = parser.parse(url, currentDepth);

        if (currentDepth == config.maxDepth()) {
            return page;
        }

        if (page.isBroken()) {
            errors.add(new CrawlError(url, currentDepth, "Failed to fetch or parse page"));
            return page;
        }

        List<URL> links = parser.extractLinks(page);
        List<CrawlTask> tasks = new ArrayList<>();

        for (URL link : links) {
            if (UrlUtil.isAllowedDomain(link, config.allowedDomains())) {
                CrawlTask task = new CrawlTask(link, currentDepth + 1, config, parser, visitedUrls, errors);
                task.fork();
                tasks.add(task);
            }
        }

        for (CrawlTask task : tasks) {
            WebPage childPage = task.join();
            page.addChildPage(childPage);
        }

        return page;
    }
}
