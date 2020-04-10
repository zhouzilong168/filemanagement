package port;

import domain.User;
import frame.loginFrame;
import frame.mainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.Timestamp;

/**
 * 客户端
 */
public class Client extends JFrame implements Runnable {
    private static final long serialVersionUID = 4504109369720431939L;
    private static JTextField enterField; // enters information from user
    private static JTextArea displayArea; // display information to user
    private String message = ""; // message from server
    private String chatServer; // host server for this application
    private Socket client; // socket to communicate with server

    public static ObjectOutputStream dos; // output stream to server
    public static ObjectInputStream dis; // input stream from server
    private static FileInputStream fis;
    private static FileOutputStream fos;

    // initialize chatServer and set up GUI
    public Client(String host) {
        super("Client");
        chatServer = host;
    }

    // connect to server and process messages from server
    public void runClient() {
        try {
            connectToServer();
            getStreams();
            processConnection();
        } catch (EOFException eofException) {
            displayMessage("\nClient terminated connection");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    // connect to server
    private void connectToServer() throws IOException {
        displayMessage("Attempting connection\n");

        // create Socket to make connection to server
        client = new Socket(InetAddress.getByName(chatServer), 12345);

        // display connection information
        displayMessage("Connected to: " +
                client.getInetAddress().getHostName());
    }

    // get streams to send and receive data
    private void getStreams() throws IOException {
        // set up output stream for objects
        dos = new ObjectOutputStream(client.getOutputStream());
        dos.flush();
        // set up input stream for objects
        dis = new ObjectInputStream(client.getInputStream());

        displayMessage("\nGot I/O streams\n");
    }

    // process connection with server
    private void processConnection() throws IOException {
        setTextFieldEditable(true);
        do {
            try {
                message = (String) dis.readObject();
                displayMessage("\n" + message);

                if (message.endsWith("Logined successful")) {
                    User user = (User) dis.readObject();
                    new mainFrame(user);
                } else if (message.endsWith("Error_user_or_pws")) {
                    JOptionPane.showMessageDialog(new JButton("确定"), "用户名或密码错误！");
                } else if (message.endsWith("Threes times Error_User_or_Password")) {
                    JOptionPane.showMessageDialog(new JButton("确定"), "三次用户名或者密码错误,系统终止！");
                    break;
                }//登入

                if (message.endsWith("Self_Mod_successful")) {
                    JOptionPane.showMessageDialog(new JButton("确定"), "修改成功！");
                } else if (message.endsWith("Error_npws")) {
                    JOptionPane.showMessageDialog(new JButton("确定"), "原密码输入错误！");
                } else if (message.endsWith("Self_Mod_unsuccessful")) {
                    JOptionPane.showMessageDialog(new JButton("确定"), "修改失败！");
                }//个人信息修改

                if (message.endsWith("File_ID_Exist")) {
                    JOptionPane.showMessageDialog(new JButton("确定"), "该档案号已存在！");
                } else if (message.endsWith("File_Up_unsuccessful")) {
                    JOptionPane.showMessageDialog(new JButton("确定"), "上传失败");
                } else if (message.endsWith("File_Up_successful")) {
                    JOptionPane.showMessageDialog(new JButton("确定"), "上传成功");
                }//文件上传

                if (message.endsWith("File_Down_prepare")) {
                    getFile();
                    String filename = message.substring(10, message.indexOf("File_Down"));
                    JOptionPane.showMessageDialog(new JButton("确定"), filename + "下载成功");
                }//下载

                if (message.endsWith("User_Add_successful")) {
                    JOptionPane.showMessageDialog(new JButton("确定"), "添加成功");
                } else if (message.endsWith("User_Add_unsuccessful")) {
                    JOptionPane.showMessageDialog(new JButton("确定"), "添加失败");
                } else if (message.endsWith("User_Add_Name_Same")) {
                    JOptionPane.showMessageDialog(new JButton("确定"), "该用户名已存在，添加失败");
                }//用户添加

                if (message.endsWith("User_Mod_successful")) {
                    JOptionPane.showMessageDialog(new JButton("确定"), "修改成功");
                } else if (message.endsWith("User_Mod_unsuccessful")) {
                    JOptionPane.showMessageDialog(new JButton("确定"), "修改失败");
                }//用户修改

                if (message.endsWith("User_Del_successful")) {
                    String name = message.substring(10, message.indexOf("User"));
                    JOptionPane.showMessageDialog(new JButton("确定"), name + "删除成功");
                } else if (message.endsWith("User_Del_unsuccessful")) {
                    String name = message.substring(10, message.indexOf("User"));
                    JOptionPane.showMessageDialog(new JButton("确定"), name + "删除失败");
                }//用户删除

            } // end try
            catch (ClassNotFoundException classNotFoundException) {
                displayMessage("\nUnknown object type received");
            }

        } while (!message.equals("SERVER>>> TERMINATE"));
    }

    // close streams and socket
    private void closeConnection() {
        displayMessage("\nClosing connection");
        setTextFieldEditable(false);

        try {
            if (dos != null) dos.close();
            if (dis != null) dis.close();
            if (fos != null) fos.close();
            if (fis != null) fis.close();
            client.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    //send file to server
    public static void sendFile(String name) {
        try {
            File file = new File(name);
            fis = new FileInputStream(file);
            //文件名和长度
            System.out.println(file.getName());
            dos.writeUTF(file.getName());
            dos.flush();
            dos.writeLong(file.length());
            dos.flush();
            //传输文件
            byte[] sendBytes = new byte[1024];
            int length = 0;
            while ((length = fis.read(sendBytes, 0, sendBytes.length)) > 0) {
                dos.write(sendBytes, 0, length);
                dos.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //get file from server
    private void getFile() throws IOException {
        try {
            //文件名和长度
            String fileName = dis.readUTF();
            long fileLength = dis.readLong();
            fos = new FileOutputStream(new File("D:\\OOP\\downloadfile\\" + fileName));
            BufferedOutputStream bfos = new BufferedOutputStream(fos);
            byte[] sendBytes = new byte[1024];
            int transLen = 0;
            displayMessage("\n----开始接收文件<" + fileName + ">----\n-------文件大小为<" + fileLength + ">-------\n");
            Timestamp past = new Timestamp(System.currentTimeMillis());
            String temp = null;
            int read = 0;
            read = dis.read(sendBytes);
            transLen += read;
            temp = String.valueOf(100 * (double) transLen / fileLength).substring(0, 4);
            enterField.setForeground(Color.BLACK);
            while (true) {
                if (transLen == fileLength) break;
                read = dis.read(sendBytes);
                transLen += read;
                processBar(transLen, fileLength);
                bfos.write(sendBytes, 0, read);
                bfos.flush();
            }

            displayMessage("----接收文件<" + fileName + ">成功-------\n");
            enterField.setBackground(Color.LIGHT_GRAY);
            enterField.setForeground(Color.RED);
            enterField.setFont(new Font("TimesRoman", Font.BOLD, 10));
            enterField.setText("接收成功，进度100.00%");
            Timestamp now = new Timestamp(System.currentTimeMillis());
            sendData("cost " + now.compareTo(past) + " ms");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) fos.close();
        }
    }

    // send message to server
    public static void sendData(String message) {
        try {
            dos.writeObject("CLIENT>>> " + message);
            dos.flush();
        } catch (IOException ioException) {
            displayArea.append("\nError writing object");
        }
    }

    // manipulates displayArea in the event-dispatch thread
    public static void displayMessage(final String messageToDisplay) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        displayArea.append(messageToDisplay);
                    }
                }
        );
    }

    // manipulates enterField in the event-dispatch thread
    private void setTextFieldEditable(final boolean editable) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        enterField.setEditable(editable);
                    }
                }
        );
    }

    public void run() {
        ImageIcon icon = new ImageIcon("D:\\OOP\\pictrue\\client.jpg");
        JLabel img = new JLabel(icon);
        this.getLayeredPane().add(img, new Integer(Integer.MIN_VALUE));
        img.setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight());
        Container contain = this.getContentPane();
        ((JPanel) contain).setOpaque(false);

        loginFrame loginframe = new loginFrame();
        loginframe.setVisible(true);
        enterField = new JTextField();
        enterField.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        sendData(event.getActionCommand());
                        enterField.setText("");
                    }
                }
        );

        add(enterField, BorderLayout.NORTH);
        displayArea = new JTextArea();
        displayArea.setFont(new Font("TimesRoman", Font.ROMAN_BASELINE, 15));
        displayArea.setForeground(Color.BLACK);
        enterField.setOpaque(false);
        displayArea.setOpaque(false);

        JScrollPane jsp = new JScrollPane(displayArea);
        jsp.setOpaque(false);
        jsp.getViewport().setOpaque(false);
        add(jsp, BorderLayout.CENTER);

        setSize(300, 600);
        setLocation(0, 30);
        setVisible(true);

        Client application;
        application = new Client("127.0.0.1");

        application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        application.runClient();
    }

    // 自定义进度条显示
    void processBar(int now, long all) {
        double pros = (double) now / all;
        enterField.setSize((int) (pros * 300), 18);
        if (String.valueOf(pros).length() > 3) {
            enterField.setText("  " + String.valueOf(pros * 100).substring(0, 4) + " %");
        } else {
            enterField.setText("  " + String.valueOf(pros) + " %");
        }
        switch ((int) (pros * 10)) {
            case 0:
                enterField.setBackground(Color.WHITE);
                break;
            case 1:
                enterField.setBackground(Color.lightGray);
                break;
            case 2:
                enterField.setBackground(Color.GRAY);
                break;
            case 3:
                enterField.setBackground(Color.PINK);
                break;
            case 4:
                enterField.setBackground(Color.YELLOW);
                break;
            case 5:
                enterField.setBackground(Color.orange);
                break;
            case 6:
                enterField.setBackground(Color.GREEN);
                break;
            case 7:
                enterField.setBackground(Color.BLUE);
                break;
            case 8:
                enterField.setBackground(Color.darkGray);
                break;
            case 9:
                enterField.setBackground(Color.BLACK);
                break;
            case 10:
                enterField.setBackground(Color.RED);
                break;
        }
    }

    public static void main(String args[]) {
        Client application;

        // if no command line args
        if (args.length == 0)
            application = new Client("127.0.0.1");
        else
            application = new Client(args[0]);

        application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        application.runClient();
    }
}