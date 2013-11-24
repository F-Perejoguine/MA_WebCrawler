import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: Fjodor
 * Date: 20.11.13
 * Time: 21:40
 * To change this template use File | Settings | File Templates.
 */
public class WebcrawlerStart {
    private JPanel panel1;
    private JTextArea seedLinksTextArea;
    private JCheckBox respectRobotsTxtProtocolCheckBox;
    private JRadioButton onlyStayOnSeedRadioButton;
    private JRadioButton onlyParseACertainRadioButton;
    private JTextField textField7;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JComboBox comboBox1;
    private JComboBox comboBox2;
    private JComboBox comboBox3;
    private JTextField textField4;
    private JTextField textField5;
    private JButton button1;
    private JCheckBox enableQueuedURLSCheckBox;
    private JButton button2;

    public static void main(String[] args) {
        JFrame frame = new JFrame("WebcrawlerStart");
        frame.setContentPane(new WebcrawlerStart().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public WebcrawlerStart() {





    }
}
