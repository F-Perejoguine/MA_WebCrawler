/**
 * Created with IntelliJ IDEA.
 * User: Fjodor
 * Date: 22.10.13
 * Time: 18:57
 * To change this template use File | Settings | File Templates.
 */
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Config {

    private static String[] seedLinks;
    private static String[] k_topical;
    private static String[] k_abstract;
    private static String[] k_specific;
    private static int crawlNumber;
    static final double FACTOR_RESERVE = 2.0;
    static final int datasetsize = 5;
    private static Level loglevel = Level.FINE;
    public static Logger logger = Logger.getLogger("MAIN_LOGGER");
    public static Queue lQueue;
    public static Core core;
    public static List<String> flinks = new ArrayList<String>();

    private Config() {}

    public static void setSeedLinks(String[] links) {
        seedLinks = links;
    }

    public static void setKeywords(String[] topical, String[] abstr, String[] specific) {
        k_topical = topical;
        k_abstract = abstr;
        k_specific = specific;
    }

    public static void initializeLogger() {
        FileHandler logFileHandler;

        try
        {
            logFileHandler = new FileHandler("crawllog_" + System.currentTimeMillis() + ".txt", true);
            logger.addHandler(logFileHandler);
            SimpleFormatter logFormatter = new SimpleFormatter();
            logger.setLevel(loglevel);
            logFileHandler.setFormatter(logFormatter);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static String[] getSeedLinks() {
        return seedLinks;
    }

    public static int getCrawlNumber() {
        return crawlNumber;
    }

    public static String[] getTopical() {
        return k_topical;
    }

    public static String[] getAbstract() {
        return k_abstract;
    }

    public static String[] getSpecific() {
        return k_specific;
    }

    public static void setCrawlNumber(int number) {
        crawlNumber = number;
    }
}
