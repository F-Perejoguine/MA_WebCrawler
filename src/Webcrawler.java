import java.io.FileWriter;
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
        String[] seedLinks = {"http://gtaforums.com/topic/402516-outoftimers-guide-to-gta-iv-mods/"};
        int crawlNumber = 1;
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
        long ctime = System.currentTimeMillis();

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
                crawllogger.log(Level.WARNING, e.getMessage() +  " parsing " + workingURL);
                parsesuccess = false;
            }
            if (doc == null) parsesuccess = false;

            if(parsesuccess)
            {
                //DOMtoFile(doc);
                foundpages.add(workingURL);
                pagesParsed++;

                int lfound = 0;
                int ladded = 0;
                Elements links = doc.select("a[href]");

                for (Element link : links)
                {
                    lfound++;
                    String foundlink = link.attr("abs:href");
                    boolean alreadyvisited = false;

                    for(String ilink : history)
                        if(ilink.equals(foundlink)) alreadyvisited = true;

                    if(!alreadyvisited && !lQueue.checkDoubles(foundlink)) //&& lQueue.size() < (pagesParsed + crawlNumber) * reserveFactor)
                    {
                        crawllogger.log(Level.FINER, "Link added to queue: " + foundlink);
                        lQueue.add(foundlink);
                        ladded++;
                    }
                    else
                    {
                        crawllogger.log(Level.FINEST, "Link rejected: " + foundlink + "  DATA: " + alreadyvisited + ", " + lQueue.checkDoubles(foundlink) + ", " + (lQueue.size() < (pagesParsed + crawlNumber) * reserveFactor));
                    }
                }

                crawllogger.log(Level.FINE, "PAGE SUCCESSFULLY PARSED: " + workingURL + ", Links found: " + lfound + ", Links added: " + ladded);
            }
        }

        double f_time = (double)(System.currentTimeMillis() - ctime)/1000;
        if(pagesParsed == crawlNumber)
        {
            crawllogger.log(Level.INFO, "Process finished with " + pagesParsed + " pages parsed in " + f_time + " seconds: Desired number of crawls reached.");
        }

        if(lQueue.size() == 0)
        {
            crawllogger.log(Level.INFO, "Process finished with " + pagesParsed + " pages parsed in " + f_time + " seconds: Not enough links to proceed.");
        }
        System.out.println("Pages parsed:");
        for (String pages : foundpages)
            System.out.println(pages);
    }
/*/
    private static void DOMtoFile(Document doc) {
        try{
            FileWriter fw = new FileWriter("DOM_" + System.currentTimeMillis() + ".txt", true);
            fw.write(doc.toString());
            fw.close();
        }
        catch(Exception e)
        {

        }
    }
/*/
    private static boolean match(String s1, String s2)
    {
        int matches = 0;
        char[] chars1 = s1.toLowerCase().toCharArray();
        char[] chars2 = s2.toLowerCase().toCharArray();

        for (int i = 0; i < Math.min(chars1.length, chars2.length); i++)
        {
            if (chars1[i] == chars2[i])
            {
                matches++;
            }
            else
            {
                break;
            }
        }

        if (chars1.length <= 3 && chars2.length == chars1.length && matches == 3)
        {
            return true;
        }
        else if(chars1.length == 4 && matches == 4)
        {
            return true;
        }
        else if(chars1.length > 4 && matches >= Math.round((chars1.length + 4) / 2) + 3)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}

