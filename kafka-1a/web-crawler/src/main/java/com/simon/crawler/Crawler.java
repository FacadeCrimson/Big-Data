package com.simon.crawler;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;
import java.util.Set;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class Crawler extends WebCrawler{
    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg|png|mp3|mp3|zip|gz))$");

    /**
    * This method receives two parameters. The first parameter is the page
    * in which we have discovered this new url and the second parameter is
    * the new url. You should implement this function to specify whether
    * the given url should be crawled or not (based on your crawling logic).
    * In this example, we are instructing the crawler to ignore urls that
    * have css, js, git, ... extensions and to only accept urls that start
    * with "http://www.ics.uci.edu/". In this case, we didn't need the
    * referringPage parameter to make the decision.
    */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        return !FILTERS.matcher(href).matches()
        && href.startsWith("https://film.avclub.com/the-best-films-of-the-00s-1798222348");
    }

    /**
    * This function is called when a page is fetched and ready
    * to be processed by your program.
    */
    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        System.out.println("URL: " + url);

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String html = htmlParseData.getHtml();
            Set<WebURL> links = htmlParseData.getOutgoingUrls();

            System.out.println("Html length: " + html.length());
            System.out.println("Number of outgoing links: " + links.size());

            Document doc= Jsoup.parse(html);
            Elements content=doc.getElementsByClass("r43lxo-0 gqfcxx js_post-content");
            if (content.size()==0){
                return;
            }

            try{
                FileWriter output = new FileWriter("/Users/apple/Desktop/data/output_"+Base64.getEncoder().encodeToString(url.getBytes("utf-8"))+".txt");
                for (Element element: content) {
                    output.write(element.text()+"\n");
                }
                output.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
