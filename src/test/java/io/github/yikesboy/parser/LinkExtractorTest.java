package io.github.yikesboy.parser;


import io.github.yikesboy.html.HtmlDocument;
import io.github.yikesboy.html.jsoup.JsoupHtmlDocument;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("LinkExtractor Tests")
public class LinkExtractorTest {
    private LinkExtractorInterface linkExtractor;
    private URL baseUrl;

    @BeforeEach
    void setUp() throws MalformedURLException {
        linkExtractor = new LinkExtractor();
        baseUrl = new URL("https://example.com/");
    }

    /**
     * Helper method to create an HtmlDocument from HTML string
     */
    private HtmlDocument createHtmlDocument(String html) {
        return new JsoupHtmlDocument(Jsoup.parse(html));
    }

    @Test
    @DisplayName("Should return empty list for null document")
    void shouldReturnEmptyListForNullDocument() {
        List<URL> links = linkExtractor.extractLinks(null, baseUrl);
        assertTrue(links.isEmpty());
    }

    @Test
    @DisplayName("Should extract valid links from HTML document")
    void shouldExtractValidLinksFromHtmlDocument() throws MalformedURLException {
        String html = """
                <!DOCTYPE html>
                <html>
                <body>
                    <a href="https://github.com">GitHub</a>
                    <a href="/relative/path">Relative</a>
                    <a href="sub/page.html">Subpage</a>
                </body>
                </html>
                """;

        List<URL> actualLinks = linkExtractor.extractLinks(createHtmlDocument(html), baseUrl);

        List<URL> expectedLinks = List.of(
                new URL("https://github.com"),
                new URL("https://example.com/relative/path"),
                new URL("https://example.com/sub/page.html")
        );

        assertEquals(expectedLinks.size(), actualLinks.size());
        assertTrue(actualLinks.containsAll(expectedLinks));
    }

    @Test
    @DisplayName("Should filter out JavaScript and fragment links")
    void shouldFilterOutInvalidLinks() throws Exception {
        String html = """
                <!DOCTYPE html>
                <html>
                <body>
                    <a href="https://valid.com/">Valid</a>
                    <a href="javascript:void(0)">JavaScript</a>
                    <a href="#section">Fragment</a>
                    <a href="">Empty</a>
                    <a href="  ">Whitespace</a>
                </body>
                </html>
                """;

        List<URL> links = linkExtractor.extractLinks(createHtmlDocument(html), baseUrl);

        assertEquals(1, links.size());
        assertEquals(new URL("https://valid.com/"), links.get(0));
    }

    @Test
    @DisplayName("Should handle document with no links")
    void shouldHandleDocumentWithNoLinks() {
        String html = """
                <!DOCTYPE html>
                <html>
                <body>
                    <p>This is a paragraph with no links</p>
                    <div>This is a div</div>
                </body>
                </html>
                """;

        List<URL> links = linkExtractor.extractLinks(createHtmlDocument(html), baseUrl);

        assertTrue(links.isEmpty());
    }

    @Test
    @DisplayName("Should resolve relative URLs against the base URL")
    void shouldResolveRelativeUrls() throws Exception {
        String html = """
                <!DOCTYPE html>
                <html>
                <body>
                    <a href="/top-level">Top level</a>
                    <a href="same-level">Same level</a>
                    <a href="../parent">Parent directory</a>
                    <a href="./current">Current directory</a>
                </body>
                </html>
                """;
        URL pageBaseUrl = new URL("https://example.com/directory/page.html");

        List<URL> links = linkExtractor.extractLinks(createHtmlDocument(html), pageBaseUrl);

        List<URL> expectedLinks = List.of(
                new URL("https://example.com/top-level"),
                new URL("https://example.com/directory/same-level"),
                new URL("https://example.com/parent"),
                new URL("https://example.com/directory/current")
        );
        assertEquals(expectedLinks.size(), links.size());
        assertTrue(links.containsAll(expectedLinks));
    }
}
