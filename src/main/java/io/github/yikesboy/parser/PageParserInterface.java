package io.github.yikesboy.parser;

import io.github.yikesboy.models.WebPage;

import java.net.URL;
import java.util.List;

public interface PageParserInterface {
    WebPage parse(URL url, int depth);

    List<URL> extractLinks(WebPage url);
}
