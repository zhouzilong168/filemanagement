package frame;

import data.DataProcessing;
import port.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class loginFrame extends JFrame implements ActionListener {
    private static final long serialVersionUID = -7819688888899405353L;
    private JLabel userLab = new JLabel("�û���"),
            pwsLab = new JLabel("����    ");
    private static TextField userText = new TextField(15);
    private static TextField pwsText = new TextField(15);
    private JButton sureBut = new JButton("ȷ��"),
            cancelBut = new JButton("ȡ��");

    public loginFrame() {
        super("ϵͳ��¼");
        ImageIcon icon = new ImageIcon("D:\\OOP\\pictrue\\welcome.jpg");
        JLabel img = new JLabel(icon);//ͨ��Labelʵ��ͼƬ����
        this.getLayeredPane().add(img, new Integer(Integer.MIN_VALUE));//���뵽Frame
        img.setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight());//���ô�С
        Container contain = this.getContentPane();
        ((JPanel) contain).setOpaque(false);
        setSize(250, 200);
//		this.setBackground(Color.CYAN);
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));
        add(userLab);
        add(userText);
        add(pwsLab);
        add(pwsText);
        add(sureBut);
        add(cancelBut);
        pwsText.setEchoChar('*');
        pwsText.addActionListener(this);
        sureBut.addActionListener(this);
        cancelBut.addActionListener(this);
        setLocationRelativeTo(null);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sureBut || e.getSource() == pwsText) {
            Client.sendData("Logining");
            Client.sendData(userText.getText());
            Client.sendData(pwsText.getText());
            System.out.println(userText.getText() + "|" + pwsText.getText());
        } else if (e.getSource() == cancelBut) {
            System.exit(0);
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        DataProcessing.connectToDatabase();
        new loginFrame();
    }

}
