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
 * 主界面窗体
 */
public class MainFrame extends JFrame implements ActionListener {

    private static final long serialVersionUID = -5590758903420072106L;
    private JLabel label = new JLabel();
    private JMenuBar menuBar = new JMenuBar();
    ;
    private JMenu menuUser = new JMenu("用户管理"),
            menuFile = new JMenu("档案管理"),
            menuSelf = new JMenu("个人信息管理"),
            menuSys = new JMenu("关于");
    private JMenuItem addUser = new JMenuItem("新增用户"),
            modUser = new JMenuItem("修改用户"),
            delUser = new JMenuItem("删除用户"),
            upFile = new JMenuItem("上传文件"),
            downFile = new JMenuItem("下载文件"),
            modMessage = new JMenuItem("修改个人信息"),
            sysExit = new JMenuItem("退出系统"),
            sysBack = new JMenuItem("切换角色");
    private User temp;
    private ObjectOutputStream oos = null;

    public MainFrame(User temp) {
        super("用户界面[" + temp.getName() + "]");
        this.temp = temp;
        oos = Client.getOutStream();
        showGUI();
    }

    public MainFrame(User temp, ObjectOutputStream oos) {
        super("用户界面[" + temp.getName() + "]");
        this.temp = temp;
        this.oos = oos;
        showGUI();
    }

    private void showGUI() {
        setLayout(new BorderLayout());
        URL path = getClass().getClassLoader().getResource("resources/pictrue/TMFriend.jpg");
        ImageIcon image = new ImageIcon(path);
        label.setIcon(image);
        add("Center", label);
        setSize(image.getIconWidth(), image.getIconHeight());
        menuUser.add(modUser);
        menuUser.add(addUser);
        menuUser.add(delUser);
        menuFile.add(upFile);
        menuFile.add(downFile);
        menuSelf.add(modMessage);
        menuSys.add(sysExit);
        menuSys.add(sysBack);
        menuBar.add(menuUser);
        menuBar.add(menuFile);
        menuBar.add(menuSelf);
        menuBar.add(menuSys);
        setJMenuBar(menuBar);
        modUser.addActionListener(this);
        addUser.addActionListener(this);
        delUser.addActionListener(this);
        upFile.addActionListener(this);
        downFile.addActionListener(this);
        modMessage.addActionListener(this);
        sysExit.addActionListener(this);
        sysBack.addActionListener(this);
        setLocationRelativeTo(null);

        setVisible(true);
        if (temp.getRole().equals("administrator")) {
            upFile.setEnabled(false);
        } else if (temp.getRole().equals("operator")) {
            menuUser.setEnabled(false);
        } else if (temp.getRole().equals("browser")) {
            menuUser.setEnabled(false);
            upFile.setEnabled(false);
        } else {
            System.exit(0);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sysExit) {
            sendData("TERMINATE");
            System.exit(0);
        } else if (e.getSource() == sysBack) {
            this.dispose();
            new LoginFrame(oos);
        } else if (e.getSource() == modUser) {
            new UserFrame(temp, oos);
        } else if (e.getSource() == addUser) {
            new UserFrame(temp, oos);
        } else if (e.getSource() == delUser) {
            new UserFrame(temp, oos);
        } else if (e.getSource() == upFile) {
            new FileFrame(temp, oos);
        } else if (e.getSource() == downFile) {
            new FileFrame(temp, oos);
        } else if (e.getSource() == modMessage) {
            new SelfFrame(temp, oos);
        } else {

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
