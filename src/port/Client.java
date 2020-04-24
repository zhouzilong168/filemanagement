package port;

import domain.User;
import frame.LoginFrame;
import frame.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * 客户端
 */
public class Client extends JFrame implements Runnable, Serializable {

    private JTextField enterField; // enters information from user
    private JTextArea displayArea; // display information to user
    private String chatServer; // host server for this application
    private int port; // port server for this application
    private Socket client; // socket to communicate with server
    private LoginFrame loginFrame; // loginFrame
    private static final int COUNT_INDEX = 10;
    private static final HashMap<String, String> map = new HashMap<>();

    static {
        map.put("Error_user_or_pws", "用户名或密码错误！");
        map.put("Threes times Error_User_or_Password", "三次用户名或者密码错误,系统终止！");
        map.put("Self_Mod_successful", "修改成功！");
        map.put("Error_npws", "原密码输入错误！");
        map.put("Self_Mod_unsuccessful", "修改失败！");
        map.put("File_ID_Exist", "该档案号已存在!");
        map.put("File_Up_unsuccessful", "上传失败");
        map.put("File_Up_successful", "上传成功");
        map.put("User_Add_successful", "添加成功");
        map.put("User_Add_unsuccessful", "添加失败");
        map.put("User_Add_Name_Same", "该用户名已存在，添加失败");
        map.put("User_Mod_successful", "修改成功");
        map.put("User_Mod_unsuccessful", "修改失败");
    }

    /*
    解决多线程使用过程中ObjectOutputStream/ObjectInputStream冲突问题解决
     */
    private static ThreadLocal<ObjectOutputStream> outTl = new ThreadLocal<>();
    private static ThreadLocal<ObjectInputStream> inTl = new ThreadLocal<>();

    /**
     * 初始化客户端参数
     *
     * @param host
     * @param port
     */
    public Client(String host, int port) {
        super("Client");
        chatServer = host;
        this.port = port;
    }

    /**
     * 建立GUI及运行客户端线程
     */
    public void run() {
        showGUI();
        runClient();
    }

    /**
     * 连接到服务端，初始化输入输出流，执行客户端
     */
    private void runClient() {
        try {
            connectToServer();
            getStreams();
            processConnection();
        } catch (UnknownHostException | EOFException e) {
            displayMessage(e.getMessage());
        } finally {
            // 关闭连接，释放资源
            closeConnection();
        }
    }

    /**
     * 连接到服务器
     *
     * @throws UnknownHostException 作为判断无法连接异常抛出
     */
    private void connectToServer() throws UnknownHostException {
        displayMessage("Attempting connection\n");

        try {
            // 连接到服务器
            client = new Socket(InetAddress.getByName(chatServer), port);
        } catch (IOException e) {
            throw new UnknownHostException("can't connect to Server");
        }

        // display connection information
        displayMessage("Connected to: " + client.getInetAddress().getHostName());
    }

    /**
     * 初始化流，并绑定到对应线程的ThreadLocal中
     */
    private void getStreams() {
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try {
            oos = new ObjectOutputStream(client.getOutputStream());
            ois = new ObjectInputStream(client.getInputStream());
        } catch (IOException e) {
            System.out.println("oos/ois Stream initial default");
        }

        // 添加当前流到线程本地变量ThreadLocal
        outTl.set(oos);
        inTl.set(ois);
/*
        System.out.println("Thread:(getIO) "+Thread.currentThread().getName());
        System.out.print("outTl: "+ outTl.get());
        System.out.println("\tinTl: "+ inTl.get());
*/
        loginFrame = new LoginFrame();
        loginFrame.setVisible(true);
        displayMessage("Got I/O streams");
    }

    /**
     * 在map中搜索message
     *
     * @param message
     * @return
     */
    private String searchMessage(String message) {
        String tip = null;
        for (Map.Entry<String, String> entry :
                map.entrySet()) {
            if (message.endsWith(entry.getKey())) {
                tip = entry.getValue();
                break;
            }
        }
        return tip;
    }

    /**
     * 执行连接服务
     *
     * @throws EOFException 作为服务端终止异常抛出
     */
    private void processConnection() throws EOFException {
        setTextFieldEditable(true);
        String message = null;
        String tip = null;
        do {
            try {
                message = (String) getData();
                if (message == null) {
                    continue;
                }
                displayMessage(message);

                if ((tip = searchMessage(message)) != null) {

                } else if (message.endsWith("File_Down_prepare")) {
                    getFile(); // 接受服务端传来的文件
                    String filename = message.substring(COUNT_INDEX, message.indexOf("File_Down"));
                    tip = filename + "下载成功";
                } else if (message.endsWith("User_Del_successful")) {
                    String name = message.substring(COUNT_INDEX, message.indexOf("User"));
                    tip = name + "删除成功";
                } else if (message.endsWith("User_Del_unsuccessful")) {
                    String name = message.substring(COUNT_INDEX, message.indexOf("User"));
                    tip = name + "删除失败";
                } else if (message.equals("SERVER>>> TERMINATE")) {
                    throw new EOFException("Server Terminate");
                } else if (message.endsWith("Logined successful")) {
                    User user = (User) getData();
                    setTitle(user.getName() + "(" + user.getRole() + ")"); // 设置Client客户端界面标题
                    new MainFrame(user, Client.getOutStream()); // 打开主界面
                    loginFrame.setVisible(false);
                }
                // 提示框
                if (tip != null && !tip.trim().equals("")) {
                    showDialog(tip);
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("socket is error whiling read or send");
                break;
            }
        } while (true);
    }

    /**
     * 弹出提示框
     *
     * @param tip
     */
    private void showDialog(String tip) {
        JOptionPane.showMessageDialog(new JButton("确定"), tip);
    }

    /**
     * 关闭客户端连接，清理释放资源
     */
    private void closeConnection() {
        displayMessage("Closing connection");
        setTextFieldEditable(false);

        try {
            outTl.remove(); // 解绑
            inTl.remove();
            client.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    /**
     * 发送文件，利用指定流，实现同步
     *
     * @param name
     * @param oos
     */
    public static void sendFile(String name, ObjectOutputStream oos) {
        FileInputStream fis = null;
        //System.out.println("now is sending: " + Thread.currentThread().getName() + " | oos: " + oos);
        try {
            File file = new File(name);
            fis = new FileInputStream(file);
            //文件名和长度
            oos.writeUTF(file.getName());
            oos.flush();
            oos.writeLong(file.length());
            oos.flush();
            //传输文件
            byte[] sendBytes = new byte[1024];
            int length = 0;
            while ((length = fis.read(sendBytes, 0, sendBytes.length)) > 0) {
                oos.write(sendBytes, 0, length);
                oos.flush();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * 从服务端接受文件ile_Down
     */
    private void getFile() {
        ObjectInputStream ois = inTl.get();
        //System.out.println("now is getting: " + Thread.currentThread().getName() + " | ois: " + ois);
        FileOutputStream fos = null;
        try {
            //文件名和长度
            String fileName = ois.readUTF();
            long fileLength = ois.readLong();
            URL resource = getClass().getClassLoader().getResource("resources/clientfiles/" + fileName);
            fos = new FileOutputStream(new File(resource.toURI()));
            BufferedOutputStream bfos = new BufferedOutputStream(fos);
            byte[] sendBytes = new byte[1024];
            int transLen = 0;
            displayMessage("\n----开始接收文件<" + fileName + ">----\n-------文件大小为<" + fileLength + ">-------\n");
            Timestamp past = new Timestamp(System.currentTimeMillis());
            int read = 0;
            read = ois.read(sendBytes);
            transLen += read;
            enterField.setForeground(Color.BLACK);
            while (true) {
                if (transLen == fileLength) break;
                read = ois.read(sendBytes);
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 向服务端发送数据
     *
     * @param obj
     */
    public void sendData(Object obj) {
        ObjectOutputStream oos = outTl.get();
        if (oos == null) {
            System.out.println("ObjectOutputStream is null");
            return;
        }
        try {
            if (obj instanceof String) {
                oos.writeObject("CLIENT>>> " + obj);
            } else {
                oos.writeObject(obj);
            }
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            //displayArea.append("\nError writing object");

        }
    }

    /**
     * 从服务端获得数据
     *
     * @return
     */
    private Object getData() throws IOException, ClassNotFoundException {
        ObjectInputStream ois = inTl.get();
        if (ois == null) {
            System.out.println("ObjectInputStream is null");
            return null;
        }
        Object message = null;
        message = ois.readObject();
        return message;
    }

    /**
     * 展示数据
     *
     * @param messageToDisplay
     */
    private void displayMessage(final String messageToDisplay) {
        SwingUtilities.invokeLater(() -> displayArea.append(messageToDisplay + "\n"));
    }

    /**
     * 设置文本域可见
     *
     * @param editable
     */
    private void setTextFieldEditable(final boolean editable) {
        SwingUtilities.invokeLater(() -> enterField.setEditable(editable));
    }

    /**
     * 自定义进度条显示
     *
     * @param now
     * @param all
     */
    private void processBar(int now, long all) {
        double pros = (double) now / all;
        enterField.setSize((int) (pros * 300), 18);
        if (String.valueOf(pros).length() > 3) {
            enterField.setText("  " + String.valueOf(pros * 100).substring(0, 4) + " %");
        } else {
            enterField.setText("  " + pros + " %");
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

    /**
     * GUI展示
     */
    private void showGUI() {
        URL path = getClass().getClassLoader().getResource("resources/pictrue/client.jpg");
        ImageIcon icon = new ImageIcon(path);
        JLabel img = new JLabel(icon);
        this.getLayeredPane().add(img, new Integer(Integer.MIN_VALUE));
        img.setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight());
        Container contain = this.getContentPane();
        ((JPanel) contain).setOpaque(false);

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

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    /**
     * 获取输出流，当前线程
     *
     * @return
     */
    public static ObjectOutputStream getOutStream() {
        return outTl.get();
    }

    /**
     * 获取输入流，当前线程
     *
     * @return
     */
    public static ObjectInputStream getInStream() {
        return inTl.get();
    }
}