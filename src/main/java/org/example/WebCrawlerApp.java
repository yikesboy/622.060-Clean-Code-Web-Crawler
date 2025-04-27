package org.example;

import org.example.config.CrawlConfig;
import org.example.crawler.WebCrawlerService;
import org.example.crawler.WebCrawlerServiceInterface;
import org.example.models.WebPage;
import org.example.parser.PageParser;
import org.example.parser.PageParserInterface;
import org.example.report.ReportGenerator;
import org.example.report.ReportGeneratorInterface;
import org.example.util.ArgumentParser;
import org.example.util.ArgumentParserInterface;
import org.example.util.ExitStatus;

import java.nio.file.Path;
import java.nio.file.Paths;

public class WebCrawlerApp implements WebCrawlerAppInterface {
    private static final String DEFAULT_OUTPUT_FILE = "crawler-report.md";
    private final ArgumentParserInterface argumentParser;

    public WebCrawlerApp() {
        this.argumentParser = new ArgumentParser();
    }

    public WebCrawlerApp(ArgumentParserInterface argumentParser) {
        this.argumentParser = argumentParser;
    }

    public ExitStatus run(String[] args) {
        try {
            CrawlConfig config = argumentParser.parse(args);
            WebPage result = executeCrawl(config);
            return generateReport(result);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            return ExitStatus.INVALID_ARGS;
        } catch (Exception e) {
            System.err.println("Unexpected error occurred: " + e.getMessage());
            return ExitStatus.UNEXPECTED_ERROR;
        }
    }

    private WebPage executeCrawl(CrawlConfig config) {
        System.out.println("Starting crawl from " + config.rootUrl());

        PageParserInterface parser = createParser();
        WebCrawlerServiceInterface crawlerService = createCrawlerService(parser);

        return crawlerService.crawl(config);
    }

    private ExitStatus generateReport(WebPage rootPage) {
        ReportGeneratorInterface reportGenerator = creteReportGenerator();
        Path outputPath = Paths.get(DEFAULT_OUTPUT_FILE);

        boolean success = reportGenerator.generateReport(rootPage, outputPath);

        if (success) {
            System.out.println("Crawl completed successfully. Report saved to: " + outputPath.toAbsolutePath());
            return ExitStatus.SUCCESS;
        } else {
            System.out.println("Failed to generate report.");
            return ExitStatus.REPORT_FAILED;
        }
    }

    private PageParserInterface createParser() {
        return new PageParser();
    }

    private WebCrawlerServiceInterface createCrawlerService(PageParserInterface parser) {
        return new WebCrawlerService(parser);
    }

    private ReportGeneratorInterface creteReportGenerator() {
        return new ReportGenerator();
    }
}
