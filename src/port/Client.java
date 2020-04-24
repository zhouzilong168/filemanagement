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
 * �ͻ���
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
        map.put("Error_user_or_pws", "�û������������");
        map.put("Threes times Error_User_or_Password", "�����û��������������,ϵͳ��ֹ��");
        map.put("Self_Mod_successful", "�޸ĳɹ���");
        map.put("Error_npws", "ԭ�����������");
        map.put("Self_Mod_unsuccessful", "�޸�ʧ�ܣ�");
        map.put("File_ID_Exist", "�õ������Ѵ���!");
        map.put("File_Up_unsuccessful", "�ϴ�ʧ��");
        map.put("File_Up_successful", "�ϴ��ɹ�");
        map.put("User_Add_successful", "��ӳɹ�");
        map.put("User_Add_unsuccessful", "���ʧ��");
        map.put("User_Add_Name_Same", "���û����Ѵ��ڣ����ʧ��");
        map.put("User_Mod_successful", "�޸ĳɹ�");
        map.put("User_Mod_unsuccessful", "�޸�ʧ��");
    }

    /*
    ������߳�ʹ�ù�����ObjectOutputStream/ObjectInputStream��ͻ������
     */
    private static ThreadLocal<ObjectOutputStream> outTl = new ThreadLocal<>();
    private static ThreadLocal<ObjectInputStream> inTl = new ThreadLocal<>();

    /**
     * ��ʼ���ͻ��˲���
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
     * ����GUI�����пͻ����߳�
     */
    public void run() {
        showGUI();
        runClient();
    }

    /**
     * ���ӵ�����ˣ���ʼ�������������ִ�пͻ���
     */
    private void runClient() {
        try {
            connectToServer();
            getStreams();
            processConnection();
        } catch (UnknownHostException | EOFException e) {
            displayMessage(e.getMessage());
        } finally {
            // �ر����ӣ��ͷ���Դ
            closeConnection();
        }
    }

    /**
     * ���ӵ�������
     *
     * @throws UnknownHostException ��Ϊ�ж��޷������쳣�׳�
     */
    private void connectToServer() throws UnknownHostException {
        displayMessage("Attempting connection\n");

        try {
            // ���ӵ�������
            client = new Socket(InetAddress.getByName(chatServer), port);
        } catch (IOException e) {
            throw new UnknownHostException("can't connect to Server");
        }

        // display connection information
        displayMessage("Connected to: " + client.getInetAddress().getHostName());
    }

    /**
     * ��ʼ���������󶨵���Ӧ�̵߳�ThreadLocal��
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

        // ��ӵ�ǰ�����̱߳��ر���ThreadLocal
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
     * ��map������message
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
     * ִ�����ӷ���
     *
     * @throws EOFException ��Ϊ�������ֹ�쳣�׳�
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
                    getFile(); // ���ܷ���˴������ļ�
                    String filename = message.substring(COUNT_INDEX, message.indexOf("File_Down"));
                    tip = filename + "���سɹ�";
                } else if (message.endsWith("User_Del_successful")) {
                    String name = message.substring(COUNT_INDEX, message.indexOf("User"));
                    tip = name + "ɾ���ɹ�";
                } else if (message.endsWith("User_Del_unsuccessful")) {
                    String name = message.substring(COUNT_INDEX, message.indexOf("User"));
                    tip = name + "ɾ��ʧ��";
                } else if (message.equals("SERVER>>> TERMINATE")) {
                    throw new EOFException("Server Terminate");
                } else if (message.endsWith("Logined successful")) {
                    User user = (User) getData();
                    setTitle(user.getName() + "(" + user.getRole() + ")"); // ����Client�ͻ��˽������
                    new MainFrame(user, Client.getOutStream()); // ��������
                    loginFrame.setVisible(false);
                }
                // ��ʾ��
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
     * ������ʾ��
     *
     * @param tip
     */
    private void showDialog(String tip) {
        JOptionPane.showMessageDialog(new JButton("ȷ��"), tip);
    }

    /**
     * �رտͻ������ӣ������ͷ���Դ
     */
    private void closeConnection() {
        displayMessage("Closing connection");
        setTextFieldEditable(false);

        try {
            outTl.remove(); // ���
            inTl.remove();
            client.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    /**
     * �����ļ�������ָ������ʵ��ͬ��
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
            //�ļ����ͳ���
            oos.writeUTF(file.getName());
            oos.flush();
            oos.writeLong(file.length());
            oos.flush();
            //�����ļ�
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
     * �ӷ���˽����ļ�ile_Down
     */
    private void getFile() {
        ObjectInputStream ois = inTl.get();
        //System.out.println("now is getting: " + Thread.currentThread().getName() + " | ois: " + ois);
        FileOutputStream fos = null;
        try {
            //�ļ����ͳ���
            String fileName = ois.readUTF();
            long fileLength = ois.readLong();
            URL resource = getClass().getClassLoader().getResource("resources/clientfiles/" + fileName);
            fos = new FileOutputStream(new File(resource.toURI()));
            BufferedOutputStream bfos = new BufferedOutputStream(fos);
            byte[] sendBytes = new byte[1024];
            int transLen = 0;
            displayMessage("\n----��ʼ�����ļ�<" + fileName + ">----\n-------�ļ���СΪ<" + fileLength + ">-------\n");
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

            displayMessage("----�����ļ�<" + fileName + ">�ɹ�-------\n");
            enterField.setBackground(Color.LIGHT_GRAY);
            enterField.setForeground(Color.RED);
            enterField.setFont(new Font("TimesRoman", Font.BOLD, 10));
            enterField.setText("���ճɹ�������100.00%");
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
     * �����˷�������
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
     * �ӷ���˻������
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
     * չʾ����
     *
     * @param messageToDisplay
     */
    private void displayMessage(final String messageToDisplay) {
        SwingUtilities.invokeLater(() -> displayArea.append(messageToDisplay + "\n"));
    }

    /**
     * �����ı���ɼ�
     *
     * @param editable
     */
    private void setTextFieldEditable(final boolean editable) {
        SwingUtilities.invokeLater(() -> enterField.setEditable(editable));
    }

    /**
     * �Զ����������ʾ
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
     * GUIչʾ
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
     * ��ȡ���������ǰ�߳�
     *
     * @return
     */
    public static ObjectOutputStream getOutStream() {
        return outTl.get();
    }

    /**
     * ��ȡ����������ǰ�߳�
     *
     * @return
     */
    public static ObjectInputStream getInStream() {
        return inTl.get();
    }
}