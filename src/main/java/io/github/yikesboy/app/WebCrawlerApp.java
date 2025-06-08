package io.github.yikesboy.app;

import io.github.yikesboy.config.CrawlConfig;
import io.github.yikesboy.crawler.WebCrawlerService;
import io.github.yikesboy.crawler.WebCrawlerServiceInterface;
import io.github.yikesboy.models.CrawlResult;
import io.github.yikesboy.parser.PageParser;
import io.github.yikesboy.report.ReportGenerator;
import io.github.yikesboy.report.ReportGeneratorInterface;
import io.github.yikesboy.util.ArgumentParser;
import io.github.yikesboy.util.ArgumentParserInterface;
import io.github.yikesboy.util.ExitStatus;

import java.nio.file.Path;
import java.nio.file.Paths;

public class WebCrawlerApp implements WebCrawlerAppInterface {
    private static final String DEFAULT_OUTPUT_FILE = "crawler-report.md";

    private final ArgumentParserInterface argumentParser;
    private final WebCrawlerServiceInterface crawlerService;
    private final ReportGeneratorInterface reportGenerator;

    public WebCrawlerApp() {
        this(
                new ArgumentParser(),
                new WebCrawlerService(new PageParser()),
                new ReportGenerator()
        );
    }

    public WebCrawlerApp(
            ArgumentParserInterface argumentParser,
            WebCrawlerServiceInterface crawlerService,
            ReportGeneratorInterface reportGenerator) {
        this.argumentParser = argumentParser;
        this.crawlerService = crawlerService;
        this.reportGenerator = reportGenerator;
    }

    public ExitStatus run(String[] args) {
        try {
            CrawlConfig config = argumentParser.parse(args);
            CrawlResult result = executeCrawl(config);
            return generateReport(result);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            return ExitStatus.INVALID_ARGS;
        } catch (Exception e) {
            System.err.println("Unexpected error occurred: " + e.getMessage());
            return ExitStatus.UNEXPECTED_ERROR;
        }
    }

    private CrawlResult executeCrawl(CrawlConfig config) {
        System.out.println("Starting crawl from " + config.rootUrl());
        return crawlerService.crawl(config);
    }

    private ExitStatus generateReport(CrawlResult result) {
        Path outputPath = Paths.get(DEFAULT_OUTPUT_FILE);
        boolean success = reportGenerator.generateReport(result.rootPage(), result.errors(), outputPath);

        if (success) {
            System.out.println("Crawl completed successfully. Report saved to: " + outputPath.toAbsolutePath());
            return ExitStatus.SUCCESS;
        } else {
            System.out.println("Failed to generate report.");
            return ExitStatus.REPORT_FAILED;
        }
    }
}
