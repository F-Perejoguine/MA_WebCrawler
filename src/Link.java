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
    private List<Dataset> data;

    public Link(String u, double r) {
        url = u;
        rating = r;
        data = new ArrayList();
    }

    public void addRef(Dataset a) {
        data.add(a);
    }

    public Dataset getRef(int index) {
        return data.get(index);
    }

    public int getRefNumber() {
        return data.size();
    }
}
