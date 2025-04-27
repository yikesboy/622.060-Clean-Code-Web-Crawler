package io.github.yikesboy.html;

public interface HtmlElement {
    String getTagName();

    String getText();

    String getAttribute(String attributeName);
}
