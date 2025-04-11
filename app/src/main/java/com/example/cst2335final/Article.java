package com.example.cst2335final;

/**
 * The Article class is a simple class that represents an article object, which includes the title, section, and url.
 */
public class Article {
    private final String title;
    private final String section;
    private final String url;

    public Article(String title, String section, String url) {
        this.title = title;
        this.section = section;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getSection() {
        return section;
    }

    public String getUrl() {
        return url;
    }
}
