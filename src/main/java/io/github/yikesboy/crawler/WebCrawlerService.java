package io.github.yikesboy.crawler;

import io.github.yikesboy.config.CrawlConfig;
import io.github.yikesboy.models.CrawlError;
import io.github.yikesboy.models.CrawlResult;
import io.github.yikesboy.models.CrawlTask;
import io.github.yikesboy.models.WebPage;
import io.github.yikesboy.parser.PageParserInterface;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinPool;

public class WebCrawlerService implements WebCrawlerServiceInterface {
    private final PageParserInterface parser;
    private final Set<String> visitedUrls;
    private final Queue<CrawlError> errors;
    private final ForkJoinPool forkJoinPool;

    public WebCrawlerService(PageParserInterface parser) {
        this.parser = parser;
        this.visitedUrls = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.errors = new ConcurrentLinkedQueue<>();
        this.forkJoinPool = new ForkJoinPool(Math.max(2, Runtime.getRuntime().availableProcessors() / 2));
    }

    @Override
    public CrawlResult crawl(CrawlConfig config) throws IllegalArgumentException {
        visitedUrls.clear();
        errors.clear();
        CrawlTask rootTask = new CrawlTask(config.rootUrl(), 0, config, parser, visitedUrls, errors);
        WebPage rootPage = forkJoinPool.invoke(rootTask);
        return new CrawlResult(rootPage, new ArrayList<>(errors));
    }
}