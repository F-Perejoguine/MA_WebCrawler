import java.io.FileWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.io.BufferedReader;
import java.io.FileReader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Webcrawler
{
    public static CrawlConfiguration config; //Configuration class containing

    public static void main(String[] args)
    {
        config = new CrawlConfiguration();
        String[] seedLinks = {"http://www.hlportal.de/"};
        String[] k_topical = {"valve", "team", "fortress", "computer", "games", "steam"};
        String[] k_abstract = {};
        String[] k_specific = {};

        config.setSeedLinks(seedLinks);
        config.setKeywords(k_topical, k_abstract, k_specific);

        int crawlNumber = 100;
        final int FACTOR_RESERVE = 4;

        int pagesParsed = 0;
        ArrayList<String> history = new ArrayList<String>();
        ArrayList<String> foundpages = new ArrayList<String>();
        Queue lQueue = new Queue(config.getSeedLinks());

        Logger crawllogger = Logger.getLogger("MAIN_LOGGER");
        FileHandler logFileHandler;

        try
        {
            logFileHandler = new FileHandler("crawllog_" + System.currentTimeMillis() + ".txt", true);
            crawllogger.addHandler(logFileHandler);
            SimpleFormatter logFormatter = new SimpleFormatter();
            crawllogger.setLevel(config.getLogLevel());
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
                foundpages.add(workingURL);
                pagesParsed++;

                DOMtoFile(doc);
                Website current = new Website(doc, config.getTopical(), config.getAbstract(), config.getSpecific());
                System.out.println(current.getRating());

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
                        crawllogger.log(Level.FINEST, "Link rejected: " + foundlink + "  DATA: " + alreadyvisited + ", " + lQueue.checkDoubles(foundlink) + ", " + (lQueue.size() < (pagesParsed + crawlNumber) * FACTOR_RESERVE));
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

    //Only for testing purposes
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

    //Only for testing purposes
    private static void Testmatch()
    {
        String text = "";
        text = readFile("testfile.txt");
        String tests = "Und Steve's Unterführung dachte mit.";
        String keyword = "arbeit";
        String[] tokenized = tests.split("(\\s|[^a-zA-Z_0-9_äöü])+");

            for (String token : tokenized) {
                System.out.println(token);
            }


    }

    //copied from http://stackoverflow.com/questions/326390/how-to-create-a-java-string-from-the-contents-of-a-file, Only for testing purposes
    private static String readFile( String file )
    {
        String result = "";
        try{
        String         ls = System.getProperty("line.separator");
        BufferedReader reader = new BufferedReader( new FileReader (file));
        String         line = null;
        StringBuilder  stringBuilder = new StringBuilder();
        while( ( line = reader.readLine() ) != null ) {
        stringBuilder.append( line );
        stringBuilder.append( ls );
    }


        result = stringBuilder.toString();
        }
        catch(Exception e) {}
        return result;
}
}

