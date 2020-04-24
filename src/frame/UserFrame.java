package frame;

import data.DataProcessing;
import domain.User;
import port.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * �û����洰��
 */
public class UserFrame extends JFrame implements ActionListener {
    private static final long serialVersionUID = 5364838666837636940L;
    private JPanel upPanel = new JPanel(),
            addPanel = new JPanel(),
            modPanel = new JPanel(),
            delPanel = new JPanel(),
            downPanel = new JPanel();
    private JButton addBut = new JButton("���"),
            modBut = new JButton("�޸�"),
            delBut = new JButton("ɾ��"),
            backBut = new JButton("����");
    private JLabel userLabel = new JLabel("�û���"),
            pwsLabel = new JLabel("����"),
            roleLabel = new JLabel("��ɫ");
    private JTextField userText = new JTextField(),
            pwsText = new JTextField();
    private Choice userList = new Choice(),
            roleList = new Choice();

    private List list = new List(6, true);

    private JButton addUser = new JButton("�����û�"),
            modUser = new JButton("�޸��û�"),
            delUser = new JButton("ɾ���û�");

    java.util.List<String> delName = new ArrayList<>();

    private User user;
    private ObjectOutputStream oos = null;

    public UserFrame(User user) {
        super("�û��������");
        oos = Client.getOutStream();
        showGUI();
    }

    public UserFrame(User user, ObjectOutputStream oos) {
        super("�û��������");
        this.oos = oos;
        showGUI();
    }

    private void showGUI() {
        URL path = getClass().getClassLoader().getResource("resources/pictrue/user.jpg");
        ImageIcon icon = new ImageIcon(path);
        JLabel img = new JLabel(icon);
        this.getLayeredPane().add(img, new Integer(Integer.MIN_VALUE));
        img.setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight());
        Container contain = this.getContentPane();
        ((JPanel) contain).setOpaque(false);
        this.user = user;
        setSize(350, 300);
        setLayout(new BorderLayout());
        upPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        upPanel.add(addUser);
        upPanel.add(modUser);
        upPanel.add(delUser);
        addUser.setBackground(Color.CYAN);
        modUser.setBackground(Color.CYAN);
        delUser.setBackground(Color.CYAN);
        addUser.addActionListener(this);
        modUser.addActionListener(this);
        delUser.addActionListener(this);
        add(upPanel, BorderLayout.NORTH);

        addPanel.setBackground(Color.CYAN);
        modPanel.setBackground(Color.lightGray);
        delPanel.setBackground(Color.ORANGE);

        userLabel.setForeground(Color.WHITE);
        pwsLabel.setForeground(Color.WHITE);
        roleLabel.setForeground(Color.WHITE);

        roleList.add("administrator");
        roleList.add("operator");
        roleList.add("browser");

        downPanel.setLayout(new FlowLayout());

        upPanel.setOpaque(false);
        addPanel.setOpaque(false);
        modPanel.setOpaque(false);
        delPanel.setOpaque(false);
        downPanel.setOpaque(false);

        addBut.addActionListener(this);
        modBut.addActionListener(this);
        delBut.addActionListener(this);
        backBut.addActionListener(this);

        setVisible(true);
        setLocationRelativeTo(null);
    }

    /**
     * �����û��б�ÿ���޸��û���ʱ�����޸�
     */
    private void flushUser() {
        list.removeAll();
        try {
            java.util.List<User> users = DataProcessing.getAllUser();
            for (User user :
                    users) {
                delName.add(user.getName());
                list.add("                " + user.getName() + "                          " +
                        user.getPassword() + "                   " + user.getRole());
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * �����û����б�ÿ���޸��û���ʱ�����޸�
     */
    private void flushUserName() {
        userList.removeAll();
        try {
            java.util.List<User> users = DataProcessing.getAllUser();
            for (User user :
                    users) {
                userList.add(user.getName());
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addUser) {
            downPanel.setBackground(Color.CYAN);
            addPanel.setLayout(new GridLayout(3, 2, 60, 60));
            addPanel.add(userLabel);
            addPanel.add(userText);
            addPanel.add(pwsLabel);
            addPanel.add(pwsText);
            addPanel.add(roleLabel);
            addPanel.add(roleList);
            addPanel.setVisible(true);
            modPanel.setVisible(false);
            delPanel.setVisible(false);
            addBut.setVisible(true);
            modBut.setVisible(false);
            delBut.setVisible(false);
            add("Center", addPanel);
            downPanel.add(addBut);
            downPanel.add(backBut);
            add("South", downPanel);
            setVisible(true);
        } else if (e.getSource() == modUser) {
            flushUserName();
            downPanel.setBackground(Color.lightGray);
            modPanel.setLayout(new GridLayout(3, 2, 60, 60));
            modPanel.add(userLabel);
            modPanel.add(userList);
            modPanel.add(pwsLabel);
            modPanel.add(pwsText);
            modPanel.add(roleLabel);
            modPanel.add(roleList);
            addPanel.setVisible(false);
            modPanel.setVisible(true);
            delPanel.setVisible(false);
            addBut.setVisible(false);
            modBut.setVisible(true);
            delBut.setVisible(false);
            add("Center", modPanel);
            downPanel.add(modBut);
            downPanel.add(backBut);
            add("South", downPanel);
            setVisible(true);
        } else if (e.getSource() == delUser) {
            downPanel.setBackground(Color.ORANGE);
            addPanel.setVisible(false);
            modPanel.setVisible(false);
            delPanel.setVisible(true);
            addBut.setVisible(false);
            modBut.setVisible(false);
            delBut.setVisible(true);
            add("Center", delPanel);

            flushUser();
            delPanel.setLayout(new BorderLayout());
            JPanel p = new JPanel();
            p.setBackground(Color.ORANGE);
            p.setOpaque(false);
            p.setLayout(new FlowLayout(FlowLayout.LEFT, 50, 5));
            p.add(new JLabel("�û��� "));
            p.add(new JLabel(" ����"));
            p.add(new JLabel(" ��ɫ"));
            delPanel.add("North", p);
            delPanel.add("Center", list);
            downPanel.add(delBut);
            downPanel.add(backBut);
            add("South", downPanel);
            setVisible(true);
        } else if (e.getSource() == addBut) {
            Object[] option = {"ȷ��", "ȡ��"};
            if (JOptionPane.showOptionDialog(null, "ȷ��Ҫ����û���",
                    "��ʾ��", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, option, option[1]) == 0) {
                sendData("User_Add");
                sendData(userText.getText());
                sendData(pwsText.getText());
                sendData(roleList.getSelectedItem());
            }
        } else if (e.getSource() == modBut) {
            Object[] option = {"ȷ��", "ȡ��"};
            if (JOptionPane.showOptionDialog(null, "ȷ��Ҫ�޸��û���",
                    "��ʾ��", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, option, option[1]) == 0) {
                sendData("User_Mod");
                sendData(userList.getSelectedItem());
                sendData(pwsText.getText());
                sendData(roleList.getSelectedItem());
            }
        } else if (e.getSource() == delBut) {
            int[] delIndex = list.getSelectedIndexes();
            if (list.getSelectedIndexes().length == 0) {
                JOptionPane.showMessageDialog(new JButton("ȷ��"), "������ѡ��һ��");
            } else {
                Object[] option = {"ȷ��", "ȡ��"};
                if (JOptionPane.showOptionDialog(null, "ȷ��Ҫɾ���û���",
                        "��ʾ��", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                        null, option, option[1]) == 0) {
                    int s;
                    sendData("User_Del");
                    for (s = 0; s < delIndex.length - 1; s++) {
                        sendData(delName.get(delIndex[s]) + "Del_Name");
                    }
                    sendData(delName.get(delIndex[s]) + "Del_Name_Last");
                } else {

                }
            }

        } else if (e.getSource() == backBut) {
            this.dispose();
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