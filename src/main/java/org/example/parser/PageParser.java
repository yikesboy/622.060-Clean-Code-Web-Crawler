package org.example.parser;

import org.example.models.Heading;
import org.example.models.WebPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PageParser implements PageParserInterface {
    private static final int TIMEOUT_IN_MILLISECONDS = 5000;

    @Override
    public WebPage parse(URL url, int depth) {
        try {
            Document doc = Jsoup.connect(url.toString()).timeout(TIMEOUT_IN_MILLISECONDS).get();
            List<Heading> headings = extractHeadings(doc);
            return new WebPage(url, headings, depth);
        } catch (IOException e) {
            return new WebPage(url, depth, true);
        }
    }

    @Override
    public List<URL> extractLinks(URL url) {
        List<URL> validUrls = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(url.toString()).timeout(TIMEOUT_IN_MILLISECONDS).get();

            Elements links = doc.select("a[href]");

            for (Element link : links) {
                String href = link.attr("abs:href");

                if (href.isEmpty() || href.startsWith("javascript:") || href.startsWith("#")) {
                    continue;
                }

                try {
                    URL validUrl = new URL(href);
                    validUrls.add(validUrl);
                } catch (MalformedURLException e) {
                    continue;
                }
            }
        } catch (IOException e) {
            return new ArrayList<>();
        }

        return validUrls;
    }

    private List<Heading> extractHeadings(Document document) {
        List<Heading> headings = new ArrayList<>();

        for (int headingLevel = 1; headingLevel <= 6; headingLevel++) {
            String selector = "h" + headingLevel;

            for (Element element : document.select(selector)) {
                Heading heading = new Heading(headingLevel, element.text().trim());
                headings.add(heading);
            }
        }

        return headings;
    }
}
