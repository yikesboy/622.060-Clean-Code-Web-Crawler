package io.github.yikesboy.parser;

import io.github.yikesboy.html.HtmlDocument;
import io.github.yikesboy.html.HtmlElement;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LinkExtractor {
    private static final String LINK_SELECTOR = "a[href]";
    private static final String HREF_ATTRIBUTE = "href";
    private static final String JAVASCRIPT_PREFIX = "javascript:";
    private static final String FRAGMENT_PREFIX = "#";

    public List<URL> extractLinks(HtmlDocument document, URL baseUrl) {
        if (document == null) {
            return new ArrayList<>();
        }

        List<URL> validUrls = new ArrayList<>();
        List<HtmlElement> links = document.select(LINK_SELECTOR);

        for (HtmlElement link : links) {
            String href = link.getAttribute(HREF_ATTRIBUTE);
            if (!isValidHref(href)) {
                continue;
            }

            tryAddValidUrl(href, baseUrl, validUrls);
        }

        return validUrls;
    }

    private boolean isValidHref(String href) {
        if (href == null || href.trim().isEmpty()) {
            return false;
        }

        String lowerHref = href.toLowerCase();
        return !lowerHref.isEmpty() && !lowerHref.startsWith(JAVASCRIPT_PREFIX) && !lowerHref.startsWith(FRAGMENT_PREFIX);
    }

    private void tryAddValidUrl(String href, URL baseUrl, List<URL> validUrls) {
        URL url;
        try {
            url = new URL(baseUrl, href);
        } catch (MalformedURLException e) {
            return;
        }
        validUrls.add(url);
    }
}
