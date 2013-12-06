/**
 * Created with IntelliJ IDEA.
 * User: Fjodor
 * Date: 05.12.13
 * Time: 23:35
 * To change this template use File | Settings | File Templates.
 */
public class RunningUpdate {
    public boolean isLog;
    public String logmessage;

    public String currentURL;
    public int queuesize;

    public int totalpages;
    public int totalerrors;
    public double averagerating;
    public double averagetime;
    public int reach;
    public int domains;

    public int trdata;
    public int valdata;

    public int treenumber;
    public int treeleaves;
    public double treetime;
    public int prunedleaves;
    public double prunedtime;
    public int treelife;

    public double totaltreerror;
    public double leaferror;
    public double avgerror;


    public RunningUpdate() {
        this.isLog = false;
    }

    public RunningUpdate(String logmessage) {
        this.logmessage = logmessage;
        this.isLog = true;
    }
}
