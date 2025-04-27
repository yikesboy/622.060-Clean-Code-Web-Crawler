package io.github.yikesboy;

import io.github.yikesboy.app.WebCrawlerApp;
import io.github.yikesboy.util.ExitStatus;

public class WebCrawler {
    public static void main(String[] args) {
        WebCrawlerApp app = new WebCrawlerApp();
        ExitStatus status = app.run(args);
        System.exit(status.getCode());
    }
}