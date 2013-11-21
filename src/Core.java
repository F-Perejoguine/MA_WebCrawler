/**
 * Created with IntelliJ IDEA.
 * User: Fjodor
 * Date: 19.11.13
 * Time: 18:51
 * To change this template use File | Settings | File Templates.
 */
import java.util.*;
import java.lang.Math;

public class Core {

    public PriorityQueue<Link> collection;

    public Core() {
        LinkSort ls = new LinkSort();
        collection = new PriorityQueue<Link>(1, ls);
    }

    public void calculatePriority(Link li) {
        //li.rating = 100.0 * Math.random();
        li.rating = li.getRef(0).sourceRating;
    }

    public boolean checkCollection(Link input) {

        boolean alreadyexists = false;
        for(Link element : collection) {
            if(input.url.equals(element.url)) {
                alreadyexists = true;
                element.addRef(input.getRef(0));
            }
        }

        return alreadyexists;
    }

    public void addPage(Link input) {
        collection.offer(input);
    }

    public int getCollectionTotal() {
        return collection.size();
    }
}
