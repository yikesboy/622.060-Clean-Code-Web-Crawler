package io.github.yikesboy.parser;

import io.github.yikesboy.html.HtmlDocument;
import io.github.yikesboy.html.HtmlDocumentFetcher;
import io.github.yikesboy.html.jsoup.JsoupDocumentFetcher;
import io.github.yikesboy.models.Heading;
import io.github.yikesboy.models.WebPage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PageParser implements PageParserInterface {
    private static final int DEFAULT_TIMEOUT_MS = 5000;

    private final HtmlDocumentFetcher documentFetcher;
    private final HeadingExtractorInterface headingExtractor;
    private final LinkExtractorInterface linkExtractor;

    public PageParser() {
        this(DEFAULT_TIMEOUT_MS);
    }

    public PageParser(int timeoutMs) {
        this(
                new JsoupDocumentFetcher(timeoutMs),
                new HeadingExtractor(),
                new LinkExtractor()
        );
    }

    public PageParser(HtmlDocumentFetcher documentFetcher,
                      HeadingExtractorInterface headingExtractor,
                      LinkExtractorInterface linkExtractor) {
        this.documentFetcher = documentFetcher;
        this.headingExtractor = headingExtractor;
        this.linkExtractor = linkExtractor;
    }

    @Override
    public WebPage parse(URL url, int depth) {
        if (url == null) {
            throw new IllegalArgumentException("Url cannot be null.");
        }
        if (depth < 0) {
            throw new IllegalArgumentException("Depth cannot be negative.");
        }

        try {
            HtmlDocument document = documentFetcher.fetch(url);
            List<Heading> headings = headingExtractor.extractHeadings(document);
            return new WebPage(url, headings, depth, document);
        } catch (IOException e) {
            return new WebPage(url, depth, true);
        }
    }

    @Override
    public List<URL> extractLinks(WebPage page) {
        if (page == null || page.isBroken()) {
            return new ArrayList<>();
        }

        HtmlDocument document = page.getDocument();
        return linkExtractor.extractLinks(document, page.getUrl());
    }
}
