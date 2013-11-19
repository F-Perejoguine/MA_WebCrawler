/**
 * Created with IntelliJ IDEA.
 * User: Fjodor
 * Date: 18.11.13
 * Time: 21:44
 * To change this template use File | Settings | File Templates.
 */
import java.util.Comparator;
import java.lang.Math;

public class LinkSort implements Comparator<Link> {

    public int compare(Link a, Link b) {

        if(a.rating > b.rating) {
            return -1;
        }else if(a.rating < b.rating) {
            return 1;
        } else {
            return 0;
        }
    }
}
