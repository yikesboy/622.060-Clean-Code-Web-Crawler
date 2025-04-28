package io.github.yikesboy.parser;

import io.github.yikesboy.html.HtmlDocument;

import java.net.URL;
import java.util.List;

public interface LinkExtractorInterface {
    List<URL> extractLinks(HtmlDocument document, URL baseUrl);
}
