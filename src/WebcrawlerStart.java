import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: Fjodor
 * Date: 20.11.13
 * Time: 21:40
 * To change this template use File | Settings | File Templates.
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.logging.*;
import javax.swing.JButton;
import javax.swing.JFrame;

public class WebcrawlerStart {
    private JPanel panel1;
    private JTextArea seedLinksTextArea;
    private JTextField maxDomains;
    private JTextField textField1;
    private JTextField thematic_keywords;
    private JTextField textField3;
    private JComboBox logleveldetail;
    private JComboBox stopConditiontxt;
    private JTextField stopParametertxt;
    private JCheckBox enableQueuedURLSCheckBox;
    private JButton startButton;
    private JCheckBox disableLearningTreePruningCheckBox;
    private JTextField updateMultipliertxt;
    private JCheckBox onlySeed;
    private JCheckBox maxDomainchk;

    static private JFrame mainframe;

    public static void main(String[] args) {
        JFrame frame = new JFrame("WebcrawlerStart");
        mainframe = frame;

        frame.setContentPane(new WebcrawlerStart().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);


    }

    public WebcrawlerStart() {
        stopConditiontxt.addItem("Certain amount of pages is parsed");
        stopConditiontxt.addItem("Certain amount of time has elapsed (in m)");
        stopConditiontxt.addItem("No more memory available");
        logleveldetail.addItem("INFO");
        logleveldetail.addItem("FINE");
        logleveldetail.addItem("FINER");
        logleveldetail.addItem("FINEST");

        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                    boolean inputcorrect = true;





                    if(inputcorrect) start();
                }
            });


    }

    private void start() {



        String[] kw_t = thematic_keywords.getText().split(",");
        for(int i = 0; i < kw_t.length; i++)
            kw_t[i] = kw_t[i].trim();

        Level logdetail = Level.INFO;
        String selecteditem = logleveldetail.getSelectedItem().toString();

        if(selecteditem.equals("INFO")) {
            logdetail = Level.INFO;
        } else if(selecteditem.equals("FINE")) {
            logdetail = Level.FINE;
        } else if(selecteditem.equals("FINER")) {
            logdetail = Level.FINER;
        } else if(selecteditem.equals("FINEST")) {
            logdetail = Level.FINEST;
        }

        String selectedcondition = stopConditiontxt.getSelectedItem().toString();
        int condition = 0;

        if(selectedcondition.equals("Certain amount of pages is parsed")) {
            condition = 0;
        } else if(selectedcondition.equals("Certain amount of time has elapsed (in m)")) {
            condition = 1;
        } else if(selectedcondition.equals("No more memory available")) {
            condition = 2;
        }

        Config.setSeedLinks(seedLinksTextArea.getText().split("\\r?\\n"));
        Config.setKeywords(kw_t, null, null);

        if(condition != 2) Config.crawlNumber = Integer.parseInt(stopParametertxt.getText().trim());
        Config.stopCondition = condition;
        Config.stopParameter = Integer.parseInt(stopParametertxt.getText().trim());
        if(maxDomainchk.isSelected()) Config.domainmax = Integer.parseInt(maxDomains.getText().trim());
        Config.onlyseeds = onlySeed.isSelected();
        Config.updatemultiplier = Double.parseDouble(updateMultipliertxt.getText().trim());
        Config.prune = !disableLearningTreePruningCheckBox.isSelected();
        Config.loglevel = logdetail;



        Config.lQueue = new Queue(Config.getSeedLinks());
        Config.core = new Core();
        Config.initializeLogger();

        mainframe.dispose();
        new WebcrawlerRunning();
        //Webcrawler.main(new String[0]);

    }
}
