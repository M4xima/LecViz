package com.lecviz.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses lecture slides from Prof. Rupesh Nasre's PDS course page.
 * Handles both:
 *   1. The HTML index page at https://cse.iitm.ac.in/~rupesh/teaching/pds/aug21/
 *   2. Individual PDF slide files (via Apache POI / PDFBox)
 *   3. Local HTML/PPTX slide files
 */
public class SlideParser {

    /**
     * Represents a single slide / topic extracted from lecture content.
     */
    public static class SlideContent {
        public String title;
        public String body;          // plain text explanation
        public String codeSnippet;   // code example if any
        public List<String> bullets = new ArrayList<>();
        public String topic;         // e.g. "Pointers", "Linked Lists"

        @Override
        public String toString() {
            return String.format("[%s] %s — %d bullets, code=%s",
                    topic, title, bullets.size(), codeSnippet != null ? "yes" : "no");
        }
    }

    /**
     * Scrape the PDS course index page for lecture links and titles.
     */
    public List<String> fetchLectureLinks(String courseUrl) throws IOException {
        Document doc = Jsoup.connect(courseUrl)
                .userAgent("LecViz/1.0")
                .timeout(10000)
                .get();

        List<String> links = new ArrayList<>();
        Elements anchors = doc.select("a[href]");
        for (Element a : anchors) {
            String href = a.attr("href");
            if (href.endsWith(".pdf") || href.endsWith(".pptx") || href.endsWith(".html")) {
                String fullUrl = href.startsWith("http") ? href : courseUrl + href;
                links.add(fullUrl);
            }
        }
        return links;
    }

    /**
     * Parse a local HTML slide file into structured content.
     */
    public List<SlideContent> parseHtmlSlides(File htmlFile) throws IOException {
        Document doc = Jsoup.parse(htmlFile, "UTF-8");
        return parseDocument(doc);
    }

    /**
     * Parse an HTML string (e.g. from downloaded page).
     */
    public List<SlideContent> parseHtmlString(String html, String topic) {
        Document doc = Jsoup.parse(html);
        List<SlideContent> slides = parseDocument(doc);
        for (SlideContent s : slides) s.topic = topic;
        return slides;
    }

    private List<SlideContent> parseDocument(Document doc) {
        List<SlideContent> slides = new ArrayList<>();

        // Try to find slide-like sections (common patterns in lecture HTML)
        Elements sections = doc.select("section, .slide, div.page, article");
        if (sections.isEmpty()) {
            // Fallback: split by headers
            sections = doc.select("h1, h2, h3");
        }

        for (Element section : sections) {
            SlideContent slide = new SlideContent();

            // Title from header
            Element header = section.selectFirst("h1, h2, h3, .title");
            slide.title = header != null ? header.text() : section.ownText();

            // Bullets
            Elements listItems = section.select("li, .bullet");
            for (Element li : listItems) {
                slide.bullets.add(li.text());
            }

            // Code blocks
            Element codeEl = section.selectFirst("pre, code, .code");
            if (codeEl != null) {
                slide.codeSnippet = codeEl.text();
            }

            // Body text
            Elements paras = section.select("p");
            StringBuilder body = new StringBuilder();
            for (Element p : paras) body.append(p.text()).append(" ");
            slide.body = body.toString().trim();

            if (slide.title != null && !slide.title.isBlank()) {
                slides.add(slide);
            }
        }

        return slides;
    }

    /**
     * Manually define slide content for a PDS topic.
     * Use this when slides are in PDF (not easily parseable) or to hand-craft content.
     */
    public static SlideContent manualSlide(String topic, String title, String body,
                                           String code, String... bullets) {
        SlideContent s = new SlideContent();
        s.topic = topic;
        s.title = title;
        s.body = body;
        s.codeSnippet = code;
        for (String b : bullets) s.bullets.add(b);
        return s;
    }
}
