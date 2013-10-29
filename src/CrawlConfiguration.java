/**
 * Created with IntelliJ IDEA.
 * User: Fjodor
 * Date: 22.10.13
 * Time: 18:57
 * To change this template use File | Settings | File Templates.
 */
import java.util.*;
import java.util.logging.Level;

public class CrawlConfiguration {

    private String[] seedLinks;
    private String[] k_topical;
    private String[] k_abstract;
    private String[] k_specific;
    private Level loglevel;
    private int crawlNumber;

    public CrawlConfiguration() {



    }

    public void setSeedLinks(String[] links) {
        seedLinks = links;
    }

    public void setKeywords(String[] topical, String[] abstr, String[] specific) {
        k_topical = topical;
        k_abstract = abstr;
        k_specific = specific;
    }

    public String[] getSeedLinks() {
        return seedLinks;
    }

    public Level getLogLevel() {
        return loglevel;
    }

    public int getCrawlNumber() {
        return crawlNumber;
    }

    public String[] getTopical() {
        return k_topical;
    }

    public String[] getAbstract() {
        return k_abstract;
    }

    public String[] getSpecific() {
        return k_specific;
    }
}
