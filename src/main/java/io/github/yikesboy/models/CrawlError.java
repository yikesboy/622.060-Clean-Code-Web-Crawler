package io.github.yikesboy.models;

import java.net.URL;

public record CrawlError(URL url, int depth, String message) {
}
