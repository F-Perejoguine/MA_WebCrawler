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

    //public PriorityQueue<Link> collection;
    public List<Link> collection = new ArrayList<Link>();
    private RegressionTree treeRoot;

    public Core() {
        //LinkSort ls = new LinkSort();
        //collection = new PriorityQueue<Link>(1, ls);
    }

    public void calculatePriority(Link li) {
        li.rating = 100.0 * Math.random();
        //li.rating = li.getRef(0).srcmatches;
    }

    public boolean checkCollection(Link input) {
        boolean alreadyexists = false;

        for(Link element : collection) {
            if(input.url.equals(element.url)) {
                alreadyexists = true;
                if(!element.checkDatapoints(input.getRef(0))) element.addRef(input.getRef(0));
            }
        }

        return alreadyexists;
    }

    public void addPage(Link input) {
        //collection.offer(input);
        collection.add(input);
    }

    public int getCollectionTotal() {
        return collection.size();
    }

    public void reloadModel() {
        System.out.println("Creating Regression tree model...");
        long ctime = System.currentTimeMillis();

        treeRoot = new RegressionTree(createRootMatrix());
    }


    public int[][] createRootMatrix() {
        int size = 0;

        for(int i = 0; i < collection.size(); i++)
            size = size + collection.get(i).getRefNumber();

        int[][] rootMatrix = new int[size][2];

        for(int i = 0; i < collection.size(); i++) {
            for(int j = 0; j < collection.get(i).getRefNumber(); j++) {
            rootMatrix[i][0] = i;
            rootMatrix[i][1]= collection.get(i).getRefNumber();
            }
        }

        return rootMatrix;
    }
}
