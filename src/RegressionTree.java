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

    public boolean isLeaf = false;
    private double value;
    public RegressionTree subtreeR;
    public RegressionTree subtreeL;

    public double errIfLeaf;

    public RegressionTree(int[][] datamatrix) {
        workingmatrix = datamatrix;
        datamatrix = null;

        //Calculate average target value
        value = 0;
        for(int i = 0; i < workingmatrix.length; i++)
            value = value + pointY(i);

        value = value / workingmatrix.length;

        //Calculate Error Sum
        calculateError();

        //Calculate amount of unique output values in matrix.
        int ycount = 1;
        if(workingmatrix.length > 0) {
            ycount  = 0;
            int current = -1;
            boolean maxnotreached = true;

            while(maxnotreached) {
                maxnotreached = false;
                int s = -1;

                for(int i = 0; i < workingmatrix.length; i++)
                    if(workingmatrix[i][0] > current && (s == -1 || workingmatrix[i][0] < s)) {
                            s = workingmatrix[i][0];
                            maxnotreached = true;
                    }

                if(maxnotreached) {
                    current = s;
                    ycount++;
                }
            }
        }

        //If stopping condition has occurred, create leaf, else calculate split-point and variable and create two subtrees.
        if(ycount < 2) {
            isLeaf = true;
        } else {
            int arrsplitpoint = findSplitPoint();
            sortby(splitvar);

            int[][] rightarray = new int[workingmatrix.length - arrsplitpoint - 1][2];
            int[][] leftarray = new int[arrsplitpoint + 1][2];

            System.arraycopy(workingmatrix, arrsplitpoint + 1, rightarray, 0, workingmatrix.length - arrsplitpoint - 1);
            System.arraycopy(workingmatrix, 0, leftarray, 0, arrsplitpoint + 1);
            workingmatrix = null;

            subtreeL = new RegressionTree(leftarray);
            subtreeR = new RegressionTree(rightarray);
        }
    }

    public RegressionTree(int nsplitpoint, int nsplitvar, double nvalue, RegressionTree nsubtreeL, RegressionTree nsubtreeR) {
        splitpoint = nsplitpoint;
        splitvar = nsplitvar;
        value = nvalue;
        isLeaf = false;
        subtreeL = nsubtreeL;
        subtreeR = nsubtreeR;
    }

    public RegressionTree(int nsplitpoint, int nsplitvar, double nvalue) {
        splitpoint = nsplitpoint;
        splitvar = nsplitvar;
        value = nvalue;
        isLeaf = true;
    }

    public RegressionTree prune() {
        if(isLeaf) {
            return this;
        } else {
            RegressionTree l = subtreeL.prune();
            RegressionTree r = subtreeR.prune();

            double errRateL = 0;
            if(!l.isLeaf)
                errRateL = (l.errIfLeaf - l.getErrSum()) / ((double)l.getLeaves() - 1.0);
            double errRateR = 0;
            if(!r.isLeaf)
                errRateR = (r.errIfLeaf - r.getErrSum()) / ((double)r.getLeaves() - 1.0);
            double errRateT = (errIfLeaf - getErrSum()) / ((double)getLeaves() - 1.0);

            if(!l.isLeaf && errRateL < errRateT && !(!r.isLeaf && errRateR < errRateL)) {
                return l;
            } else if(!r.isLeaf && errRateR < errRateT) {
                return r;
            } else {
                return this;
            }
        }
    }

    public int getLeaves() {
        if(isLeaf) {
            return 1;
        } else {
            return subtreeL.getLeaves() + subtreeR.getLeaves();
        }
    }

    public double getErrSum() {
        if(isLeaf) {
            return errIfLeaf;
        } else {
            return subtreeL.getErrSum() + subtreeR.getErrSum();
        }
    }

    private void calculateError() {
        errIfLeaf = 0;
        for(int i = 0; i < workingmatrix.length; i++)
            errIfLeaf = errIfLeaf + Math.abs(pointY(i) - value);
    }

    public double getTotalError(int[][] validationMatrix) {
        workingmatrix = validationMatrix;

        if(isLeaf) {
            double valError = 0;
            for(int i = 0; i < workingmatrix.length; i++)
                valError = valError + Math.abs(pointY(i) - value);

            return valError;
        } else {
            sortby(splitvar);

            int arrsplitpoint = 0;
            for(int i = 0; i < workingmatrix.length; i++) {
                if (splitvar == 4) {
                    if(!pointB(i)) {
                        arrsplitpoint = i - 1;
                        break;
                    } else if(i == workingmatrix.length - 1) {
                        arrsplitpoint = i;
                    }
                } else {
                    if(pointN(i, splitvar) > splitpoint) {
                        arrsplitpoint = i - 1;
                        break;
                    }
                }
            }

            int[][] rightarray = new int[workingmatrix.length - arrsplitpoint - 1][2];
            int[][] leftarray = new int[arrsplitpoint + 1][2];

            System.arraycopy(workingmatrix, arrsplitpoint + 1, rightarray, 0, workingmatrix.length - arrsplitpoint - 1);
            System.arraycopy(workingmatrix, 0, leftarray, 0, arrsplitpoint + 1);
            workingmatrix = null;

            double errSum = 0;

            if(rightarray.length != 0)
                errSum = errSum + subtreeR.getTotalError(rightarray);
            if(leftarray.length != 0)
                errSum = errSum + subtreeL.getTotalError(leftarray);

            return errSum;
        }
    }

    public RegressionTree copy() {
        if(isLeaf) {
            return new RegressionTree(splitpoint, splitvar, value);
        } else {
            return new RegressionTree(splitpoint, splitvar, value, subtreeL.copy(), subtreeR.copy());
        }
    }

    private int findSplitPoint() {
        int bestsplitpoint = 0;
        int bestsplitarrpoint = 0;
        int bestsplitvar = 0;

        double bestsplitvalue = 0;

        int N_ofall = workingmatrix.length;
        double S_ofall = 0;
        for(int i = 0; i < N_ofall; i++)
            S_ofall = S_ofall + pointY(i);

        for(int x = 0; x < Config.datasetsize; x++) {
            sortby(x);
            double sr = S_ofall;
            double sl = 0;
            double nr = N_ofall;
            double nl = 0;

            if(x == 4) {
                for(int i = 0; i < workingmatrix.length - 1; i++) {
                    sl = sl + pointY(i);
                    sr = sr - pointY(i);
                    nl++;
                    nr--;

                    if(!pointB(i + 1)) {
                        double newsplitvalue = (sl * sl / nl) + (sr * sr / nr);
                        if(newsplitvalue > bestsplitvalue) {
                            bestsplitvalue = newsplitvalue;
                            bestsplitarrpoint = i;
                            bestsplitvar = x;
                        }
                        break;
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
                if(!pointB(i) && pointB(j))
                {
                    int a = workingmatrix[i][0];
                    int b = workingmatrix[i][1];
                    workingmatrix[i][0] = workingmatrix[j][0];
                    workingmatrix[i][1] = workingmatrix[j][1];
                    workingmatrix[j][0] = a;
                    workingmatrix[j][1] = b;
                    i++;
                } else if(pointB(i)) {
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
