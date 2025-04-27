package io.github.yikesboy.parser;

import io.github.yikesboy.html.HtmlDocument;
import io.github.yikesboy.html.HtmlElement;
import io.github.yikesboy.models.Heading;

import java.util.ArrayList;
import java.util.List;

public class HeadingExtractor {
    private static final String HEADLINE_SELECTOR_QUERY = "h1, h2, h3, h4, h5, h6";

    public List<Heading> extractHeadings(HtmlDocument document) {
        if (document == null) {
            return new ArrayList<>();
        }

        List<Heading> headings = new ArrayList<>();
        List<HtmlElement> elements = document.select(HEADLINE_SELECTOR_QUERY);

        for (HtmlElement element : elements) {
            String tagName = element.getTagName();
            int level = Integer.parseInt(tagName.substring(1));
            String text = element.getText();
            headings.add(new Heading(level, text));
        }

        return headings;
    }
}
