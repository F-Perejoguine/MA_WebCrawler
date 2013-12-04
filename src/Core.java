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
        long ctime = System.currentTimeMillis();

        MatrixSet root = createRootMatrix();
        System.out.println("Training set: " + root.trMatrix.length + "   Validation set: " + root.valMatrix.length);
        treeRoot = new RegressionTree(root.trMatrix);


        double f_time = Math.round((double)(System.currentTimeMillis() - ctime)) / 1000;

        int leaves = treeRoot.getLeaves();
        int nodes = leaves - 1;

        System.out.println("RegressionTree model created in " + f_time + " second with " + nodes + " nodes and " + leaves + " leaves.");


        ctime = System.currentTimeMillis();
        double leasterror = -1;

        if(root.valMatrix.length > 5) {
            RegressionTree workingtree = treeRoot;
            RegressionTree besttree = workingtree;
            leasterror = workingtree.getTotalError(root.valMatrix);

            while(!workingtree.isLeaf) {
                RegressionTree prunetree = workingtree.prune();
                prunetree.isLeaf = true;
                prunetree.subtreeL = null;
                prunetree.subtreeR = null;
                double treeError = workingtree.getTotalError(root.valMatrix);

                System.out.println("Pruning... " + workingtree.getLeaves() + "     " + treeError);

                if(treeError < leasterror) {
                    besttree = workingtree.copy();
                    leasterror = treeError;
                }
            }
            treeRoot = besttree;
        }

        leaves = treeRoot.getLeaves();
        nodes = leaves - 1;


        double p_time = Math.round((double)(System.currentTimeMillis() - ctime)) / 1000;
        System.out.println("Pruned to " + nodes + " nodes, and " + leaves + " leaves in " + p_time + " seconds. Total Error of " + leasterror);
    }

    public MatrixSet createRootMatrix() {
        int trsize = 0; //Size of training data set
        int valsize = 0; //Size of validation data set

        for(Link element : collection) {
            int size = element.getRefNumber();
            if(size == 2) {
                trsize++;
                valsize++;
            } else if(size > 2) {
                int third = (int)((double)size / 3.0);
                valsize = valsize + third;
                trsize = trsize + size - third;
            } else {
                trsize = trsize + size;
            }
        }

        MatrixSet root = new MatrixSet(new int[trsize][2], new int[valsize][2]);

        int index = 0;
        int vindex = 0;

        for(int i = 0; i < collection.size(); i++) {
            if(collection.get(i).getRefNumber() < 3) {
                for(int j = 0; j < collection.get(i).getRefNumber(); j++) {
                    if(j == 0) {
                        root.trMatrix[index][0] = i;
                        root.trMatrix[index][1]= j;
                        index++;
                    } else {
                        root.valMatrix[vindex][0] = i;
                        root.valMatrix[vindex][1]= j;
                        vindex++;
                    }
                }
            } else {
                int third =(int)((double)collection.get(i).getRefNumber() / 3.0);
                for(int j = 0; j < collection.get(i).getRefNumber() - third; j++) {
                    root.trMatrix[index][0] = i;
                    root.trMatrix[index][1] = j;
                    index++;
                }
                for(int j = collection.get(i).getRefNumber() - third; j < collection.get(i).getRefNumber(); j++) {
                    root.valMatrix[vindex][0] = i;
                    root.valMatrix[vindex][1]= j;
                    vindex++;
                }
            }
        }

        return root;
    }
}
