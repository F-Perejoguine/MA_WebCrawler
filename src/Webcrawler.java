import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;
import java.util.logging.Logger;
import java.util.logging.FileHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Webcrawler
{

    public static void main(String[] args)
    {
        String[] seedLinks = {"http://stackoverflow.com/questions/4452288/what-is-the-name-of-this-convention-for-curly-braces", "http://docs.oracle.com/javase/index.html"};
        int crawlNumber = 10;
        int reserveFactor = 4;

        int pagesParsed = 0;
        ArrayList<String> history = new ArrayList<String>();
        ArrayList<String> foundpages = new ArrayList<String>();
        Queue lQueue = new Queue(seedLinks);
        Logger crawllogger = Logger.getLogger("MAIN_LOGGER");
        FileHandler logFileHandler;

        try
        {
            logFileHandler = new FileHandler("crawllog_" + System.currentTimeMillis() + ".txt", true);
            crawllogger.addHandler(logFileHandler);
            SimpleFormatter logFormatter = new SimpleFormatter();
            crawllogger.setLevel(Level.FINE);
            logFileHandler.setFormatter(logFormatter);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        crawllogger.log(Level.INFO, "Starting Webcrawl for " + crawlNumber + " pages.");

        while(pagesParsed < crawlNumber && lQueue.size() != 0)
        {
            boolean parsesuccess = true;
            String workingURL = lQueue.get();
            history.add(workingURL);

            Document doc = null;
            try
            {
                doc = Jsoup.connect(workingURL).get();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                parsesuccess = false;
            }
            if (doc == null) parsesuccess = false;

            if(parsesuccess)
            {
                foundpages.add(workingURL);
                crawllogger.log(Level.FINE, "Page successfully parsed: " + workingURL);
                pagesParsed++;


                Elements links = doc.select("a[href]");

                for (Element link : links)
                {
                    String foundlink = link.attr("abs:href");
                    boolean alreadyvisited = false;

                    for(String ilink : history)
                        if(ilink.equals(foundlink)) alreadyvisited = true;

                    if(!alreadyvisited && !lQueue.checkDoubles(foundlink) && lQueue.size() < (pagesParsed + crawlNumber) * reserveFactor)
                    {
                        crawllogger.log(Level.FINE, "ACCEPTED: " + foundlink + "  DATA: " + alreadyvisited + ", " + lQueue.checkDoubles(foundlink) + ", " + (lQueue.size() < (pagesParsed + crawlNumber) * reserveFactor));
                        lQueue.add(foundlink);
                    }
                    else
                    {
                        crawllogger.log(Level.FINE, "REJECTED: " + foundlink + "  DATA: " + alreadyvisited + ", " + lQueue.checkDoubles(foundlink) + ", " + (lQueue.size() < (pagesParsed + crawlNumber) * reserveFactor));
                    }
                }
            }
        }

        if(pagesParsed == crawlNumber)
        {
            crawllogger.log(Level.INFO, "Process finished with " + pagesParsed + " pages parsed: Desired number of crawls reached.");
        }

        if(lQueue.size() == 0)
        {
            crawllogger.log(Level.INFO, "Process finished with " + pagesParsed + " pages parsed: Not enough links to proceed.");
        }
        System.out.println("Pages found:");
        for (String pages : foundpages)
            System.out.println(pages);
    }
}