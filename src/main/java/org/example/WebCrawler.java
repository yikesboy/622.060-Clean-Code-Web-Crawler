package org.example;

import org.example.util.ExitStatus;

public class WebCrawler {
    public static void main(String[] args) {
        WebCrawlerApp app = new WebCrawlerApp();
        ExitStatus status = app.run(args);
        System.exit(status.getCode());
    }
}