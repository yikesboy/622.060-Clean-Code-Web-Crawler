package io.github.yikesboy.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("CrawlConfig Tests")
public class CrawlConfigTest {
    @Test
    @DisplayName("Should create valid config with minimum values")
    void shouldCreateValidConfig() throws MalformedURLException {
        URL rootUrl = new URL("https://github.com");
        int maxDepth = 0;
        Set<String> allowedDomains = Set.of("github.com");

        CrawlConfig config = new CrawlConfig(rootUrl, maxDepth, allowedDomains);

        assertEquals(rootUrl, config.rootUrl());
        assertEquals(maxDepth, config.maxDepth());
        assertEquals(allowedDomains, config.allowedDomains());
    }

    @Test
    @DisplayName("Should create valid config with multiple domains")
    void shouldCreateConfigWithMultipleDomains() throws MalformedURLException {
        URL rootUrl = new URL("https://github.com");
        int maxDepth = 2;
        Set<String> allowedDomains = Set.of("github.com", "github.org", "github.net");

        CrawlConfig config = new CrawlConfig(rootUrl, maxDepth, allowedDomains);

        assertEquals(allowedDomains, config.allowedDomains());
        assertEquals(3, config.allowedDomains().size());
    }

    @Test
    @DisplayName("Should throw exception for null root URL")
    void shouldThrowForNullRootUrl() {
        int maxDepth = 2;
        Set<String> allowedDomains = Set.of("github.com");

        assertThrows(IllegalArgumentException.class, () -> new CrawlConfig(null, maxDepth, allowedDomains));
    }

    @Test
    @DisplayName("Should throw exception for negative max depth")
    void shouldThrowForNegativeMaxDepth() throws MalformedURLException {
        URL rootUrl = new URL("https://github.com");
        Set<String> allowedDomains = Set.of("github.com");
        int negativeDepth = -1;

        assertThrows(IllegalArgumentException.class, () -> new CrawlConfig(rootUrl, negativeDepth, allowedDomains));
    }

    @Test
    @DisplayName("Should throw exception for null allowed domains")
    void shouldThrowForNullAllowedDomains() throws MalformedURLException {
        URL rootUrl = new URL("https://github.com");
        int maxDepth = 2;

        assertThrows(IllegalArgumentException.class, () -> new CrawlConfig(rootUrl, maxDepth, null));
    }

    @Test
    @DisplayName("Should throw exception for empty allowed domains")
    void shouldThrowForEmptyAllowedDomains() throws MalformedURLException {
        URL rootUrl = new URL("https://github.com");
        int maxDepth = 2;
        Set<String> allowedDomains = new HashSet<>();

        assertThrows(IllegalArgumentException.class, () -> new CrawlConfig(rootUrl, maxDepth, allowedDomains));
    }
}
