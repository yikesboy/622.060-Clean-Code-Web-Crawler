package io.github.yikesboy.html.jsoup;

import io.github.yikesboy.html.HtmlElement;
import org.jsoup.nodes.Element;

public class JsoupHtmlElement implements HtmlElement {
    private final Element element;

    public JsoupHtmlElement(Element element) {
        this.element = element;
    }

    @Override
    public String getTagName() {
        return element.tagName();
    }

    @Override
    public String getText() {
        return element.text().trim();
    }

    @Override
    public String getAttribute(String attributeName) {
        return element.attr(attributeName);
    }
}
