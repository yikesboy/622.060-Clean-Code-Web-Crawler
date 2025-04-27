package org.example.util;

public enum ExitStatus {
    SUCCESS(0, "Successful execution"),
    INVALID_ARGS(1, "Invalid arguments"),
    UNEXPECTED_ERROR(2, "Unexpected error occurred"),
    REPORT_FAILED(3, "Report generation failed");

    private final int code;
    private final String description;

    ExitStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
