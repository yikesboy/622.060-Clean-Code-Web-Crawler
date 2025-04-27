package org.example.parser;

import org.example.models.Heading;
import org.example.models.WebPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PageParser implements PageParserInterface {
    private static final int DEFAULT_TIMEOUT_MS = 5000;
    private static final String HEADLINE_SELECTOR_QUERY = "h1, h2, h3, h4, h5, h6";

    private final int timeoutMs;

    public PageParser() {
        this(DEFAULT_TIMEOUT_MS);
    }

    public PageParser(int timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    @Override
    public WebPage parse(URL url, int depth) {
        if (depth < 0) {
            throw new IllegalArgumentException("Depth cannot be negative.");
        }

        try {
            Document doc = fetchDocument(url);
            List<Heading> headings = extractHeadings(doc);
            return new WebPage(url, headings, depth);
        } catch (IOException e) {
            return new WebPage(url, depth, true);
        }
    }

    @Override
    public List<URL> extractLinks(URL url) {
        try {
            Document doc = fetchDocument(url);
            return extractUrlsFromDocument(doc);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private Document fetchDocument(URL url) throws IOException {
        return Jsoup.connect(url.toString()).timeout(timeoutMs).get();
    }

    private List<URL> extractUrlsFromDocument(Document doc) {
        List<URL> validUrls = new ArrayList<>();
        Elements links = doc.select("a[href]");

        for (Element link : links) {
            String href = link.attr("abs:href");
            if (!isValidHref(href)) {
                continue;
            }

            tryAddValidUrl(href, validUrls);
        }

        return validUrls;
    }

    private boolean isValidHref(String href) {
        if (href == null || href.trim().isEmpty()) {
            return false;
        }
        String lowerHref = href.toLowerCase();
        return !lowerHref.isEmpty() &&
                !lowerHref.startsWith("javascript:") &&
                !lowerHref.startsWith("#");
    }

    private void tryAddValidUrl(String href, List<URL> validUrls) {
        URL url;
        try {
            url = new URL(href);
        } catch (MalformedURLException e) {
            return;
        }
        validUrls.add(url);
    }

    private List<Heading> extractHeadings(Document document) {
        List<Heading> headings = new ArrayList<>();
        Elements elements = document.select(HEADLINE_SELECTOR_QUERY);

        for (Element element : elements) {
            int level = Integer.parseInt(element.tagName().substring(1));
            String text = element.text().trim();
            headings.add(new Heading(level, text));
        }

        return headings;
    }
}
