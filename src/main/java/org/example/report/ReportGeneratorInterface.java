package org.example.report;

import org.example.models.WebPage;

import java.nio.file.Path;

public interface ReportGeneratorInterface {
    boolean generateReport(WebPage rootPage, Path outputPath);
}
