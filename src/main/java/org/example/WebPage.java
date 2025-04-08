package org.example;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WebPage {
    private final URL url;
    private final List<String> headings;
    private final List<WebPage> childPages;
    private final boolean isBroken;
    private final int depth;

    public WebPage(URL url, List<String> headings, int depth) {
        this.url = url;
        this.headings = new ArrayList<>(headings);
        this.childPages = new ArrayList<>();
        this.isBroken = false;
        this.depth = depth;
    }

    public WebPage(URL url, int depth, boolean isBroken) {
        this.url = url;
        this.headings = new ArrayList<>();
        this.childPages = new ArrayList<>();
        this.isBroken = isBroken;
        this.depth = depth;
    }

    public URL getUrl() {
        return url;
    }

    public List<String> getHeadings() {
        return headings;
    }

    public List<WebPage> getChildPages() {
        return childPages;
    }

    public boolean isBroken() {
        return isBroken;
    }

    public int getDepth() {
        return depth;
    }

    public void addChildPage(WebPage childPage) {
        childPages.add(childPage);
    }
}