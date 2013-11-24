/**
 * Created with IntelliJ IDEA.
 * User: Fjodor
 * Date: 22.11.13
 * Time: 18:25
 * To change this template use File | Settings | File Templates.
 */
public class RegressionTree {

    public int splitvar;
    public int splitpoint;
    private int[][] workingmatrix;

    private boolean isLeaf = false;
    private double value;
    private RegressionTree subtreeR;
    private RegressionTree subtreeL;

    public RegressionTree(int[][] datamatrix) {
        workingmatrix = datamatrix;
        datamatrix = null;

        //If stopping condition has occurred, create leaf, else calculate split-point and variable and create two subtrees.
        if(workingmatrix.length < 10) {
            Config.leafamount++;

            isLeaf = true;

            value = 0;
            for(int i = 0; i < workingmatrix.length; i++)
                value = value + pointY(i);

            value = value / workingmatrix.length;
        } else {
            Config.nodeamount++;

            int arrsplitpoint = findSplitPoint();
            sortby(splitvar);

            int[][] rightarray = new int[workingmatrix.length - arrsplitpoint - 1][2];
            int[][] leftarray = new int[arrsplitpoint + 1][2];

            System.arraycopy(workingmatrix, arrsplitpoint + 1, rightarray, 0, workingmatrix.length - arrsplitpoint - 1);
            System.arraycopy(workingmatrix, 0, leftarray, 0, arrsplitpoint + 1);

            subtreeL = new RegressionTree(leftarray);
            subtreeR = new RegressionTree(rightarray);
            workingmatrix = null;
        }
    }

    public int findSplitPoint() {
        int bestsplitpoint = 0;
        int bestsplitarrpoint = 0;
        int bestsplitvar = 0;

        double bestsplitvalue = 0;

        int Nofall = workingmatrix.length;
        double Sofall = 0;
        for(int i = 0; i < Nofall; i++)
            Sofall = Sofall + pointY(i);


        for(int x = 0; x < Config.datasetsize; x++) {
            sortby(x);
            double sr = Sofall;
            double sl = 0;
            double nr = Nofall;
            double nl = 0;

            if(x == 4) {
                for(int i = 0; i < workingmatrix.length - 1; i++) {
                    sl = sl + pointY(i);
                    sr = sr - pointY(i);
                    nl++;
                    nr--;

                    if(pointB(i + 1) == false) {
                        double newsplitvalue = (sl * sl / nl) + (sr * sr / nr);
                        if(newsplitvalue > bestsplitvalue) {
                            bestsplitvalue = newsplitvalue;
                            bestsplitarrpoint = i;
                            bestsplitvar = x;
                        }
                    }
                }
            } else {
                for(int i = 0; i < workingmatrix.length - 1; i++) {
                    sl = sl + pointY(i);
                    sr = sr - pointY(i);
                    nl++;
                    nr--;

                    if(!(pointN(i, x) == pointN(i + 1, x))) {

                        double newsplitvalue = (sl * sl / nl) + (sr * sr / nr);
                        if(newsplitvalue > bestsplitvalue) {
                            bestsplitvalue = newsplitvalue;
                            bestsplitarrpoint = i;
                            bestsplitvar = x;
                            bestsplitpoint = pointN(i, x);
                        }
                    }
                }
            }
        }
        splitpoint = bestsplitpoint;
        splitvar = bestsplitvar;
        return bestsplitarrpoint;
    }

    public int pointN(int i, int varindex) {
            return (Integer) Config.core.collection.get(workingmatrix[i][0]).getRef(workingmatrix[i][1]).get(varindex);
    }

    public boolean pointB(int i) {
            return (Boolean) Config.core.collection.get(workingmatrix[i][0]).getRef(workingmatrix[i][1]).get(4);
    }

    public double pointY(int i) {
            return Config.core.collection.get(workingmatrix[i][0]).rating;
    }

    public void sortby(int x) {
        if (x == 4) {   //If boolean sort by the order true, false;
            int i = 0;
            int j = workingmatrix.length - 1;

            while( i != j) {
                if(pointB(i) == false && pointB(j) == true)
                {
                    int a = workingmatrix[i][0];
                    int b = workingmatrix[i][1];
                    workingmatrix[i][0] = workingmatrix[j][0];
                    workingmatrix[i][1] = workingmatrix[j][1];
                    workingmatrix[j][0] = a;
                    workingmatrix[j][1] = b;
                    i++;
                } else if(pointB(i) == true) {
                    i++;
                } else j--;
            }
        } else {
            quicksort(x, 0, workingmatrix.length - 1);
        }
    }

    private void quicksort(int x, int start, int end) {
        if(start < end) {
            int pivot = pointN(end, x);
            int pivot_u = workingmatrix[end][0];
            int pivot_v = workingmatrix[end][1];
            int i = start;
            int j = end;

            while(i != j) {
                if (pointN(i, x) > pivot) {
                    workingmatrix[j] = workingmatrix[i];
                    workingmatrix[i] = workingmatrix[j - 1];
                    j--;
                } else i++;
            }

            workingmatrix[j][0] = pivot_u;
            workingmatrix[j][1] = pivot_v;

            quicksort(x, start, j-1);
            quicksort(x, j+1, end);
        }
    }

    public double estimateY(Datapoint dp) {
        if (isLeaf) {
            return value;
        } else {
            if (splitvar == 4) {
                if ((Boolean)dp.get(splitvar)) {
                    return subtreeL.estimateY(dp);
                } else {
                    return subtreeR.estimateY(dp);
                }
            } else {
                if((Integer)dp.get(splitvar) > splitpoint) {
                    return subtreeR.estimateY(dp);
                } else {
                    return subtreeL.estimateY(dp);
                }
            }
        }
    }
}
