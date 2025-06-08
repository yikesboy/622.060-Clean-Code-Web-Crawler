package io.github.yikesboy.app;

import io.github.yikesboy.config.CrawlConfig;
import io.github.yikesboy.crawler.WebCrawlerServiceInterface;
import io.github.yikesboy.models.CrawlResult;
import io.github.yikesboy.models.WebPage;
import io.github.yikesboy.report.ReportGeneratorInterface;
import io.github.yikesboy.util.ArgumentParserInterface;
import io.github.yikesboy.util.ExitStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WebCrawlerApp Tests")
public class WebCrawlerAppTest {
    @Mock
    private ArgumentParserInterface argumentParser;
    @Mock
    private WebCrawlerServiceInterface crawlerService;
    @Mock
    private ReportGeneratorInterface reportGenerator;

    private WebCrawlerApp webCrawlerApp;
    private CrawlConfig testConfig;
    private WebPage testRootPage;
    private CrawlResult testCrawlResult;

    @BeforeEach
    void setUp() throws MalformedURLException {
        webCrawlerApp = new WebCrawlerApp(argumentParser, crawlerService, reportGenerator);
        URL testUrl = new URL("https://github.com");
        testConfig = new CrawlConfig(testUrl, 2, Set.of("https://github.com"));
        testRootPage = new WebPage(testUrl, 0, false);
        testCrawlResult = new CrawlResult(testRootPage, Collections.emptyList());
    }

    @Test
    @DisplayName("Should return SUCCESS status for successful crawl and report")
    void shouldReturnSuccessStatus() {
        String[] args = {"https://github.com", "2", "https://github.com"};
        when(argumentParser.parse(args)).thenReturn(testConfig);
        when(crawlerService.crawl(testConfig)).thenReturn(testCrawlResult);
        when(reportGenerator.generateReport(any(WebPage.class), anyList(), any(Path.class)))
                .thenReturn(true);

        ExitStatus result = webCrawlerApp.run(args);

        assertEquals(ExitStatus.SUCCESS, result);
        verify(argumentParser).parse(args);
        verify(crawlerService).crawl(testConfig);
        verify(reportGenerator).generateReport(any(WebPage.class), anyList(), any(Path.class));
    }

    @Test
    @DisplayName("Should return INVALID_ARGS status when argument parsing fails")
    void shouldReturnInvalidArgsStatus() {
        String[] args = {"invalid"};
        when(argumentParser.parse(args)).thenThrow(new IllegalArgumentException("Invalid args"));

        ExitStatus result = webCrawlerApp.run(args);

        assertEquals(ExitStatus.INVALID_ARGS, result);
        verify(argumentParser).parse(args);
        verifyNoInteractions(crawlerService);
        verifyNoInteractions(reportGenerator);
    }

    @Test
    @DisplayName("Should return UNEXPECTED_ERROR status on general exception")
    void shouldReturnUnexpectedErrorStatus() {
        String[] args = {"https://github.com", "2", "https://github.com"};
        when(argumentParser.parse(args)).thenReturn(testConfig);
        when(crawlerService.crawl(testConfig)).thenThrow(new RuntimeException("Unexpected error"));

        ExitStatus result = webCrawlerApp.run(args);

        assertEquals(ExitStatus.UNEXPECTED_ERROR, result);
        verify(argumentParser).parse(args);
        verify(crawlerService).crawl(testConfig);
        verifyNoInteractions(reportGenerator);
    }

    @Test
    @DisplayName("Should return REPORT_FAILED status when report generation fails")
    void shouldReturnReportFailedStatus() {
        String[] args = {"https://github.com", "2", "https://github.com"};
        when(argumentParser.parse(args)).thenReturn(testConfig);
        when(crawlerService.crawl(testConfig)).thenReturn(testCrawlResult);
        when(reportGenerator.generateReport(any(WebPage.class), anyList(), any(Path.class)))
                .thenReturn(false);

        ExitStatus result = webCrawlerApp.run(args);

        assertEquals(ExitStatus.REPORT_FAILED, result);
        verify(argumentParser).parse(args);
        verify(crawlerService).crawl(testConfig);
        verify(reportGenerator).generateReport(any(WebPage.class), anyList(), any(Path.class));

    }

    @Test
    @DisplayName("Should create WebCrawlerApp with default constructor")
    void shouldCreateWithDefaultConstructor() {
        WebCrawlerApp app = new WebCrawlerApp();

        ExitStatus result = app.run(new String[]{"invalid"});

        assertNotNull(app);
        assertEquals(ExitStatus.INVALID_ARGS, result);
    }
}
