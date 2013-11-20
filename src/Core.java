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

    public List<Link> collection;

    public Core() {
        collection = new ArrayList();
    }

    public void calculatePriority(Link li) {
        li.rating = 100.0 * Math.random();
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
        collection.add(input);
    }

    public int getCollectionTotal() {
        return collection.size();
    }
}
