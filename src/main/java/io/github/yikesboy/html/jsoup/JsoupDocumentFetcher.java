package io.github.yikesboy.html.jsoup;

import io.github.yikesboy.html.HtmlDocument;
import io.github.yikesboy.html.HtmlDocumentFetcher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;

public class JsoupDocumentFetcher implements HtmlDocumentFetcher {
    private final int timeoutMs;

    public JsoupDocumentFetcher(int timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    @Override
    public HtmlDocument fetch(URL url) throws IOException {
        if (url == null) {
            throw new IllegalArgumentException("Url cannot be null");
        }

        Document document = Jsoup.connect(url.toString()).timeout(timeoutMs).get();
        return new JsoupHtmlDocument(document);
    }
}
