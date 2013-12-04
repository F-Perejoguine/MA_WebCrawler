/**
 * Created with IntelliJ IDEA.
 * User: Fjodor
 * Date: 19.11.13
 * Time: 16:53
 * To change this template use File | Settings | File Templates.
 */
public class Datapoint {

    public int srcmatches;
    public int srccontent;
    public int linkmatches;
    public int urlmatches;
    public boolean samedomain;

    public Datapoint(int srcm, int srcc, int lm, int urlm, boolean sdomain) {
        srcmatches = srcm;
        srccontent = srcc;
        linkmatches = lm;
        urlmatches = urlm;
        samedomain = sdomain;
    }

    public boolean dataequals(Datapoint dp) {
        return srcmatches == dp.srcmatches && srccontent == dp.srccontent && linkmatches == dp.linkmatches && urlmatches == dp.urlmatches && samedomain == dp.samedomain;
    }

    public Object get(int index) {
        switch(index) {
            case 0:
                return srccontent;
            case 1:
                return srcmatches;
            case 2:
                return linkmatches;
            case 3:
                return urlmatches;
            case 4:
                return samedomain;
            default:
                return null;
        }
    }
}
