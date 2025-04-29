package io.github.yikesboy.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UrlUtil Tests")
public class UrlUtilTest {
    @Test
    @DisplayName("Should identify exact domain match")
    void shouldIdentifyExactDomainMatch() throws MalformedURLException {
        URL url = new URL("https://github.com/page");
        Set<String> allowedDomains = Set.of("github.com");

        assertTrue(UrlUtil.isAllowedDomain(url, allowedDomains));
    }

    @Test
    @DisplayName("Should identify subdomain match")
    void shouldIdentifySubdomainMatch() throws MalformedURLException {
        URL url = new URL("https://blog.github.com/page");
        Set<String> allowedDomains = Set.of("github.com");

        assertTrue(UrlUtil.isAllowedDomain(url, allowedDomains));
    }

    @Test
    @DisplayName("Should identify nested subdomain match")
    void shouldIdentifyNestedSubdomainMatch() throws MalformedURLException {
        URL url = new URL("https://dev.blog.github.com/page");
        Set<String> allowedDomains = Set.of("github.com");

        assertTrue(UrlUtil.isAllowedDomain(url, allowedDomains));
    }

    @Test
    @DisplayName("Should reject different domain")
    void shouldRejectDifferentDomain() throws MalformedURLException {
        URL url = new URL("https://different.com/page");
        Set<String> allowedDomains = Set.of("github.com");

        assertFalse(UrlUtil.isAllowedDomain(url, allowedDomains));
    }

    @Test
    @DisplayName("Should reject partial domain match")
    void shouldRejectPartialDomainMatch() throws MalformedURLException {
        URL url = new URL("https://notgithub.com/page");
        Set<String> allowedDomains = Set.of("github.com");

        assertFalse(UrlUtil.isAllowedDomain(url, allowedDomains));
    }

    @Test
    @DisplayName("Should match any of multiple allowed domains")
    void shouldMatchAnyOfMultipleAllowedDomains() throws MalformedURLException {
        URL url1 = new URL("https://github.com/page");
        URL url2 = new URL("https://github.org/page");
        URL url3 = new URL("https://sub.github.net/page");
        Set<String> allowedDomains = Set.of("github.com", "github.org", "github.net");

        assertTrue(UrlUtil.isAllowedDomain(url1, allowedDomains));
        assertTrue(UrlUtil.isAllowedDomain(url2, allowedDomains));
        assertTrue(UrlUtil.isAllowedDomain(url3, allowedDomains));
    }

    @Test
    @DisplayName("Should handle IP addresses")
    void shouldHandleIpAddresses() throws MalformedURLException {
        URL url = new URL("http://127.0.0.1/page");
        Set<String> allowedDomains = Set.of("127.0.0.1");

        assertTrue(UrlUtil.isAllowedDomain(url, allowedDomains));
    }

    @ParameterizedTest
    @CsvSource({
            "https://GITHUB.com/path, https://github.com/path",
            "http://github.com/, http://github.com",
            "https://Github.Com/Path, https://github.com/path"
    })
    @DisplayName("Should normalize URLs by removing trailing slash and converting to lowercase")
    void shouldNormalizeUrls(String input, String expected) throws MalformedURLException {
        URL url = new URL(input);

        String normalized = UrlUtil.normalizeUrl(url);

        assertEquals(expected, normalized);
    }

    @Test
    @DisplayName("Should normalize URL without trailing slash")
    void shouldNormalizeUrlWithoutTrailingSlash() throws MalformedURLException {
        URL url = new URL("https://github.com/path");

        String normalized = UrlUtil.normalizeUrl(url);

        assertEquals("https://github.com/path", normalized);
    }

    @Test
    @DisplayName("Should normalize URL with query parameters")
    void shouldNormalizeUrlWithQueryParameters() throws MalformedURLException {
        URL url = new URL("https://github.com/search?q=test&page=1");

        String normalized = UrlUtil.normalizeUrl(url);

        assertEquals("https://github.com/search?q=test&page=1", normalized);
    }

    @Test
    @DisplayName("Should normalize URL with mixed case and trailing slash")
    void shouldNormalizeUrlWithMixedCaseAndTrailingSlash() throws MalformedURLException {
        URL url = new URL("https://Github.COM/Path/");

        String normalized = UrlUtil.normalizeUrl(url);

        assertEquals("https://github.com/path", normalized);
    }
}
