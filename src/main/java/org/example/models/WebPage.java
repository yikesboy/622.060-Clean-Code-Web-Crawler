package org.example.models;

import org.example.html.HtmlDocument;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WebPage {
    private final URL url;
    private final List<Heading> headings;
    private final List<WebPage> childPages;
    private final boolean isBroken;
    private final int depth;
    private final HtmlDocument document;

    public WebPage(URL url, List<Heading> headings, int depth, HtmlDocument document) {
        this.url = url;
        this.headings = new ArrayList<>(headings);
        this.childPages = new ArrayList<>();
        this.isBroken = false;
        this.depth = depth;
        this.document = document;
    }

    public WebPage(URL url, int depth, boolean isBroken) {
        this.url = url;
        this.headings = new ArrayList<>();
        this.childPages = new ArrayList<>();
        this.isBroken = isBroken;
        this.depth = depth;
        this.document = null;
    }

    public URL getUrl() {
        return url;
    }

    public List<Heading> getHeadings() {
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

    public HtmlDocument getDocument() {
        return document;
    }
}