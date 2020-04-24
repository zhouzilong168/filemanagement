package frame;

import data.DataProcessing;
import domain.Doc;
import domain.User;
import port.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;

/**
 * 文件操作窗体
 */
public class FileFrame extends JFrame implements ActionListener {
    private JPanel panel0 = new JPanel(),
            panel1 = new JPanel(),
            panel2 = new JPanel(),
            panel11 = new JPanel(),
            panel22 = new JPanel(),
            panel = new JPanel();
    private JPanel panel3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 40, 1));
    private JButton downFileBut = new JButton("文件下载"),
            upFileBut = new JButton("文件上传"),
            downBut = new JButton("下载"),
            backBut = new JButton("返回");
    private JLabel idLab = new JLabel("档案号"),
            creLab = new JLabel("创建者"),
            timeLab = new JLabel("     时间                  "),
            nameLab = new JLabel("文件名"),
            desLab = new JLabel("概述");

    private JLabel lab11 = new JLabel("             档案号"),
            lab22 = new JLabel("             档案描述"),
            lab33 = new JLabel("             档案文件名");
    private JButton openBut = new JButton("打开"),
            upBut = new JButton("上传"),
            canBut = new JButton("取消");
    private JTextField idText = new JTextField(15),
            nameText = new JTextField(15);
    private JTextArea desText = new JTextArea(3, 15);

    private List fileList = new List(6, true);
    private java.util.List<String> downName = new ArrayList<>();
    private java.util.List<String> downID = new ArrayList<>();
    private JFrame fr = new JFrame();

    private JFrame f = new JFrame();

    private String dictionary;

    private User user;

    private ObjectOutputStream oos = null;

    public FileFrame(User user) {
        super("文件管理界面[" + user.getName() + "]");
        this.user = user;
        oos = Client.getOutStream();
        showGUI();
    }

    public FileFrame(User user, ObjectOutputStream oos) {
        super("文件管理界面[" + user.getName() + "]");
        this.user = user;
        this.oos = oos;
        showGUI();
    }

    public void flushFile() {
        fileList.removeAll();
        try {
            java.util.List<Doc> docs = DataProcessing.getAllDocs();
            Collections.sort(docs, (t1, t2) -> {
                int id1 = Integer.valueOf(t1.getID()), id2 = Integer.valueOf(t2.getID());
                if (id1 < id2) {
                    return -1;
                } else {
                    return 1;
                }
            });
            for (Doc doc :
                    docs) {
                downName.add(doc.getFilename());
                fileList.add("               " + doc.getID() + "                    " +
                        doc.getCreator() + "           " +
                        doc.getTimestamp() + "                " + doc.getFilename() +
                        "               " + doc.getDescription());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == downFileBut) {
            flushFile();
            add("Center", panel1);
            add("South", panel2);
            panel11.setVisible(false);
            panel22.setVisible(false);
            panel1.setVisible(true);
            panel2.setVisible(true);
            setVisible(true);
        } else if (e.getSource() == upFileBut) {
            add("Center", panel11);
            add("South", panel22);
            panel1.setVisible(false);
            panel2.setVisible(false);
            panel11.setVisible(true);
            panel22.setVisible(true);
            setVisible(true);
        } else if (e.getSource() == downBut) {
            int[] downIndex = fileList.getSelectedIndexes();
            if (downIndex.length == 0) {
                JOptionPane.showMessageDialog(new JButton("确定"), "请至少选择一项");
            } else {
                Object[] option = {"确定", "取消"};
                if (JOptionPane.showOptionDialog(null, "确认要下载文件吗？",
                        "提示！", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                        null, option, option[1]) == 0) {
                    sendData("File_Down");
                    int i;
                    for (i = 0; i < downIndex.length - 1; i++) {
                        sendData(downName.get(downIndex[i]) + "File_Name");
                    }
                    sendData(downName.get(downIndex[i]) + "File_Name_Last");
                }
            }
        } else if (e.getSource() == backBut) {
            this.dispose();
        } else if (e.getSource() == openBut) {
            FileDialog fd = new FileDialog(f, "Open file", FileDialog.LOAD);
            //noinspection deprecation
            fd.show();
            nameText.setText(fd.getFile());
            dictionary = fd.getDirectory() + fd.getFile();
        } else if (e.getSource() == upBut) {
            try {
                Object[] option = {"确定", "取消"};
                if (JOptionPane.showOptionDialog(null, "确认要上传文件吗？",
                        "提示！", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                        null, option, option[1]) == 0) {
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    sendData("File_Up");
                    oos.writeObject(new Doc(idText.getText(), user.getName(), timestamp,
                            desText.getText(), nameText.getText()));
                    oos.flush();
                    new Thread(() -> {
                        Client.sendFile(dictionary, oos);
                    }).start();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        } else if (e.getSource() == canBut) {
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

    private void showGUI() {
        URL path = getClass().getClassLoader().getResource("resources/pictrue/file.jpg");
        ImageIcon icon = new ImageIcon(path);
        JLabel img = new JLabel(icon);
        this.getLayeredPane().add(img, new Integer(Integer.MIN_VALUE));
        img.setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight());
        Container contain = this.getContentPane();
        ((JPanel) contain).setOpaque(false);
        setSize(600, 300);
        setLayout(new BorderLayout());
        panel11.setBackground(Color.PINK);
        panel22.setBackground(Color.PINK);
        panel2.setBackground(Color.LIGHT_GRAY);
        panel0.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel0.add(downFileBut);
        panel0.add(upFileBut);
        downFileBut.setBackground(Color.CYAN);
        upFileBut.setBackground(Color.CYAN);
        downFileBut.setForeground(Color.BLUE);
        upFileBut.setForeground(Color.BLUE);
        upFileBut.setFocusPainted(false);
        downFileBut.setFocusPainted(false);
        if (user.getRole().equals("administrator") || user.getRole().equals("browser")) {
            upFileBut.setEnabled(false);
        } else {
            upFileBut.setEnabled(true);
        }
        add("North", panel0);

        panel1.setLayout(new BorderLayout());
        flushFile();
        panel1.add("Center", fileList);
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new FlowLayout(FlowLayout.LEFT, 50, 10));
        p.add(idLab);
        p.add(creLab);
        p.add(timeLab);
        p.add(nameLab);
        p.add(desLab);
        panel1.add("North", p);

        panel2.setLayout(new FlowLayout(FlowLayout.CENTER, 2, 0));
        panel2.add(downBut);
        panel2.add(backBut);
        downFileBut.addActionListener(this);
        upFileBut.addActionListener(this);
        downBut.addActionListener(this);
        backBut.addActionListener(this);
        panel11.setLayout(new GridLayout(3, 3, 100, 60));
        panel11.add(lab11);
        panel11.add(idText);
        panel11.add(new JLabel());
        panel11.add(lab22);
        panel11.add(desText);
        panel11.add(new JLabel());
        panel11.add(lab33);
        panel11.add(nameText);
        panel11.add(openBut);
        panel22.setLayout(new FlowLayout(FlowLayout.CENTER, 2, 0));
        panel22.add(upBut);
        panel22.add(canBut);

        panel0.setOpaque(false);
        panel1.setOpaque(false);
        panel2.setOpaque(false);
        panel11.setOpaque(false);
        panel22.setOpaque(false);
        panel.setOpaque(false);
        panel3.setOpaque(false);

        openBut.addActionListener(this);
        upBut.addActionListener(this);
        canBut.addActionListener(this);
        setLocationRelativeTo(null);

        setVisible(true);
    }
}