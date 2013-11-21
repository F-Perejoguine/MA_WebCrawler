/**
 * Created with IntelliJ IDEA.
 * User: Fjodor
 * Date: 19.11.13
 * Time: 16:53
 * To change this template use File | Settings | File Templates.
 */
public class Dataset {

    //public String sourceURL;
    public double sourceRating;
    public int inlinkmatches;

    public Dataset(double srating, int matches) {
        //sourceURL = sURL;
        sourceRating = srating;
        inlinkmatches = matches;
    }
}
