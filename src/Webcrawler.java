import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;
import java.util.logging.Logger;
import java.util.logging.FileHandler;

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
        long ctime = System.currentTimeMillis();

        while(pagesParsed < crawlNumber && lQueue.size() != 0)
        {
            String fullHTML = "";
            boolean parsesuccess = true;
            String workingURL = lQueue.get();
            history.add(workingURL);

            try
            {
                URL url = new URL(workingURL);
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

                String line;
                while ((line = reader.readLine()) != null)
                {
                    fullHTML = fullHTML + line;
                }
                reader.close();
            }
            catch (java.net.MalformedURLException e)
            {
                crawllogger.log(Level.WARNING, "Error parsing " + workingURL + ", Malformed URL Exception: " + e.getMessage());
                parsesuccess = false;
            }
            catch (IOException r)
            {
                crawllogger.log(Level.WARNING, "Error parsing " + workingURL + ", IO Exception: " + r.getMessage());
                parsesuccess = false;
            }

            if(fullHTML.equals("")) parsesuccess = false;

            if(parsesuccess)
            {
                foundpages.add(workingURL);
                crawllogger.log(Level.FINE, "Page successfully parsed: " + workingURL);
                pagesParsed++;

                Pattern pat = Pattern.compile("https?://[\\w-_]+\\.[^\"^'\\s]+");
                Matcher mat = pat.matcher(fullHTML);

                while(mat.find())
                {
                    String foundlink = mat.group();
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

        double f_time = (double)(System.currentTimeMillis() - ctime)/1000;
        if(pagesParsed == crawlNumber)
        {
            crawllogger.log(Level.INFO, "Process finished with " + pagesParsed + " pages parsed in " + f_time + " seconds: Desired number of crawls reached.");
        }

        if(lQueue.size() == 0)
        {
            crawllogger.log(Level.INFO, "Process finished with " + pagesParsed + " pages parsed in " + f_time + " seconds: Not enough links to proceed.");
        }
        System.out.println("Pages found:");
        for (String pages : foundpages)
            System.out.println(pages);
    }
}