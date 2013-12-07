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

    public static String[] seedLinks;
    public static String[] k_topical;
    private static String[] k_abstract;
    private static String[] k_specific;
    public static Level loglevel;
    public static int stopCondition;
    public static int stopParameter;
    public static boolean onlyseeds;
    public static int domainmax;
    public static double updatemultiplier;
    public static boolean prune;

    public static int crawlNumber;

    public static Queue lQueue;
    public static Core core;
    public static List<String> flinks = new ArrayList<String>();

    public static Logger logger = Logger.getLogger("MAIN_LOGGER");
    static final double FACTOR_RESERVE = 2.0;
    static final int datasetsize = 5;

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

    public static String[] getTopical() {
        return k_topical;
    }
}
