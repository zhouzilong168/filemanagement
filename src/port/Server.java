package port;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import data.DataProcessing;
import domain.Doc;
import domain.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * �������ˣ�ʵ�������ͻ�������
 *
 * @author thinkpad
 */
public class Server extends JFrame {
    private static final long serialVersionUID = -7527718720024463495L;
    private JTextField enterField; // inputs message from user
    private JTextArea displayArea; // display information to user
    private ServerSocket server; // server socket
    private int counter = 1; // counter of number of connections
    private static final int PORT = 12345; // ���Ӷ˿�
    private static final int REQUESTS = 100; // ���������д�С

    private static ThreadLocal<ObjectOutputStream> outTl = new ThreadLocal<>();
    private static ThreadLocal<ObjectInputStream> inTl = new ThreadLocal<>();
    private static ThreadLocal<Socket> socketTl = new ThreadLocal<>();

    // �̳߳ع����߳�
    private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            10, 100, 60l, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(REQUESTS), Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy()
    );

    public Server() {
        super("Server");
        showGUI();
    }

    public void runServer() {
        try {
            // ServerSocket ���� ������������
            server = new ServerSocket(PORT, REQUESTS); // create ServerSocket
            displayMessage("Waiting for connection");
            while (true) {
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// ���ùرշ�������GUI����ʱ���˳�ϵͳ
                Socket connection = server.accept();// ����һ����Ϣ
                threadPool.execute(new CreateServerThread(connection));// ��������ʱ�������̳߳�
                this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                counter++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                server.close();
            } catch (IOException e) {
                System.out.println("server terminate error");
                e.printStackTrace();
            }
        }
    }

    /**
     * �߳��ڲ������ô���ͻ��˷���������
     */
    class CreateServerThread extends Thread {
        private Socket client;

        CreateServerThread(Socket s) {
            client = s;
            displayMessage("Connection " + counter + " received from: "
                    + client.getInetAddress().getHostName()
                    + "\nClient(" + getName() + ") come in...");
        }

        public void run() {
            socketTl.set(client);
            runServerThread();
        }
    }

    /**
     * �����������߳���������
     */
    private void runServerThread() {
        try {
            getStreams();
            processConnection();
        } catch (TerminationException e) {
            displayMessage(e.getMessage());
        } finally {
            closeConnection();
        }
    }

    /**
     * ��ȡ������
     */
    private void getStreams() {
        Socket connection = socketTl.get();
        ObjectOutputStream dos = null;
        ObjectInputStream dis = null;
        try {
            dos = new ObjectOutputStream(connection.getOutputStream());
            dos.flush();

            // set up input stream for objects
            dis = new ObjectInputStream(connection.getInputStream());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        outTl.set(dos); // ��ӵ�ǰ�����̱߳��ر���
        inTl.set(dis);
/*
        System.out.println("Thread:(getIO) " + Thread.currentThread().getName());
        System.out.print("outTl: " + outTl.get());
        System.out.println("\tinTl: " + inTl.get());
*/
        displayMessage("\nGot I/O streams\n");
    }

    /**
     * ��ͻ���ͨ��������
     *
     * @throws TerminationException
     */
    private void processConnection() throws TerminationException {
        String message = "Connection successful";
        sendData(message);
        displayMessage("CLIENT>>> " + message);
        setTextFieldEditable(true);

        int i;// ʵ�������û������������ϵͳ�˳������ٽ�������
        for (i = 0; i < 3; i++) {
            String users = null;
            String pws = null;
            try {
                message = (String) getData();
                displayMessage(message);
                //System.out.println("process: " + message + "\t" + Thread.currentThread().getName() + " " + inTl.get().hashCode());
                users = (String) getData();
                pws = (String) getData();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("socket is error whiling read or send");
                break;
            }
            if (users != null && pws != null) {
                users = users.substring(10);
                pws = pws.substring(10);
            }
            User user = null;
            try {
                user = DataProcessing.searchUser(users, pws);
            } catch (SQLException e) {
                System.out.println("database error");
            }
            if (user == null) {
                sendData("Error_user_or_pws");
            } else {
                sendData("Logined successful");
                sendData(user);
                exchangeMessage(user);
                break;
            }
        }
        if (i == 3)
            sendData("Threes times Error_User_or_Password");
    }

    /**
     * ��ͻ�����Ϣͨ��
     *
     * @param user
     * @return
     */
    private void exchangeMessage(User user) throws TerminationException {
        String message = "";
        do {
            try {
                message = (String) getData(); // read new message
                if (message == null) {
                    continue;
                }
                displayMessage(message); // display message
                if (message.contains("TERMINATE")) {
                    throw new TerminationException("one Client exit");
                } else if (message.endsWith("Self_Mod")) {
                    modSelf(user); // �޸�
                } else if (message.endsWith("File_Up")) {
                    upFile(); // �ϴ�
                } else if (message.endsWith("File_Down")) {
                    downFile();// ����
                } else if (message.endsWith("User_Add")) {
                    addUser();// �����û�
                } else if (message.endsWith("User_Mod")) {
                    modUser();// �޸��û�
                } else if (message.endsWith("User_Del")) {
                    delUser();// ɾ���û�
                }
            } catch (SQLException e1) {
                System.out.println("database error " + e1.getMessage());
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("socket is error whiling read or send");
                break;
            }
        } while (true);
    }

    /**
     * �ر������ͷ���Դ
     */
    private void closeConnection() {
        setTextFieldEditable(false);
        try {
            ObjectOutputStream oos = outTl.get();
            if (oos != null) {
                oos = null;
            }
            outTl.remove();
            ObjectInputStream ois = inTl.get();
            if (ois != null) {
                ois = null;
            }
            inTl.remove();
            socketTl.get().close();
            socketTl.remove();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * �޸��Լ���Ϣ
     *
     * @param user
     * @throws SQLException
     */
    private void modSelf(User user) throws SQLException, IOException, ClassNotFoundException {
        String ppws = (String) getData();
        String npws = (String) getData();
        ppws = ppws.substring(10);
        npws = npws.substring(10);
        if (!ppws.equals(user.getPassword())) {
            sendData("Error_npws");
        } else if (user.changeSelfInfo(npws)) {
            sendData("Self_Mod_successful");
        } else {
            sendData("Self_Mod_unsuccessful");
        }
    }

    /**
     * �ϴ��ļ�
     *
     * @throws SQLException
     */
    private void upFile() throws SQLException, IOException, ClassNotFoundException {
        Doc doc = (Doc) getData();
        if (DataProcessing.searchDoc(doc.getID()) != null) {
            sendData("File_ID_Exist");
        } else {
            if (!DataProcessing.insertDoc(doc.getID(), doc.getCreator(), doc.getTimestamp(), doc.getDescription(),
                    doc.getFilename())) {
                sendData("File_Up_unsuccessful");
            } else {
                getFile();
                sendData("File_Up_successful");
            }
        }
    }

    /**
     * �����ļ�
     *
     * @throws IOException
     */
    private void downFile() throws IOException, ClassNotFoundException {
        String file = null;
        do {
            file = (String) getData();
            if (file.indexOf("File_Name") > 0) {
                String filename = file.substring(10, file.indexOf("File_Name"));
                sendData(filename + "File_Down_prepare");
                sendFile(filename);
            }
        } while (!file.endsWith("File_Name_Last"));
    }

    /**
     * ����û�
     *
     * @throws SQLException
     */
    private void addUser() throws SQLException, IOException, ClassNotFoundException {
        String users = (String) getData();
        String pws = (String) getData();
        String role = (String) getData();
        users = users.substring(10);
        pws = pws.substring(10);
        role = role.substring(10);
        try {
            if (DataProcessing.insertUser(users, pws, role)) {
                sendData("User_Add_successful");
            } else {
                sendData("User_Add_unsuccessful");
            }
        } catch (MySQLIntegrityConstraintViolationException e) {
            sendData("User_Add_Name_Same");
        }
    }

    /**
     * �޸��û�
     *
     * @throws SQLException
     */
    private void modUser() throws SQLException, IOException, ClassNotFoundException {
        String users = (String) getData();
        String pws = (String) getData();
        String role = (String) getData();
        users = users.substring(10);
        pws = pws.substring(10);
        role = role.substring(10);
        if (DataProcessing.updateUser(users, pws, role)) {
            sendData("User_Mod_successful");
        } else {
            sendData("User_Mod_unsuccessful");
        }
    }

    /**
     * ɾ���û�
     *
     * @throws SQLException
     */
    private void delUser() throws SQLException, IOException, ClassNotFoundException {
        String name = "";
        do {
            name = (String) getData();
            if (name.indexOf("Del_Name") > 0) {
                name = name.substring(10, name.indexOf("Del"));
                if (DataProcessing.deleteUser(name)) {
                    sendData(name + "User_Del_successful");
                } else {
                    sendData(name + "User_Del_unsuccessuful");
                }
            }

        } while (!name.endsWith("Del_Name_Last"));
    }

    /**
     * �����ļ�
     *
     * @param name
     * @throws IOException
     */
    private void sendFile(String name) {
        ObjectOutputStream oos = outTl.get();
        //System.out.println("now is sending :" + Thread.currentThread().getName() + " | oos: " + oos);
        FileInputStream fis = null;
        URL resource = getClass().getClassLoader().getResource("resources/serverfiles/"+name);
        try {
            File file = new File(resource.toURI());
            fis = new FileInputStream(file);
            // �ļ����ͳ���
            oos.writeUTF(file.getName());
            oos.flush();
            oos.writeLong(file.length());
            oos.flush();
            // �����ļ�
            byte[] sendBytes = new byte[1024];
            int length = 0;
            while ((length = fis.read(sendBytes, 0, sendBytes.length)) > 0) {
                oos.write(sendBytes, 0, length);
                oos.flush();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    /**
     * �����ļ�
     */
    private void getFile() {
        ObjectInputStream ois = inTl.get();
        //System.out.println("now is getting :" + Thread.currentThread().getName() + " | ois: " + ois);
        FileOutputStream fos = null;
        try {
            enterField.setOpaque(true);
            // �ļ����ͳ���
            String fileName = ois.readUTF();
            long fileLength = ois.readLong();
            URL resource = getClass().getClassLoader().getResource("resources/serverfiles/" + fileName);
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
                if (transLen == fileLength)
                    break;
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
            enterField.setOpaque(false);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * ��������
     *
     * @param obj
     */
    private void sendData(Object obj) {
        ObjectOutputStream oos = outTl.get();
        if (oos == null) {
            System.out.println("ObjectOutputStream is null");
            return;
        }
        try {
            if (obj instanceof String) {
                oos.writeObject("SERVER>>> " + obj);
                displayMessage("SERVER>>> " + obj);
            } else {
                oos.writeObject(obj);
            }
            oos.flush();
        } catch (IOException e) {
            //displayArea.append("Error writing object");
            //System.out.println("senddata");
            e.printStackTrace();
        }
    }

    /**
     * ��������
     *
     * @return
     */
    private Object getData() throws IOException, ClassNotFoundException {
        ObjectInputStream ois = inTl.get();
        if (ois == null) {
            System.out.println("ObjectInputStream is null");
            return null;
        }
        Object obj = null;
        obj = ois.readObject();
        return obj;
    }

    /**
     * ��ӡ����
     *
     * @param messageToDisplay
     */
    private void displayMessage(final String messageToDisplay) {
        SwingUtilities.invokeLater(() -> displayArea.append(messageToDisplay + "\n")
        );
    }

    /**
     * �����ı���ɱ༭
     *
     * @param editable
     */
    private void setTextFieldEditable(final boolean editable) {
        SwingUtilities.invokeLater(() -> enterField.setEditable(editable)
        );
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
        URL path = getClass().getClassLoader().getResource("resources/pictrue/server.jpg");
        ImageIcon icon = new ImageIcon(path);
        JLabel img = new JLabel(icon);
        this.getLayeredPane().add(img, new Integer(Integer.MIN_VALUE));
        img.setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight());
        Container contain = this.getContentPane();// ���������Container Ҳ������JPanel
        ((JPanel) contain).setOpaque(false);// �������ݴ���͸��
        enterField = new JTextField(); // create enterField
        enterField.setEditable(false);
        enterField.addActionListener(new ActionListener() {
                                         // send message to client
                                         public void actionPerformed(ActionEvent event) {
                                             sendData(event.getActionCommand());
                                             enterField.setText("");
                                         }
                                     }
        );

        add(enterField, BorderLayout.NORTH);
        enterField.setOpaque(false);
        displayArea = new JTextArea(); // create displayArea
        displayArea.setFont(new Font("TimesRoman", Font.ROMAN_BASELINE, 15));
        displayArea.setForeground(Color.CYAN);
        displayArea.setOpaque(false);

        JScrollPane jsp = new JScrollPane(displayArea); // JScrollPanel ����͸��ʱ����Ҫ��������������ԴJViewPort����Ϊ͸��
        jsp.setOpaque(false);
        jsp.getViewport().setOpaque(false);
        add(jsp, BorderLayout.CENTER);


        setSize(300, 600); // set size of window
        this.setLocation(1070, 30);
        setVisible(true); // show window
    }

    class TerminationException extends Exception {
        public TerminationException() {
            super();
        }

        public TerminationException(String message) {
            super(message);
        }
    }
}
