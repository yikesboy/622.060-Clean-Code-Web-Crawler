package io.github.yikesboy.report;

import io.github.yikesboy.models.WebPage;

import java.nio.file.Path;

public interface ReportGeneratorInterface {
    boolean generateReport(WebPage rootPage, Path outputPath);
}
