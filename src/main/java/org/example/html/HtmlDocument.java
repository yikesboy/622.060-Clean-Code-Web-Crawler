package org.example.html;

import java.util.List;

public interface HtmlDocument {
    List<HtmlElement> select(String cssSelector);

    String getTitle();
}
