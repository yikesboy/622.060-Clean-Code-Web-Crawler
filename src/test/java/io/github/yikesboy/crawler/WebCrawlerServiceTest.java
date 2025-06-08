package io.github.yikesboy.crawler;

import io.github.yikesboy.config.CrawlConfig;
import io.github.yikesboy.models.CrawlResult;
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
    @DisplayName("Should return CrawlResult with root page")
    void shouldReturnCrawlResultWithRootPage() {
        WebPage rootPage = new WebPage(rootUrl, 0, false);

        when(mockParser.parse(rootUrl, 0)).thenReturn(rootPage);
        when(mockParser.extractLinks(rootPage)).thenReturn(new ArrayList<>());

        CrawlResult result = crawlerService.crawl(config);

        assertNotNull(result);
        assertNotNull(result.rootPage());
        assertEquals(rootUrl, result.rootPage().getUrl());
        assertEquals(0, result.rootPage().getDepth());
        assertTrue(result.errors().isEmpty());
    }

    @Test
    @DisplayName("Should handle single page crawl")
    void shouldHandleSinglePageCrawl() {
        WebPage rootPage = new WebPage(rootUrl, 0, false);
        CrawlConfig singlePageConfig = new CrawlConfig(rootUrl, 0, Set.of("github.com"));

        when(mockParser.parse(rootUrl, 0)).thenReturn(rootPage);

        CrawlResult result = crawlerService.crawl(singlePageConfig);

        assertNotNull(result);
        assertNotNull(result.rootPage());
        assertTrue(result.rootPage().getChildPages().isEmpty());
        verify(mockParser).parse(rootUrl, 0);
    }

    @Test
    @DisplayName("Should create WebCrawlerService with valid parser")
    void shouldCreateWithValidParser() {
        WebCrawlerService service = new WebCrawlerService(mockParser);
        assertNotNull(service);
    }

    @Test
    @DisplayName("Should handle broken root page")
    void shouldHandleBrokenRootPage() {
        WebPage brokenRootPage = new WebPage(rootUrl, 0, true);

        when(mockParser.parse(rootUrl, 0)).thenReturn(brokenRootPage);

        CrawlResult result = crawlerService.crawl(config);

        assertNotNull(result);
        assertTrue(result.rootPage().isBroken());
        assertTrue(result.rootPage().getChildPages().isEmpty());
        verify(mockParser).parse(rootUrl, 0);
        verify(mockParser, never()).extractLinks(any());
    }

    @Test
    @DisplayName("Should clear state between crawls")
    void shouldClearStateBetweenCrawls() throws MalformedURLException {
        WebPage rootPage = new WebPage(rootUrl, 0, false);
        URL secondUrl = new URL("https://example.com");
        WebPage rootPage2 = new WebPage(secondUrl, 0, false);
        CrawlConfig secondConfig = new CrawlConfig(secondUrl, 1, Set.of("example.com"));

        when(mockParser.parse(rootUrl, 0)).thenReturn(rootPage);
        when(mockParser.extractLinks(rootPage)).thenReturn(new ArrayList<>());
        when(mockParser.parse(secondUrl, 0)).thenReturn(rootPage2);
        when(mockParser.extractLinks(rootPage2)).thenReturn(new ArrayList<>());

        CrawlResult firstResult = crawlerService.crawl(config);
        CrawlResult secondResult = crawlerService.crawl(secondConfig);

        assertNotNull(firstResult);
        assertNotNull(secondResult);
        assertEquals(rootUrl, firstResult.rootPage().getUrl());
        assertEquals(secondUrl, secondResult.rootPage().getUrl());
    }
}
