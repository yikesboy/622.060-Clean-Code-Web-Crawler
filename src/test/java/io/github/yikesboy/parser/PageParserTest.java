package io.github.yikesboy.parser;

import io.github.yikesboy.html.HtmlDocument;
import io.github.yikesboy.html.HtmlDocumentFetcher;
import io.github.yikesboy.models.Heading;
import io.github.yikesboy.models.WebPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PageParser Tests")
public class PageParserTest {
    @Mock
    private HtmlDocumentFetcher documentFetcher;
    @Mock
    private HeadingExtractorInterface headingExtractor;
    @Mock
    private LinkExtractorInterface linkExtractor;
    @Mock
    HtmlDocument htmlDocument;

    private PageParser pageParser;
    private URL testUrl;

    @BeforeEach
    void setUp() throws MalformedURLException {
        pageParser = new PageParser(documentFetcher, headingExtractor, linkExtractor);
        testUrl = new URL("https://github.com");
    }

    @Test
    @DisplayName("Should create complete WebPage on successful fetch")
    void shouldCreateCompleteWebPageOnSuccess() throws IOException {
        List<Heading> headings = List.of(new Heading(1, "Test Heading"));
        when(documentFetcher.fetch(testUrl)).thenReturn(htmlDocument);
        when(headingExtractor.extractHeadings(htmlDocument)).thenReturn(headings);

        WebPage result = pageParser.parse(testUrl, 2);

        assertFalse(result.isBroken());
        assertEquals(testUrl, result.getUrl());
        assertEquals(2, result.getDepth());
        assertEquals(htmlDocument, result.getDocument());
        assertEquals(headings, result.getHeadings());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for null URL")
    void shouldThrowForNullUrl() {
        assertThrows(IllegalArgumentException.class, () -> pageParser.parse(null, 0));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for negative Depth")
    void shouldThrowForNegativeDepth() {
        assertThrows(IllegalArgumentException.class, () -> pageParser.parse(testUrl, -1));
    }

    @Test
    @DisplayName("Should create WebPage with error flag when exception occurs")
    void shouldCreateBrokenPageWhenExceptionOccurs() throws IOException {
        when(documentFetcher.fetch(testUrl)).thenThrow(new IOException("Connection failed"));

        WebPage result = pageParser.parse(testUrl, 1);

        assertTrue(result.isBroken());
        assertEquals(testUrl, result.getUrl());
        assertEquals(1, result.getDepth());
        assertNull(result.getDocument());
    }


    @Test
    @DisplayName("Should return empty list when extracting links from null page")
    void shouldReturnEmptyListForNullPage() {
        List<URL> result = pageParser.extractLinks(null);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when extracting links from broken page")
    void shouldReturnEmptyListForBrokenPage() {
        WebPage brokenPage = new WebPage(testUrl, 0, true);

        List<URL> result = pageParser.extractLinks(brokenPage);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should extract links from valid page")
    void shouldExtractLinksFromValidPage() throws Exception {
        WebPage validPage = new WebPage(testUrl, List.of(), 0, htmlDocument);
        List<URL> expectedLinks = List.of(new URL("https://example.com/page1"));
        when(linkExtractor.extractLinks(htmlDocument, testUrl)).thenReturn(expectedLinks);

        List<URL> result = pageParser.extractLinks(validPage);

        assertEquals(expectedLinks, result);
        verify(linkExtractor).extractLinks(htmlDocument, testUrl);
    }
}
