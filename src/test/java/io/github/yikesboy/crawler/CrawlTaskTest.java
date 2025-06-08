package io.github.yikesboy.crawler;

import io.github.yikesboy.config.CrawlConfig;
import io.github.yikesboy.models.CrawlError;
import io.github.yikesboy.models.WebPage;
import io.github.yikesboy.parser.PageParserInterface;
import io.github.yikesboy.util.UrlUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CrawlTaskTest {
    @Mock
    private PageParserInterface mockParser;

    private URL testUrl;
    private CrawlConfig testConfig;
    private Set<String> visitedUrls;
    private Queue<CrawlError> errors;

    @BeforeEach
    void setUp() throws MalformedURLException {
        testUrl = new URL("https://github.com");
        testConfig = new CrawlConfig(testUrl, 2, Set.of("github.com"));
        visitedUrls = new HashSet<>();
        errors = new ConcurrentLinkedQueue<>();
    }

    @Test
    @DisplayName("Should return null when URL is null")
    void shouldReturnNullWhenURLIsNull() {
        CrawlTask task = new CrawlTask(null, 0, testConfig, mockParser, visitedUrls, errors);

        WebPage result = task.compute();

        assertNull(result);
        assertTrue(visitedUrls.isEmpty());
        assertTrue(errors.isEmpty());
    }

    @Test
    @DisplayName("Should return null when current depth exceeds maxDepth")
    void shouldReturnNullWhenCurrenDepthExceedsMaxDepth() throws MalformedURLException {
        URL url = new URL("https://github.com");
        CrawlTask task = new CrawlTask(url, 3, testConfig, mockParser, visitedUrls, errors);

        WebPage result = task.compute();

        assertNull(result);
        assertTrue(errors.isEmpty());
    }

    @Test
    @DisplayName("Should return null when URL is already visited")
    void shouldReturnNullWhenUrlAlreadyVisited() throws MalformedURLException {
        URL url = new URL("https://github.com");
        String normalizedUrl = UrlUtil.normalizeUrl(url);
        visitedUrls.add(normalizedUrl);
        CrawlTask task = new CrawlTask(url, 0, testConfig, mockParser, visitedUrls, errors);

        WebPage result = task.compute();

        assertNull(result);
        assertTrue(errors.isEmpty());
    }

    @Test
    @DisplayName("Should ignore forbidden URLs")
    void shouldIgnoreForbiddenUrls() throws MalformedURLException {
        URL url = new URL("https://forbidden.com");
        WebPage mockPage = new WebPage(url, 0, false);
        List<URL> mockLinks = List.of(
                new URL("https://forbidden.com/page")
        );
        when(mockParser.parse(url, 0)).thenReturn(mockPage);
        when(mockParser.extractLinks(mockPage)).thenReturn(mockLinks);

        CrawlTask task = new CrawlTask(url, 0, testConfig, mockParser, visitedUrls, errors);
        WebPage result = task.compute();

        assertNotNull(result);
        assertTrue(result.getChildPages().isEmpty());
        assertTrue(errors.isEmpty());
    }
}
