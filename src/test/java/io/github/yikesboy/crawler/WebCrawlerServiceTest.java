package io.github.yikesboy.crawler;

import io.github.yikesboy.config.CrawlConfig;
import io.github.yikesboy.models.WebPage;
import io.github.yikesboy.parser.PageParserInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WebCrawlerService Tests")
public class WebCrawlerServiceTest {
    @Mock
    private PageParserInterface mockParser;

    private WebCrawlerService crawlerService;
    private URL rootUrl;
    private CrawlConfig config;

    @BeforeEach
    void setUp() throws MalformedURLException {
        crawlerService = new WebCrawlerService(mockParser);
        rootUrl = new URL("https://github.com");
        config = new CrawlConfig(rootUrl, 2, Set.of("github.com"));
    }

    @Test
    @DisplayName("Should throw exception for null config")
    void shouldThrowExceptionForNullConfig() {
        assertThrows(IllegalArgumentException.class, () -> crawlerService.crawl(null));
    }

    @Test
    @DisplayName("Should crawl single page with no links")
    void shouldCrawlSinglePageWithNoLinks() {
        WebPage rootPage = new WebPage(rootUrl, 0, false);

        when(mockParser.parse(rootUrl, 0)).thenReturn(rootPage);
        when(mockParser.extractLinks(rootPage)).thenReturn(new ArrayList<>());

        WebPage result = crawlerService.crawl(config);

        assertNotNull(result);
        assertEquals(rootUrl, result.getUrl());
        assertEquals(0, result.getDepth());
        assertTrue(result.getChildPages().isEmpty());

        verify(mockParser).parse(rootUrl, 0);
        verify(mockParser).extractLinks(rootPage);
        verifyNoMoreInteractions(mockParser);
    }

    @Test
    @DisplayName("Should respect max depth")
    void shouldRespectMaxDepth() {
        WebPage rootPage = new WebPage(rootUrl, 0, false);

        config = new CrawlConfig(rootUrl, 0, Set.of("github.com"));

        when(mockParser.parse(rootUrl, 0)).thenReturn(rootPage);

        WebPage result = crawlerService.crawl(config);

        assertNotNull(result);
        assertTrue(result.getChildPages().isEmpty());

        verify(mockParser).parse(rootUrl, 0);
        verifyNoMoreInteractions(mockParser);
    }

    @Test
    @DisplayName("Should crawl child pages")
    void shouldCrawlChildPages() throws MalformedURLException {
        WebPage rootPage = new WebPage(rootUrl, 0, false);
        URL childUrl = new URL("https://github.com/child");
        WebPage childPage = new WebPage(childUrl, 1, false);
        List<URL> childLinks = List.of(childUrl);

        when(mockParser.parse(rootUrl, 0)).thenReturn(rootPage);
        when(mockParser.extractLinks(rootPage)).thenReturn(childLinks);
        when(mockParser.parse(childUrl, 1)).thenReturn(childPage);
        when(mockParser.extractLinks(childPage)).thenReturn(new ArrayList<>());

        WebPage result = crawlerService.crawl(config);

        assertNotNull(result);
        assertEquals(1, result.getChildPages().size());
        assertEquals(childUrl, result.getChildPages().get(0).getUrl());

        verify(mockParser).parse(rootUrl, 0);
        verify(mockParser).extractLinks(rootPage);
        verify(mockParser).parse(childUrl, 1);
        verify(mockParser).extractLinks(childPage);
        verifyNoMoreInteractions(mockParser);
    }

    @Test
    @DisplayName("Should skip already visited URLs")
    void shouldSkipAlreadyVisitedUrls() throws MalformedURLException {
        WebPage rootPage = new WebPage(rootUrl, 0, false);
        URL childUrl = new URL("https://github.com/child");
        WebPage childPage = new WebPage(childUrl, 1, false);

        List<URL> rootLinks = List.of(childUrl);
        List<URL> childLinks = List.of(rootUrl);

        when(mockParser.parse(rootUrl, 0)).thenReturn(rootPage);
        when(mockParser.extractLinks(rootPage)).thenReturn(rootLinks);
        when(mockParser.parse(childUrl, 1)).thenReturn(childPage);
        when(mockParser.extractLinks(childPage)).thenReturn(childLinks);

        WebPage result = crawlerService.crawl(config);

        assertNotNull(result);
        assertEquals(1, result.getChildPages().size());

        verify(mockParser, times(1)).parse(rootUrl, 0);
    }

    @Test
    @DisplayName("Should stop at broken pages")
    void shouldStopAtBrokenPages() throws MalformedURLException {
        WebPage rootPage = new WebPage(rootUrl, 0, false);
        URL childUrl = new URL("https://github.com/broken");
        WebPage brokenChildPage = new WebPage(childUrl, 1, true);

        List<URL> rootLinks = List.of(childUrl);

        when(mockParser.parse(rootUrl, 0)).thenReturn(rootPage);
        when(mockParser.extractLinks(rootPage)).thenReturn(rootLinks);
        when(mockParser.parse(childUrl, 1)).thenReturn(brokenChildPage);

        WebPage result = crawlerService.crawl(config);

        assertNotNull(result);
        assertEquals(1, result.getChildPages().size());
        assertTrue(result.getChildPages().get(0).isBroken());

        verify(mockParser).parse(rootUrl, 0);
        verify(mockParser).extractLinks(rootPage);
        verify(mockParser).parse(childUrl, 1);
        verify(mockParser, never()).extractLinks(brokenChildPage);
        verifyNoMoreInteractions(mockParser);
    }

    @Test
    @DisplayName("Should filter by allowed domains")
    void shouldFilterByAllowedDomains() throws MalformedURLException {
        WebPage rootPage = new WebPage(rootUrl, 0, false);

        URL allowedUrl = new URL("https://github.com/valid");
        WebPage allowedPage = new WebPage(allowedUrl, 1, false);

        Set<String> allowedDomains = new HashSet<>();
        allowedDomains.add("github.com");

        CrawlConfig testConfig = new CrawlConfig(rootUrl, 3, allowedDomains);

        when(mockParser.parse(rootUrl, 0)).thenReturn(rootPage);
        when(mockParser.extractLinks(rootPage)).thenReturn(List.of(allowedUrl));
        when(mockParser.parse(allowedUrl, 1)).thenReturn(allowedPage);
        when(mockParser.extractLinks(allowedPage)).thenReturn(new ArrayList<>());

        WebPage result = crawlerService.crawl(testConfig);

        assertNotNull(result);
        assertEquals(1, result.getChildPages().size());
        verify(mockParser).parse(allowedUrl, 1);
    }

    @Test
    @DisplayName("Should handle null links returned by parser")
    void shouldHandleNullLinks() throws MalformedURLException {
        WebPage rootPage = new WebPage(rootUrl, 0, false);
        URL validUrl = new URL("https://github.com/valid");
        WebPage validPage = new WebPage(validUrl, 1, false);

        when(mockParser.parse(rootUrl, 0)).thenReturn(rootPage);
        when(mockParser.extractLinks(rootPage)).thenReturn(Arrays.asList(null, validUrl));
        when(mockParser.parse(validUrl, 1)).thenReturn(validPage);

        when(mockParser.extractLinks(validPage)).thenReturn(new ArrayList<>());

        WebPage result = crawlerService.crawl(config);

        assertNotNull(result);
        assertEquals(1, result.getChildPages().size());
        verify(mockParser).parse(validUrl, 1);
    }

    @Test
    @DisplayName("Should create WebCrawlerService with default constructor")
    void shouldCreateWithDefaultConstructor() {
        WebCrawlerService service = new WebCrawlerService(mockParser);
        assertNotNull(service);
    }
}
