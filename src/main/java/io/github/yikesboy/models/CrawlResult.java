package io.github.yikesboy.models;

import java.util.List;

public record CrawlResult(WebPage rootPage, List<CrawlError> errors) {
}
