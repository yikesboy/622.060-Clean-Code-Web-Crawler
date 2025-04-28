package io.github.yikesboy.parser;

import io.github.yikesboy.html.HtmlDocument;
import io.github.yikesboy.html.jsoup.JsoupHtmlDocument;
import io.github.yikesboy.models.Heading;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("HeadingExtractor Tests")
public class HeadingExtractorTest {
    private HeadingExtractor headingExtractor;

    @BeforeEach
    void setUp() {
        headingExtractor = new HeadingExtractor();
    }

    /**
     * Helper method to create an HtmlDocument from HTML string
     */
    private HtmlDocument createHtmlDocument(String html) {
        return new JsoupHtmlDocument(Jsoup.parse(html));
    }

    @Test
    @DisplayName("Should return empty list for null document")
    void shouldReturnEmptyArrayListForNullDocument() {
        List<Heading> headings = headingExtractor.extractHeadings(null);
        assertTrue(headings.isEmpty());
    }

    @Test
    @DisplayName("Should return empty ArrayList()")
    void shouldExtractHeadingsFromHtmlDocument() {
        String html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Sample Input</title>
                </head>
                <body>
                    <h1>First heading</h1>
                    <h2>Second heading</h2>
                    <h3>Third heading</h3>
                    <h4>Fourth heading</h4>
                    <h5>Fifth heading</h5>
                    <h6>Sixth heading</h6>
                </body>
                </html>
                """;
        List<Heading> actualHeadings = headingExtractor.extractHeadings(createHtmlDocument(html));

        List<Heading> expectedHeadings = List.of(
                new Heading(1, "First heading"),
                new Heading(2, "Second heading"),
                new Heading(3, "Third heading"),
                new Heading(4, "Fourth heading"),
                new Heading(5, "Fifth heading"),
                new Heading(6, "Sixth heading")
        );
        assertEquals(expectedHeadings, actualHeadings);
    }

    @Test
    @DisplayName("Should handle document with no headings")
    void shouldHandleDocumentWithNoHeadings() {
        String html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>No Headings</title>
                </head>
                <body>
                    <p>This is a paragraph</p>
                    <div>This is a div</div>
                </body>
                </html>
                """;
        List<Heading> headings = headingExtractor.extractHeadings(createHtmlDocument(html));

        assertTrue(headings.isEmpty());
    }

    @Test
    @DisplayName("Should preserve heading order")
    void shouldPreserveHeadingOrder() {
        String html = """
                <!DOCTYPE html>
                <html>
                <body>
                    <h3>Level 3</h3>
                    <h1>Level 1</h1>
                    <h6>Level 6</h6>
                </body>
                </html>
                """;
        List<Heading> headings = headingExtractor.extractHeadings(createHtmlDocument(html));

        assertEquals(3, headings.size());
        assertEquals(new Heading(3, "Level 3"), headings.get(0));
        assertEquals(new Heading(1, "Level 1"), headings.get(1));
        assertEquals(new Heading(6, "Level 6"), headings.get(2));
    }
}
