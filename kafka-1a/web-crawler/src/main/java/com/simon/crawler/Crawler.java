package com.simon.crawler;

import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class Crawler extends WebCrawler{
    private final static Logger logger = LoggerFactory.getLogger(Crawler.class.getName());
    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg|png|mp3|mp3|zip|gz))$");
    private final static String PREFIX = System.getenv("prefix");

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
        && href.startsWith(PREFIX);
    }

    /**
    * This function is called when a page is fetched and ready
    * to be processed by your program.
    */
    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        logger.info("URL: " + url);

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String html = htmlParseData.getHtml();
            Set<WebURL> links = htmlParseData.getOutgoingUrls();

            System.out.println("Html length: " + html.length());
            System.out.println("Number of outgoing links: " + links.size());

            IndeedPlugin.process(html);
            // Document doc= Jsoup.parse(html);
            
            // try{
            //     String fileName = "output_"+Base64.getEncoder().encodeToString(url.getBytes("utf-8"))+".txt";
            //     FileWriter output = new FileWriter(STORAGE+fileName);
            //     Elements cards = doc.getElementsByClass("jobsearch-SerpJobCard unifiedRow row result");
            //     for(Element card : cards){
            //         ObjectNode post = JsonNodeFactory.instance.objectNode();
            //         post.put("id",card.attr("data-jk"));
    
            //         Element title = card.getElementsByClass("title").first().getElementsByTag("a").first();
            //         post.put("title",title.attr("title"));
            //         post.put("link",title.attr("href"));

            //         Element company = card.getElementsByClass("company").first();
            //         post.put("company",company.text());

            //         Element loc = card.getElementsByClass("location accessible-contrast-color-location").first();
            //         post.put("location",loc.text());

            //         Element summary = card.getElementsByClass("summary").first();
            //         post.put("summary",summary.text());

            //         Element date = card.getElementsByClass("date").first();
            //         post.put("date",date.text());

            //         output.write(post.toString());
            //         output.write("\n");
            //     }
            //     output.close();
            // }catch(IOException e){
            //     e.printStackTrace();
            // }
        }
    }
}
