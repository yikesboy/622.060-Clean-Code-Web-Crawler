package io.github.yikesboy.report;

import io.github.yikesboy.models.Heading;
import io.github.yikesboy.models.WebPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ReportGenerator Tests")
public class ReportGeneratorTest {
    private ReportGeneratorInterface reportGenerator;
    private URL rootUrl;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws MalformedURLException {
        reportGenerator = new ReportGenerator();
        rootUrl = new URL("https://github.com");
    }

    @Test
    @DisplayName("Should generate report for simple page with no children")
    void shouldGenerateReportForSimplePage() throws IOException {
        WebPage page = createSimplePage();
        Path outputPath = tempDir.resolve("simple-report.md");

        boolean result = reportGenerator.generateReport(page, outputPath);

        assertTrue(result);
        assertTrue(Files.exists(outputPath));
        String content = Files.readString(outputPath);
        assertTrue(content.contains("input: <a>https://github.com</a>"));
        assertTrue(content.contains("depth: 0"));
        assertTrue(content.contains("# Test Heading"));
    }

    @Test
    @DisplayName("Should generate report with nested child pages")
    void shouldGenerateReportWithNestedPages() throws IOException {
        WebPage rootPage = createPageWithChildren();
        Path outputPath = tempDir.resolve("nested-report.md");

        boolean result = reportGenerator.generateReport(rootPage, outputPath);

        assertTrue(result);
        String content = Files.readString(outputPath);
        assertTrue(content.contains("input: <a>https://github.com</a>"));
        assertTrue(content.contains("-->"));
        assertTrue(content.contains("link to <a>https://github.com/child</a>"));
        assertTrue(content.contains("## --> Child Heading"));
    }

    @Test
    @DisplayName("Should indicate broken links in report")
    void shouldIndicateBrokenLinks() throws IOException {
        WebPage rootPage = createPageWithBrokenChild();
        Path outputPath = tempDir.resolve("broken-links-report.md");

        boolean result = reportGenerator.generateReport(rootPage, outputPath);

        assertTrue(result);
        String content = Files.readString(outputPath);
        assertTrue(content.contains("broken link <a>https://github.com/broken</a>"));
        assertFalse(content.contains("depth: 1") && content.contains("broken link"));
    }

    @Test
    @DisplayName("Should handle page with multiple headings")
    void shouldHandleMultipleHeadings() throws IOException {
        WebPage page = createPageWithMultipleHeadings();
        Path outputPath = tempDir.resolve("multiple-headings-report.md");

        boolean result = reportGenerator.generateReport(page, outputPath);

        assertTrue(result);
        String content = Files.readString(outputPath);
        assertTrue(content.contains("# Heading 1"));
        assertTrue(content.contains("## Heading 2"));
        assertTrue(content.contains("### Heading 3"));
    }

    @Test
    @DisplayName("Should return false when writing fails")
    void shouldReturnFalseWhenWritingFails() {
        WebPage page = createSimplePage();
        Path invalidPath = Path.of("/invalid/directory/report.md");

        boolean result = reportGenerator.generateReport(page, invalidPath);

        assertFalse(result);
    }

    @Test
    @DisplayName("Should create ReportGenerator with default constructor")
    void shouldCreateWithDefaultConstructor() {
        ReportGenerator generator = new ReportGenerator();

        WebPage page = createSimplePage();
        Path tempFile = tempDir.resolve("test-report.md");

        assertNotNull(generator);
        boolean result = generator.generateReport(page, tempFile);
        assertTrue(result, "ReportGenerator should be able to generate a report");
        assertTrue(Files.exists(tempFile), "Report file should exist");
    }

    /**
     * Helper method that returns a simple WebPage
     */
    private WebPage createSimplePage() {
        List<Heading> headings = Collections.singletonList(new Heading(1, "Test Heading"));
        return new WebPage(rootUrl, headings, 0, null);
    }

    /**
     * Helper method that returns a WebPage with a child WebPage
     */
    private WebPage createPageWithChildren() throws MalformedURLException {
        WebPage rootPage = createSimplePage();

        URL childUrl = new URL("https://github.com/child");
        List<Heading> childHeadings = Collections.singletonList(new Heading(2, "Child Heading"));
        WebPage childPage = new WebPage(childUrl, childHeadings, 1, null);

        rootPage.addChildPage(childPage);
        return rootPage;
    }

    /**
     * Helper method that returns a WebPage with a broken child WebPage
     */
    private WebPage createPageWithBrokenChild() throws MalformedURLException {
        WebPage rootPage = createSimplePage();

        URL brokenUrl = new URL("https://github.com/broken");
        WebPage brokenPage = new WebPage(brokenUrl, 1, true);

        rootPage.addChildPage(brokenPage);
        return rootPage;
    }

    /**
     * Helper method that returns a WebPage with multiple Headings
     */
    private WebPage createPageWithMultipleHeadings() {
        List<Heading> headings = Arrays.asList(
                new Heading(1, "Heading 1"),
                new Heading(2, "Heading 2"),
                new Heading(3, "Heading 3")
        );
        return new WebPage(rootUrl, headings, 0, null);
    }
}
