package io.github.yikesboy.html.jsoup;

import io.github.yikesboy.html.HtmlDocument;
import io.github.yikesboy.html.HtmlElement;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class JsoupHtmlDocument implements HtmlDocument {
    private final Document document;

    public JsoupHtmlDocument(Document document) {
        this.document = document;
    }

    @Override
    public List<HtmlElement> select(String cssSelector) {
        Elements elements = document.select(cssSelector);
        List<HtmlElement> htmlElements = new ArrayList<>();

        for (Element element : elements) {
            htmlElements.add(new JsoupHtmlElement(element));
        }
        return htmlElements;
    }

    @Override
    public String getTitle() {
        return document.title();
    }
}
