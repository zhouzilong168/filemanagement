package port;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.naming.NameNotFoundException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import domain.Doc;
import domain.User;
import data.DataProcessing;

/**
 * 服务器端，实现与多个客户端连接
 *
 * @author thinkpad
 *
 */
public class Server extends JFrame {
	private static final long serialVersionUID = -7527718720024463495L;
	private JTextField enterField; // inputs message from user
	private JTextArea displayArea; // display information to user
	private ServerSocket server; // server socket
	private Socket connection; // connection to client
	private int counter = 1; // counter of number of connections

	private ObjectOutputStream dos; // output stream to client
	private ObjectInputStream dis; // input stream from client
	private FileOutputStream fos;
	private FileInputStream fis;

	// set up GUI
	public Server() throws IOException {
		super("Server");
		ImageIcon icon = new ImageIcon("D:\\OOP\\pictrue\\server.jpg");
		JLabel img = new JLabel(icon);
		this.getLayeredPane().add(img, new Integer(Integer.MIN_VALUE));
		img.setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight());
		Container contain = this.getContentPane();// 这里可以用Container 也可以用JPanel
		((JPanel) contain).setOpaque(false);// 设置内容窗格透明
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

		JScrollPane jsp=new JScrollPane(displayArea); // JScrollPanel 设置透明时候，需要将滚动面板的数据源JViewPort设置为透明
		jsp.setOpaque(false);
		jsp.getViewport().setOpaque(false);
		add( jsp, BorderLayout.CENTER );


		setSize(300, 600); // set size of window
		this.setLocation(1070, 30);
		setVisible(true); // show window

		server = new ServerSocket(12345, 100); // create ServerSocket
		displayMessage("Waiting for connection\n");
		try {
			while (true) {
				setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 设置关闭服务器端GUI界面时，退出系统
				connection = server.accept();// 接收一个消息
				new CreateServerThread(connection);// 当有请求时，启一个线程处理-
				this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				counter++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeConnection();
			server.close();
		}
	} // end Server constructor

	// 内部类
	class CreateServerThread extends Thread {
		private Socket client;

		public CreateServerThread(Socket s) throws IOException {
			client = s;
			displayMessage("\nConnection " + counter + " received from: " + client.getInetAddress().getHostName()
					+ "\nClient(" + getName() + ") come in...");
			start();
		}

		public void run() {
			try {
				runServer();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// set up and run server
	public void runServer() throws ClassNotFoundException, SQLException, HeadlessException, NameNotFoundException {
		try // set up server to receive connections; process connections
		{
			try {
				getStreams(); // get input & output streams
				processConnection();
			} catch (SocketException e) {
				displayMessage("one Client exit");
			} catch (EOFException eofException) {
				displayMessage("\nServer terminated connection");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// get streams to send and receive data
	private void getStreams() throws IOException {
		dos = new ObjectOutputStream(connection.getOutputStream());
		dos.flush();

		// set up input stream for objects
		dis = new ObjectInputStream(connection.getInputStream());
		displayMessage("\nGot I/O streams\n");
	}

	private void processConnection()
			throws IOException, NameNotFoundException, HeadlessException, SQLException, ClassNotFoundException {
		String message = "Connection successful";
		sendData(message);
		displayMessage("\nCLIENT>>> " + message);
		setTextFieldEditable(true);

		int i;// 实现三次用户名或密码错误，系统退出不得再进行输入
		for (i = 0; i < 3; i++) {
			message = (String) dis.readObject();
			displayMessage("\n" + message);
			System.out.println(message);
			String users = (String) dis.readObject();
			String pws = (String) dis.readObject();
			users = users.substring(10);
			pws = pws.substring(10);
			System.out.println(users + pws);
			final User user = DataProcessing.searchUser(users, pws);
			if (user == null) {
				sendData("Error_user_or_pws");
			} else {
				sendData("Logined successful");
				dos.writeObject(user);
				dos.flush();
				do
				{
					try
					{
						message = (String) dis.readObject(); // read new message
						displayMessage("\n" + message); // display message
						// if(message.endsWith("Logining")) logining();
						if (message.endsWith("Self_Mod"))
							modSelf(user); // 修改
						if (message.endsWith("File_Up"))
							upFile(); // 上传
						if (message.endsWith("File_Down"))
							downFile();// 下载
						if (message.endsWith("User_Add"))
							addUser();// 新增用户
						if (message.endsWith("User_Mod"))
							modUser();// 修改用户
						if (message.endsWith("User_Del"))
							delUser();// 删除用户
					} catch (SQLException e1) {
						e1.printStackTrace();
					} catch (ClassNotFoundException classNotFoundException) {
						displayMessage("\nUnknown object type received");
					} catch (HeadlessException e1) {
						// TODO 自动生成的 catch 块
						e1.printStackTrace();
					}
				} while (!message.equals("CLIENT>>> TERMINATE"));
			}
		}
		if (i == 3)
			sendData("Threes times Error_User_or_Password");
	}

	// close streams and socket
	private void closeConnection() {
		displayMessage("\nTerminating connection\n");
		setTextFieldEditable(false);

		try {
			if (dos != null)
				dos.close();
			if (dis != null)
				dis.close();
			if (fos != null)
				fos.close();
			if (fis != null)
				fis.close();
			connection.close();
		}
		catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	private void modSelf(User user) throws SQLException, ClassNotFoundException, IOException {
		String ppws = (String) dis.readObject();
		String npws = (String) dis.readObject();
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

	private void upFile() throws SQLException, IOException, ClassNotFoundException {
		Doc doc = (Doc) dis.readObject();
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

	private void downFile() throws IOException, ClassNotFoundException {
		String file = null;
		do {
			file = (String) dis.readObject();
			if (file.indexOf("File_Name") > 0) {
				file = file.substring(10, file.indexOf("File_Name"));
				sendData(file + "File_Down_prepare");
				sendFile("D:\\OOP\\uploadfile\\" + file);
			}
		} while (!file.endsWith("File_Name_Last"));
	}

	private void addUser() throws ClassNotFoundException, IOException, SQLException {
		String users = (String) dis.readObject();
		String pws = (String) dis.readObject();
		String role = (String) dis.readObject();
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

	private void modUser() throws ClassNotFoundException, IOException, SQLException {
		String users = (String) dis.readObject();
		String pws = (String) dis.readObject();
		String role = (String) dis.readObject();
		users = users.substring(10);
		pws = pws.substring(10);
		role = role.substring(10);
		if (DataProcessing.updateUser(users, pws, role)) {
			sendData("User_Mod_successful");
		} else {
			sendData("User_Mod_unsuccessful");
		}
	}

	private void delUser() throws ClassNotFoundException, IOException, SQLException {
		String name = "";
		do {
			name = (String) dis.readObject();
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

	public void sendFile(String name) throws IOException {
		try {
			File file = new File(name);
			fis = new FileInputStream(file);
			// 文件名和长度
			dos.writeUTF(file.getName());
			dos.flush();
			dos.writeLong(file.length());
			dos.flush();
			// 传输文件
			byte[] sendBytes = new byte[1024];
			int length = 0;
			while ((length = fis.read(sendBytes, 0, sendBytes.length)) > 0) {
				dos.write(sendBytes, 0, length);
				dos.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null)
				fis.close();
		}
	}

	// get file from client
	private void getFile() throws IOException {
		try {
			enterField.setOpaque(true);
			// 文件名和长度
			String fileName = dis.readUTF();
			long fileLength = dis.readLong();
			fos = new FileOutputStream(new File("D:\\OOP\\uploadfile\\" + fileName));
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
				if (transLen == fileLength)
					break;
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
			enterField.setOpaque(false);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null)
				fos.close();
		}
	}

	// send message to client
	private void sendData(String message) {
		try
		{
			dos.writeObject("SERVER>>> " + message);
			dos.flush();
			displayMessage("\nSERVER>>> " + message);
		}
		catch (IOException ioException) {
			displayArea.append("\nError writing object");
		}
	}

	// manipulates displayArea in the event-dispatch thread
	private void displayMessage(final String messageToDisplay) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				displayArea.append(messageToDisplay);
			}
		}
		);
	}

	// manipulates enterField in the event-dispatch thread
	private void setTextFieldEditable(final boolean editable) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				enterField.setEditable(editable);
			}
		}
		);
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

	public static void main(String args[])
			throws ClassNotFoundException, SQLException, IOException, HeadlessException, NameNotFoundException {
		DataProcessing.connectToDatabase();
		new Server(); // create server
	}
}
