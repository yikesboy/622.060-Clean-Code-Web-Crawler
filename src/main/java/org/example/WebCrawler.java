package org.example;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class WebCrawler {
    public static void main(String[] args) {
        try {
            CrawlConfig config = parseArgs(args);
            //TODO: instantiate and start business logic
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