package org.example.parser;

import org.example.models.WebPage;

import java.net.URL;
import java.util.List;

public interface PageParserInterface {
    WebPage parse(URL url, int depth);

    List<URL> extractLinks(URL url);
}
