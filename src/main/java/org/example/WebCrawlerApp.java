package org.example;

import org.example.config.CrawlConfig;
import org.example.crawler.WebCrawlerService;
import org.example.crawler.WebCrawlerServiceInterface;
import org.example.models.WebPage;
import org.example.parser.PageParser;
import org.example.parser.PageParserInterface;
import org.example.report.ReportGenerator;
import org.example.report.ReportGeneratorInterface;
import org.example.util.ExitStatus;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class WebCrawlerApp {
    private static final String DEFAULT_OUTPUT_FILE = "crawler-report.md";

    public ExitStatus run(String[] args) {
        try {
            CrawlConfig config = parseArgs(args);
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

    private CrawlConfig parseArgs(String[] args) {
        if (args.length < 3) {
            throw new IllegalArgumentException("Missing required arguments.");
        }

        try {
            URL url = new URL(args[0]);
            int depth = parseDepth(args[1]);
            Set<String> domains = new HashSet<>(Arrays.asList(args[2].split(",")));

            return new CrawlConfig(url, depth, domains);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL provided.");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Depth must be a valid integer.");
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

    private int parseDepth(String depthArgument) {
        try {
            int depth = Integer.parseInt(depthArgument);
            if (depth < 0) {
                throw new IllegalArgumentException("Depth must be non-negative.");
            }
            return depth;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Depth must be a valid integer.");
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
