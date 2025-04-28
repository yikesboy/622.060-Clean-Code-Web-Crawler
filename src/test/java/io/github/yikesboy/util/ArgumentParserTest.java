package io.github.yikesboy.util;

import io.github.yikesboy.config.CrawlConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("ArgumentParser Tests")
public class ArgumentParserTest {
    private ArgumentParser parser;

    @BeforeEach
    void setUp() {
        parser = new ArgumentParser();
    }

    @Test
    @DisplayName("Should parse valid arguments correctly")
    void shouldParseValidArguments() throws Exception {
        String[] args = {"https://github.com", "3", "https://github.com/yikesboy,https://github.com/yikesboy/622.060-Clean-Code-Web-Crawler"};

        CrawlConfig config = parser.parse(args);

        assertEquals(new URL("https://github.com"), config.rootUrl());
        assertEquals(3, config.maxDepth());
        assertEquals(Set.of("https://github.com/yikesboy", "https://github.com/yikesboy/622.060-Clean-Code-Web-Crawler"), config.allowedDomains());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for insufficient arguments")
    void shouldThrowForInsufficientArguments() {
        String[] args = {"https://github.com"};
        assertThrows(IllegalArgumentException.class, () -> parser.parse(args));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for malformed URL")
    void shouldThrowForMalformedUrl() {
        String[] args = {"hts://githubom", "3", "https://github.com/yikesboy,https://github.com/yikesboy/622.060-Clean-Code-Web-Crawler"};
        assertThrows(IllegalArgumentException.class, () -> parser.parse(args));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for negative depth")
    void shouldThrowForNegativeDepth() {
        String[] args = {"https://github.com", "-1", "https://github.com/yikesboy,https://github.com/yikesboy/622.060-Clean-Code-Web-Crawler"};
        assertThrows(IllegalArgumentException.class, () -> parser.parse(args));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for non-integer depth")
    void shouldThrowForNonIntegerDepth() {
        String[] args = {"https://github.com", "String", "https://github.com/yikesboy,https://github.com/yikesboy/622.060-Clean-Code-Web-Crawler"};
        assertThrows(IllegalArgumentException.class, () -> parser.parse(args));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for empty domain list")
    void shouldThrowForEmptyDomains() {
        String[] args = {"https://github.com", "3", ""};
        assertThrows(IllegalArgumentException.class, () -> parser.parse(args));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for null domain list")
    void shouldThrowForDomainsNull() {
        String[] args = {"https://github.com", "3", null};
        assertThrows(NullPointerException.class, () -> parser.parse(args));
    }

    @Test
    @DisplayName("Should parse domains correctly with duplicates and empty strings")
    void shouldParseDomainsWithDuplicatesAndEmpty() {
        String[] args = {"https://github.com", "3", "https://github.com,,https://github.com,https://example.com"};
        CrawlConfig config = parser.parse(args);
        assertEquals(Set.of("https://github.com", "https://example.com"), config.allowedDomains());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for domain list with only empty strings")
    void shouldThrowForOnlyEmptyDomains() {
        String[] args = {"https://github.com", "3", ",,"};
        assertThrows(IllegalArgumentException.class, () -> parser.parse(args));
    }

    @Test
    @DisplayName("Should parse depth zero correctly")
    void shouldParseDepthZero() {
        String[] args = {"https://github.com", "0", "https://github.com"};
        CrawlConfig config = parser.parse(args);
        assertEquals(0, config.maxDepth());
    }

    @Test
    @DisplayName("Should handle more than three arguments by ignoring extras")
    void shouldIgnoreExtraArguments() throws Exception {
        String[] args = {"https://github.com", "3", "https://github.com", "extraArg"};
        CrawlConfig config = parser.parse(args);
        assertEquals(new URL("https://github.com"), config.rootUrl());
        assertEquals(3, config.maxDepth());
        assertEquals(Set.of("https://github.com"), config.allowedDomains());
    }
}
