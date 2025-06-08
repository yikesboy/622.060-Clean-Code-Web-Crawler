package io.github.yikesboy.report;

import io.github.yikesboy.models.CrawlError;
import io.github.yikesboy.models.WebPage;

import java.nio.file.Path;
import java.util.List;

public interface ReportGeneratorInterface {
    boolean generateReport(WebPage rootPage, List<CrawlError> errors, Path outputPath);
}
