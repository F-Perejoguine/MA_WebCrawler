/**
 * Created with IntelliJ IDEA.
 * User: Fjodor
 * Date: 05.12.13
 * Time: 20:18
 * To change this template use File | Settings | File Templates.
 */
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.swing.*;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;


public class WebcrawlerRunning {
    private JTextArea logArea;
    private JPanel rootPanel;
    private JButton stopButton;
    private JLabel currentURL_lbl;
    private JLabel currentMemory_lbl;
    private JLabel time_lbl;
    private JLabel queueSize_lbl;
    private JLabel pagesParsed_lbl;
    private JLabel parsingErrors;
    private JLabel averageRating_lbl;
    private JLabel avgPageTime_lbl;
    private JLabel reach_lbl;
    private JLabel domains_lbl;
    private JLabel tree_lbl;
    private JLabel pruned_lbl;
    private JLabel trdata_lbl;
    private JLabel valdata_lbl;
    private JLabel treeError_lbl;
    private JLabel leafError_lbl;
    private JLabel treeLife_lbl;
    private JLabel avgerror_lbl;
    private RunningUpdate current;

    SwingWorker<Void, RunningUpdate> crawler;

    public WebcrawlerRunning() {
        JFrame frame = new JFrame("WebcrawlerRunning");
        frame.setContentPane(rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);


        Timer timer = new Timer(1000, new ActionListener() {
            int seconds = 0;
            int minutes = 0;

            public void actionPerformed(ActionEvent e) {
                if(seconds == 59) {
                    seconds = 0;
                    minutes++;
                } else seconds++;

                time_lbl.setText(minutes + ":" + seconds);
            }
        });

        timer.start();

        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                crawler.cancel(true);
            }
        });


        crawler = new SwingWorker<Void, RunningUpdate>() {

            @Override
            protected Void doInBackground() throws Exception {
                // Simulate doing something useful.

                    // The type we pass to publish() is determined
                    // by the second template parameter.
                    //publish(i);
                Config.logger.log(Level.INFO, "Starting Webcrawl for " + Config.crawlNumber + " pages.");
                long ctime = System.currentTimeMillis();
                double ratingsum = 0;
                int trees = 0;
                double error = 0;
                boolean conditionnotmet = false;
                int treelife = 0;

                RunningUpdate regularUpdate = new RunningUpdate();

                do {
                    Link workinglink = Config.lQueue.get();
                    String workingURL = workinglink.url;

                    publish(new RunningUpdate("Parsing page Nr. " + (Config.core.getCollectionTotal() + 1) + ": " + workingURL));
                    regularUpdate.currentURL = workingURL;
                    regularUpdate.queuesize = Config.lQueue.size();
                    publish(regularUpdate);

                    boolean parsesuccess = true;
                    Document doc = null;
                    String domain = "";
                    try {
                        domain = new URI(workingURL).getHost();
                    } catch(Exception e) {}

                    try {
                        doc = Jsoup.connect(workingURL).get();
                    }
                    catch (Exception e) {
                        Config.logger.log(Level.WARNING, e.getMessage() +  " parsing " + workingURL);
                        publish(new RunningUpdate("Error : " + e.getMessage() + " while parsing " + workingURL));
                        parsesuccess = false;
                    }

                    if (doc == null) parsesuccess = false;

                    if(parsesuccess) {
                        //DOMtoFile(doc);
                        Website current = new Website(doc, workingURL);
                        double prediction = workinglink.rating;
                        workinglink.rating = current.getRating();
                        error = error + Math.abs(prediction - workinglink.rating);

                        if(treelife == 0) {
                            Config.core.reloadModel(regularUpdate);
                            treelife = (int)((double)regularUpdate.trdata * Config.updatemultiplier / 80.0);
                            trees++;
                        } else treelife--;

                        Config.core.addPage(workinglink);
                        Config.lQueue.updatePriorities();
                        current.parseLinks();

                        ratingsum = ratingsum + workinglink.rating;
                    } else {
                        Config.flinks.add(workingURL);
                    }

                    double i_time = (double)(System.currentTimeMillis() - ctime);

                    regularUpdate.treelife = treelife;
                    regularUpdate.totalpages = Config.core.getCollectionTotal();
                    regularUpdate.totalerrors = Config.flinks.size();
                    regularUpdate.averagerating = (ratingsum / (double)Config.core.getCollectionTotal());
                    regularUpdate.averagetime = (double)Math.round(i_time / (double)Config.core.getCollectionTotal()) / 1000.0;
                    regularUpdate.reach = 0;
                    regularUpdate.domains = 0;

                    regularUpdate.treenumber = trees;
                    regularUpdate.avgerror = error / (double)Config.core.getCollectionTotal();


                    switch (Config.stopCondition) {
                        case 0:
                            conditionnotmet = Config.core.getCollectionTotal() < Config.stopParameter;
                            break;
                        case 1:
                            double time = (double)(System.currentTimeMillis() - ctime)/1000;
                            conditionnotmet = time < (double)(Config.stopParameter * 60);
                            break;
                        case 2:
                            conditionnotmet = true;
                            break;
                    }
                } while(conditionnotmet);

                publish(new RunningUpdate("Process finished with " + Config.core.getCollectionTotal() + " pages parsed: Stopping condition met."));
                publish(regularUpdate);

                // Here we can return some object of whatever type
                // we specified for the first template parameter.
                // (in this case we're auto-boxing 'true').
                return null;
            }

            // Can safely update the GUI from this method.
            protected void done() {
                try {
                    get();
                    System.out.println("normal");
                } catch(InterruptedException e) {
                    System.out.println("interrupted");
                } catch(Exception e) {

                }
            }

            @Override
            // Can safely update the GUI from this method.
            protected void process(List<RunningUpdate> chunks) {
                // Here we receive the values that we publish().
                // They may come grouped in chunks.
                for(RunningUpdate update : chunks) {
                    if(update.isLog) {
                        logArea.append("\n" + update.logmessage);

                    } else {
                        // Get current size of heap in bytes
                        long heapSize = Runtime.getRuntime().totalMemory();

                        // Get maximum size of heap in bytes. The heap cannot grow beyond this size.// Any attempt will result in an OutOfMemoryException.
                        long heapMaxSize = Runtime.getRuntime().maxMemory();

                        currentMemory_lbl.setText(((double)Math.round((double)heapSize * 10000.0 / (double)heapMaxSize)/100.0) + " %");


                        queueSize_lbl.setText(Integer.toString(update.queuesize));
                        pagesParsed_lbl.setText(Integer.toString(update.totalpages));
                        parsingErrors.setText(Integer.toString(update.totalerrors));
                        averageRating_lbl.setText(Double.toString(Math.round(update.averagerating * 100) / 100));
                        avgPageTime_lbl.setText(Double.toString(update.averagetime));
                        reach_lbl.setText(Integer.toString(update.reach));
                        domains_lbl.setText(Integer.toString(update.domains));
                        tree_lbl.setText("Regression Tree Nr. " + update.treenumber + " created in " + update.treetime + " seconds with " + update.treeleaves + " leaves.");
                        pruned_lbl.setText("Pruned in " + update.prunedtime + " seconds, to " + update.prunedleaves + " leaves.");
                        trdata_lbl.setText(Integer.toString(update.trdata));
                        valdata_lbl.setText(Integer.toString(update.valdata));
                        treeError_lbl.setText(Double.toString(Math.round(update.totaltreerror * 100) / 100));
                        leafError_lbl.setText(Double.toString(Math.round(update.leaferror * 100) / 100));
                        avgerror_lbl.setText(Double.toString(update.avgerror));
                        treeLife_lbl.setText(Integer.toString(update.treelife));
                    }
                }
            }

        };

        crawler.execute();
    }

    private void Stop() {
        crawler.cancel(true);
    }
}
