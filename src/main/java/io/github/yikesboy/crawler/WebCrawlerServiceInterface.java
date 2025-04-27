package io.github.yikesboy.crawler;

import io.github.yikesboy.config.CrawlConfig;
import io.github.yikesboy.models.WebPage;

public interface WebCrawlerServiceInterface {
    WebPage crawl(CrawlConfig config);
}
