package org.example.util;

import org.example.config.CrawlConfig;

public interface ArgumentParserInterface {
    CrawlConfig parse(String[] args);
}
