package io.github.yikesboy.html;

import java.util.List;

public interface HtmlDocument {
    List<HtmlElement> select(String cssSelector);

    String getTitle();
}
