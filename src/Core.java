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
        double sum = 0;

        for(int i = 0; i < li.getRefNumber(); i++)
            sum = sum + treeRoot.estimateY(li.getRef(i));

        li.rating = sum / (double)li.getRefNumber();
    }

    public double calculatePriority(Datapoint dp) {
        return treeRoot.estimateY(dp);
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

        double f_time = (double)(System.currentTimeMillis() - ctime)/1000;

        System.out.println("RegressionTree model created in " + f_time + " second with " + Config.nodeamount + " nodes and " + Config.leafamount + " leaves.");
        Config.nodeamount = 0;
        Config.leafamount = 0;

    }

    public int[][] createRootMatrix() {
        int size = 0;

        for(int i = 0; i < collection.size(); i++)
            size = size + collection.get(i).getRefNumber();

        int[][] rootMatrix = new int[size][2];

        int index = 0;
        for(int i = 0; i < collection.size(); i++) {
            for(int j = 0; j < collection.get(i).getRefNumber(); j++) {
                rootMatrix[index][0] = i;
                rootMatrix[index][1]= j;
                index++;
            }
        }

        return rootMatrix;
    }
}
