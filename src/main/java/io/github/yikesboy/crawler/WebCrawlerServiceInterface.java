package io.github.yikesboy.crawler;

import io.github.yikesboy.config.CrawlConfig;
import io.github.yikesboy.models.CrawlResult;

public interface WebCrawlerServiceInterface {
    CrawlResult crawl(CrawlConfig config);
}
