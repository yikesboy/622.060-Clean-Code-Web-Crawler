package org.example;

import org.example.config.CrawlConfig;
import org.example.crawler.WebCrawlerService;
import org.example.models.WebPage;
import org.example.parser.PageParser;
import org.example.report.ReportGenerator;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class WebCrawler {
    private static final String DEFAULT_OUTPUT_FILE = "crawler-report.md";

    public static void main(String[] args) {
        try {
            CrawlConfig config = parseArgs(args);

            PageParser parser = new PageParser();
            WebCrawlerService crawlerService = new WebCrawlerService(parser);
            ReportGenerator reportGenerator = new ReportGenerator();

            System.out.println("Starting crawl from " + config.rootUrl());
            WebPage rootPage = crawlerService.crawl(config);

            Path outputPath = Paths.get(DEFAULT_OUTPUT_FILE);
            boolean success = reportGenerator.generateReport(rootPage, outputPath);

            if (success) {
                System.out.println("Crawl completed successfully. Report saved to: " + outputPath.toAbsolutePath());
            } else {
                System.err.println("Failed to complete crawl. No report generated.");
                System.exit(3);
            }

        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            System.out.println("\nUsage: java -jar webcrawler.jar <URL> <depth> <domains>");
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Unexpected error occurred: " + e.getMessage());
            System.exit(2);
        }
    }

    private static CrawlConfig parseArgs(String[] args) {
        if (args.length < 3) {
            throw new IllegalArgumentException("Missing required arguments.");
        }

        try {
            URL url = new URL(args[0]);
            int depth = Integer.parseInt(args[1]);

            if (depth < 0) {
                throw new IllegalArgumentException("Depth must be non-negative.");
            }

            Set<String> domains = new HashSet<>(Arrays.asList(args[2].split(",")));

            return new CrawlConfig(url, depth, domains);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL provided.");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Depth must be a valid integer.");
        }
    }
}