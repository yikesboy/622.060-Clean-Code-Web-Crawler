package io.github.yikesboy.util;

import io.github.yikesboy.config.CrawlConfig;

public interface ArgumentParserInterface {
    CrawlConfig parse(String[] args);
}
