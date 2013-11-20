import java.io.FileWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.io.BufferedReader;
import java.io.FileReader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class Webcrawler
{

    public static void main(String[] args)
    {
        String[] seedLinks = {"http://www.hlportal.de/", "http://www.poke646.com"};
        String[] k_topical = {"valve", "team", "fortress", "computer", "games", "steam"};
        String[] k_abstract = {};
        String[] k_specific = {};

        Config.setSeedLinks(seedLinks);
        Config.setKeywords(k_topical, k_abstract, k_specific);
        Config.core = new Core();

        int crawlNumber = 10;
        Config.setCrawlNumber(crawlNumber);
        Config.lQueue = new Queue(Config.getSeedLinks());


        Config.logger.log(Level.INFO, "Starting Webcrawl for " + crawlNumber + " pages.");
        long ctime = System.currentTimeMillis();

        while(Config.core.getCollectionTotal() < crawlNumber && Config.lQueue.size() != 0)
        {
            Link workinglink = Config.lQueue.get();

            String workingURL = workinglink.url;
            boolean parsesuccess = true;
            Document doc = null;
            System.out.println("parsing " + workingURL);
            try
            {
                doc = Jsoup.connect(workingURL).get();
            }
            catch (Exception e)
            {
                Config.logger.log(Level.WARNING, e.getMessage() +  " parsing " + workingURL);
                parsesuccess = false;
            }
            if (doc == null) parsesuccess = false;

            if(parsesuccess)
            {
                //DOMtoFile(doc);
                Website current = new Website(doc, workingURL);
                workinglink.rating = current.getRating();
                Config.core.addPage(workinglink);
                current.parseLinks();
            }
            else
            {
                Config.flinks.add(workingURL);
            }
        }


        double f_time = (double)(System.currentTimeMillis() - ctime)/1000;
        if(Config.core.getCollectionTotal() == crawlNumber)
        {
            Config.logger.log(Level.INFO, "Process finished with " + Config.core.getCollectionTotal() + " pages parsed in " + f_time + " seconds: Desired number of crawls reached.");
        }

        if(Config.lQueue.size() == 0)
        {
            Config.logger.log(Level.INFO, "Process finished with " + Config.core.getCollectionTotal() + " pages parsed in " + f_time + " seconds: Not enough links to proceed.");
        }


        System.out.println("Pages parsed:");
        for (Link element : Config.core.collection)
        {
            System.out.println(element.url + "       " + element.rating);
        }

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

