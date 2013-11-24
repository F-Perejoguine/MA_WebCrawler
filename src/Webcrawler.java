import java.io.FileWriter;
import java.util.logging.Level;
import java.io.BufferedReader;
import java.io.FileReader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.net.URI;


public class Webcrawler
{

    public static void main(String[] args)
    {
        String[] seedLinks = {"http://www.gamestar.de/", "http://stackoverflow.com/questions/3571203/what-is-the-exact-meaning-of-runtime-getruntime-totalmemory-and-freememory", "http://www.hlportal.de/", "http://www.teamfortress.com/"};
        String[] k_topical = {"machine", "learning", "regression", "decision", "trees", "artificial", "intelligence", "linear"};
        String[] k_abstract = {};
        String[] k_specific = {};
        int crawlNumber = 50;


        Config.setSeedLinks(seedLinks);
        Config.setKeywords(k_topical, k_abstract, k_specific);
        Config.setCrawlNumber(crawlNumber);
        Config.core = new Core();
        Config.lQueue = new Queue(Config.getSeedLinks());
        Config.initializeLogger();


        Config.logger.log(Level.INFO, "Starting Webcrawl for " + crawlNumber + " pages.");
        long ctime = System.currentTimeMillis();

        while(Config.core.getCollectionTotal() < crawlNumber && Config.lQueue.size() != 0)
        {
            // Get current size of heap in bytes
            long heapSize = Runtime.getRuntime().totalMemory();

            // Get maximum size of heap in bytes. The heap cannot grow beyond this size.// Any attempt will result in an OutOfMemoryException.
            long heapMaxSize = Runtime.getRuntime().maxMemory();

            // Get amount of free memory within the heap in bytes. This size will increase // after garbage collection and decrease as new objects are created.
            long heapFreeSize = Runtime.getRuntime().freeMemory();


            Link workinglink = Config.lQueue.get();

            String workingURL = workinglink.url;
            boolean parsesuccess = true;
            Document doc = null;
            String domain = "";
            try {
                domain = new URI(workingURL).getHost();
            } catch(Exception e) {}

            System.out.println(heapMaxSize + " max, " + heapSize + " current, " + heapFreeSize + " free    parsing page Nr. " + Config.core.getCollectionTotal() + ":   " + workingURL);
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

            Config.core.reloadModel();


        double f_time = (double)(System.currentTimeMillis() - ctime)/1000;
        if(Config.core.getCollectionTotal() == crawlNumber)
        {
            Config.logger.log(Level.INFO, "Process finished with " + Config.core.getCollectionTotal() + " pages parsed in " + f_time + " seconds: Desired number of crawls reached.");
        }

        if(Config.lQueue.size() == 0)
        {
            Config.logger.log(Level.INFO, "Process finished with " + Config.core.getCollectionTotal() + " pages parsed in " + f_time + " seconds: Not enough links to proceed.");
        }

        boolean successful = false;
        String newLine = System.getProperty("line.separator");

        int count = 0;
        double sum = 0;
        try {
            FileWriter fw = new FileWriter("crawleroutput.txt", true);

            for(int i = 0; i < Config.core.collection.size(); i++)
            {
                Link element = Config.core.collection.get(i);

                fw.write(element.url + newLine);
                fw.write("SC  SM  LM  UM  SD      RATING         " + newLine);
                for(Datapoint dp : element.data) {
                    fw.write(dp.srccontent + "  " + dp.srcmatches + "  " + dp.linkmatches + "  " + dp.urlmatches + "  " + dp.samedomain + "    " + element.rating + newLine);
                    //count++;

                    double estimation = Config.core.calculatePriority(dp);
                    sum = sum + Math.abs(element.rating - estimation);
                    System.out.println("Predicted: " + estimation + ", reality: " + element.rating);

                    count++;
                }
                fw.write(element.getRefNumber() + newLine);
                fw.write(newLine + newLine + newLine);

                //System.out.println(element.rating + "       " + element.url + newLine);
            }
            successful = true;
            fw.close();
        } catch(Exception e) {
            System.err.println(e.getMessage());
        }

        if(successful) System.out.println("Result written to file. Average error: " + (sum / (double)count));
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

