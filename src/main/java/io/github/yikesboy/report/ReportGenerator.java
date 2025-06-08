package io.github.yikesboy.report;

import io.github.yikesboy.models.CrawlError;
import io.github.yikesboy.models.Heading;
import io.github.yikesboy.models.WebPage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ReportGenerator implements ReportGeneratorInterface {
    private static final String ARROW_INDENTATION = ">";
    private static final String LINK_START = "<a>";
    private static final String LINK_END = "</a>";
    private static final String LINE_BREAK = "<br>";
    private static final String NEW_LINE = "\n";
    private static final String INDENT_ELEMENT = "--";


    @Override
    public boolean generateReport(WebPage rootPage, List<CrawlError> errors, Path outputPath) {
        StringBuilder report = new StringBuilder();

        appendPageMetadata(report, rootPage);
        appendHeadings(report, rootPage, "");
        appendChildPages(report, rootPage);
        appendErrors(report, errors);

        try {
            Files.writeString(outputPath, report.toString());
            return true;
        } catch (IOException e) {
            System.err.println("Error writing report: " + e.getMessage());
            return false;
        }
    }

    private void appendPageMetadata(StringBuilder report, WebPage page) {
        report.append("input: ").append(formatLink(page.getUrl().toString())).append(NEW_LINE);
        report.append(LINE_BREAK).append("depth: ").append(page.getDepth()).append(NEW_LINE);
    }

    private void appendHeadings(StringBuilder report, WebPage page, String indentPrefix) {
        List<Heading> headings = page.getHeadings();
        for (Heading heading : headings) {
            String headingMarker = "#".repeat(heading.level()) + " ";
            report.append(headingMarker).append(indentPrefix).append(heading.text()).append(NEW_LINE);
        }
    }

    private void appendChildPages(StringBuilder report, WebPage page) {
        for (WebPage childPage : page.getChildPages()) {
            report.append(NEW_LINE).append(LINE_BREAK);

            String arrowIndent = createIndentation(childPage.getDepth());

            appendLink(report, childPage, arrowIndent, childPage.isBroken());
            appendHeadings(report, childPage, arrowIndent + " ");
            appendChildPages(report, childPage);
        }
    }

    private void appendErrors(StringBuilder report, List<CrawlError> errors) {
        if (errors.isEmpty()) {
            return;
        }

        report.append(NEW_LINE).append(LINE_BREAK).append("## Crawl Errors").append(NEW_LINE);
        for (CrawlError error : errors) {
            String arrowIndent = createIndentation(error.depth());
            report.append(arrowIndent)
                    .append(" error at ")
                    .append(formatLink(error.url().toString()))
                    .append(": ")
                    .append(error.message())
                    .append(NEW_LINE);
        }
    }

    private String formatLink(String url) {
        return LINK_START + url + LINK_END;
    }

    private String createIndentation(int depth) {
        return INDENT_ELEMENT.repeat(Math.max(0, depth)) + ARROW_INDENTATION;
    }

    private void appendLink(StringBuilder report, WebPage childPage, String arrowIndent, boolean isBroken) {
        report.append(arrowIndent)
                .append(isBroken ? " broken link " : " link to ")
                .append(formatLink(childPage.getUrl().toString()))
                .append(NEW_LINE);

        if (!isBroken) {
            report.append(LINE_BREAK)
                    .append("depth: ")
                    .append(childPage.getDepth())
                    .append(NEW_LINE);
        }
    }
}
