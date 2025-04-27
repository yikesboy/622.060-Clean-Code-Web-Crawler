package org.example.util;

import org.example.config.CrawlConfig;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class ArgumentParser implements ArgumentParserInterface {
    public CrawlConfig parse(String[] args) {
        validateArgLength(args);
        URL url = parseUrl(args[0]);
        int depth = parseDepth(args[1]);
        Set<String> domains = parseDomains(args[2]);

        return new CrawlConfig(url, depth, domains);
    }

    private void validateArgLength(String[] args) {
        if (args.length < 3) {
            throw new IllegalArgumentException("Missing required arguments.");
        }
    }

    private URL parseUrl(String urlArgument) {
        try {
            return new URL(urlArgument);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL provided.");
        }
    }

    private int parseDepth(String depthArgument) {
        try {
            int depth = Integer.parseInt(depthArgument);
            if (depth < 0) {
                throw new IllegalArgumentException("Depth cannot be negative.");
            }
            return depth;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Depth must be a valid integer.");
        }
    }

    private Set<String> parseDomains(String domainArgument) {
        String[] domainCandidates = domainArgument.split(",");
        Set<String> domains = new HashSet<>(List.of(domainCandidates));

        if (domains.isEmpty()) {
            throw new IllegalArgumentException("At least one domain must be specified.");
        }

        return domains;
    }
}
