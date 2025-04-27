package org.example.crawler;

import org.example.config.CrawlConfig;
import org.example.models.WebPage;

public interface WebCrawlerServiceInterface {
    WebPage crawl(CrawlConfig config);
}
