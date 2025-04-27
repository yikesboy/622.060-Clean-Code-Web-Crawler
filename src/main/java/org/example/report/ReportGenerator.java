package org.example.report;

import org.example.models.Heading;
import org.example.models.WebPage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ReportGenerator implements ReportGeneratorInterface {
    private static final String ARROW_INDENTATION = ">";

    @Override
    public boolean generateReport(WebPage rootPage, Path outputPath) {
        StringBuilder report = new StringBuilder();

        report.append("input: <a>").append(rootPage.getUrl()).append("</a>\n");
        report.append("<br>depth: ").append(rootPage.getDepth()).append("\n");

        appendHeadings(report, rootPage, "");

        appendChildPages(report, rootPage);

        try {
            Files.writeString(outputPath, report.toString());
            return true;
        } catch (IOException e) {
            System.err.println("Error writing report: " + e.getMessage());
            return false;
        }
    }

    private void appendHeadings(StringBuilder report, WebPage page, String indentPrefix) {
        List<Heading> headings = page.getHeadings();
        for (Heading heading : headings) {
            String headingMarker = "#".repeat(heading.getLevel()) + " ";
            report.append(headingMarker).append(indentPrefix).append(heading.getText()).append("\n");
        }
    }

    private void appendChildPages(StringBuilder report, WebPage page) {
        for (WebPage childPage : page.getChildPages()) {
            report.append("\n<br>");

            String arrowIndent = getArrowIndent(childPage.getDepth());

            if (childPage.isBroken()) {
                report.append(arrowIndent).append(" broken link <a>").append(childPage.getUrl()).append("</a>\n");
            } else {
                report.append(arrowIndent).append(" link to <a>").append(childPage.getUrl()).append("</a>\n");
                report.append("<br>depth: ").append(childPage.getDepth()).append("\n");

                appendHeadings(report, childPage, arrowIndent + " ");
                appendChildPages(report, childPage);
            }
        }
    }

    private String getArrowIndent(int depth) {
        String indentation = "--".repeat(Math.max(0, depth));
        indentation += ARROW_INDENTATION;
        return indentation;
    }
}
