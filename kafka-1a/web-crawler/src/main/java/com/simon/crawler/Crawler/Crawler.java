package com.simon.crawler.Crawler;

import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Pattern;

import com.simon.crawler.App;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class Crawler extends WebCrawler {
    private final static Logger logger = LoggerFactory.getLogger(Crawler.class.getName());
    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg|png|mp3|mp3|zip|gz))$");
    private ArrayList<String> prefixes;

    public Crawler(ArrayList<String> prefixes) {
        this.prefixes = prefixes;
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        logger.info("href");
        if (!FILTERS.matcher(href).matches()) {
            return true;
        } else {
            for (String prefix : prefixes) {
                if (href.startsWith(prefix)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        logger.info("URL: " + url);

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String html = htmlParseData.getHtml();
            Set<WebURL> links = htmlParseData.getOutgoingUrls();

            logger.info("Html length: " + html.length());
            logger.info("Number of outgoing links: " + links.size());

            try {
                App.htmls.put(url + "#####" + html);
            } catch (Exception e) {
                logger.error("Adding element to blocking queue fails.", e);
            } finally {
                logger.info(url + "   OK");
            }

        }
    }
}
