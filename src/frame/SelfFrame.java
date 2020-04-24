package frame;

import domain.User;
import port.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;

/**
 * 个人信息修改窗体
 */
public class SelfFrame extends JFrame implements ActionListener {
    private static final long serialVersionUID = -2274384143825653568L;
    private JPanel panel = new JPanel();
    private JLabel userLab = new JLabel("用户名"),
            ppwsLab = new JLabel("原密码"),
            npwsLab = new JLabel("新密码"),
            npwsLab1 = new JLabel("确认新密码"),
            roleLab = new JLabel("角色");
    private TextField userText = new TextField(15),
            ppwsText = new TextField(15),
            npwsText = new TextField(15),
            npwsText1 = new TextField(15),
            roleText = new TextField(15);
    private JButton modBut = new JButton("修改"),
            backBut = new JButton("返回");
    private User user;
    private ObjectOutputStream oos = null;

    public SelfFrame(User user) {
        this.user = user;
        oos = Client.getOutStream();
        showGUI(user);
    }

    public SelfFrame(User user, ObjectOutputStream oos) {
        this.user = user;
        this.oos = oos;
        showGUI(user);
    }

    private void showGUI(User user) {
        URL path = getClass().getClassLoader().getResource("resources/pictrue/selfMod.jpg");
        ImageIcon icon = new ImageIcon(path);
        JLabel img = new JLabel(icon);
        this.getLayeredPane().add(img, new Integer(Integer.MIN_VALUE));
        img.setBounds(0, 0, 300, 250);
        Container contain = this.getContentPane();
        ((JPanel) contain).setOpaque(false);
        setSize(300, 250);
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setLayout(new GridLayout(5, 2, 10, 10));
        panel.add(userLab);
        panel.add(userText);
        panel.add(ppwsLab);
        panel.add(ppwsText);
        panel.add(npwsLab);
        panel.add(npwsText);
        panel.add(npwsLab1);
        panel.add(npwsText1);
        panel.add(roleLab);
        ppwsText.setEchoChar('*');
        npwsText.setEchoChar('*');
        npwsText1.setEchoChar('*');
        userText.setText(user.getName());
        userText.setEnabled(false);
        roleText.setText(user.getRole());
        roleText.setEnabled(false);
        panel.add(roleText);
        panel.setOpaque(false);
        contain.add(panel);
        contain.add(modBut);
        contain.add(backBut);
        npwsText1.addActionListener(this);
        modBut.addActionListener(this);
        backBut.addActionListener(this);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == modBut || e.getSource() == npwsText1) {
            Object[] option = {"确定", "取消"};
            if (ppwsText.getText().equals(npwsText.getText())) {
                JOptionPane.showMessageDialog(new JButton("确定"), "和原密码一致！");
            } else if (!npwsText.getText().equals(npwsText1.getText())) {
                JOptionPane.showMessageDialog(new JButton("确定"), "确认密码错误！");
            } else if (JOptionPane.showOptionDialog(null, "确认要修改密码吗？",
                    "提示！", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, option, option[1]) == 0) {
                sendData("Self_Mod");
                sendData(ppwsText.getText());
                sendData(npwsText.getText());
            }
        } else if (e.getSource() == backBut) {
            this.dispose();
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
