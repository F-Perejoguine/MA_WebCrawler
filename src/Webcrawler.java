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

    public static void main(String[] args)
    {
        String[] seedLinks = {"http://www.teamfortress.com/"};
        String[] input = {"keyword1"};
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
                foundpages.add(workingURL);
                pagesParsed++;

                matchWebsite(doc, input);
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

    private static double matchWebsite(Document doc, String[] input)
    {
        DOMtoFile(doc);

        Website current = new Website(doc, input);




        return 2.0;
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

        String keyword = "arbeit";
        String[] tokenized = text.split(" ");

        for (String element : tokenized)
        {

            if (element != "") match(keyword, element);
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



    private static boolean match(String keyword, String subject)
    {
        int matches = 0;
        char[] chars1 = keyword.toLowerCase().toCharArray();
        char[] chars2 = subject.toLowerCase().toCharArray();

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
        else if(chars1.length > 4 && matches >= Math.round((chars1.length - 4) / 2.0) + 3)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}

