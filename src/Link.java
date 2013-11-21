/**
 * Created with IntelliJ IDEA.
 * User: Fjodor
 * Date: 18.11.13
 * Time: 13:30
 * To change this template use File | Settings | File Templates.
 */
import java.util.ArrayList;
import java.util.List;

public class Link {

    public String url;
    public double rating;
    public List<Datapoint> data;

    public Link(String u, double r) {
        url = u;
        rating = r;
        data = new ArrayList<Datapoint>();
    }

    public void addRef(Datapoint a, String actor) {
        //System.out.println("ADDREFERENCE from " + actor + " from     " + a.sourceURL);
        data.add(a);
    }

    public Datapoint getRef(int index) {
        return data.get(index);
    }

    public int getRefNumber() {
        return data.size();
    }

    public boolean checkDatapoints(Datapoint dp) {
        boolean doubledata = false;

        for (int i = 0; i < data.size(); i++)  //loop through all datapoints of link to check for exact matches for the provided Datapoint.
            if (data.get(i).dataequals(dp)) {
                doubledata = true;
                break;
            }

        return doubledata;
    }
}
