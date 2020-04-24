package frame;

import port.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;

/**
 * ��¼����
 */
public class LoginFrame extends JFrame implements ActionListener {
    private static final long serialVersionUID = -7819688888899405353L;
    private JLabel userLab = new JLabel("�û���"),
            pwsLab = new JLabel("����    ");
    private TextField userText = new TextField(15);
    private TextField pwsText = new TextField(15);
    private JButton sureBut = new JButton("ȷ��"),
            cancelBut = new JButton("ȡ��");
    private ObjectOutputStream oos = null;

    public LoginFrame() {
        super("ϵͳ��¼");
        oos = Client.getOutStream();
        showGUI();
    }

    public LoginFrame(ObjectOutputStream oos) {
        super("ϵͳ��¼");
        this.oos = oos;
        showGUI();
    }

    private void showGUI() {
        URL path = getClass().getClassLoader().getResource("resources/pictrue/welcome.jpg");
        ImageIcon icon = new ImageIcon(path);
        JLabel img = new JLabel(icon);//ͨ��Labelʵ��ͼƬ����
        this.getLayeredPane().add(img, new Integer(Integer.MIN_VALUE));//���뵽Frame
        img.setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight());//���ô�С
        Container contain = this.getContentPane();
        ((JPanel) contain).setOpaque(false);
        setSize(250, 200);
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
            sendData("Logining");
            sendData(userText.getText());
            sendData(pwsText.getText());
        } else if (e.getSource() == cancelBut) {
            System.exit(0);
        }
    }

    private void sendData(String message) {
        try {
            oos.writeObject("CLIENT>>> " + message);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
