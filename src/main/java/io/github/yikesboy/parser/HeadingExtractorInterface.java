package io.github.yikesboy.parser;

import io.github.yikesboy.html.HtmlDocument;
import io.github.yikesboy.models.Heading;

import java.util.List;

public interface HeadingExtractorInterface {
    List<Heading> extractHeadings(HtmlDocument document);
}