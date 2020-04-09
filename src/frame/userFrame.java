package frame;

import javax.swing.*;

import domain.Administrator;
import port.Client;
import data.DataProcessing;
import domain.User;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Enumeration;

public class userFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = 5364838666837636940L;
	private JPanel upPanel=new JPanel(),
	               addPanel=new JPanel(),
	               modPanel=new JPanel(),
	               delPanel=new JPanel(),
	               downPanel=new JPanel();
	private JButton addBut=new JButton("添加"),
			        modBut=new JButton("修改"),
			        delBut=new JButton("删除"),
			        backBut=new JButton("返回");
	private JLabel userLabel=new JLabel("用户名"),
			       pwsLabel=new JLabel("密码"),
			       roleLabel=new JLabel("角色");
	private  JTextField userText=new JTextField(),
			            pwsText=new JTextField();
	private  Choice userList=new Choice(),
			      roleList=new Choice();

	List list=new List(6,true);

	JButton addUser=new JButton("新增用户"),
			modUser=new JButton("修改用户"),
			delUser=new JButton("删除用户");

	String[] delName=new String[20];

	private User user;
	public userFrame(User user) {
		super("用户管理界面");
	    ImageIcon icon=new ImageIcon("D:\\OOP\\pictrue\\user.jpg");
		 JLabel img=new JLabel(icon);
		 this.getLayeredPane().add(img, new Integer(Integer.MIN_VALUE));
		 img.setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight());
	     Container contain = this.getContentPane();
	     ((JPanel) contain).setOpaque(false);
		this.user=user;
		setSize(350,300);
		setLayout(new BorderLayout());
		upPanel.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
		upPanel.add(addUser);
		upPanel.add(modUser);
		upPanel.add(delUser);
		addUser.setBackground(Color.CYAN);
		modUser.setBackground(Color.CYAN);
		delUser.setBackground(Color.CYAN);
		addUser.addActionListener(this);
		modUser.addActionListener(this);
		delUser.addActionListener(this);
		add(upPanel,BorderLayout.NORTH);

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
	private void flushUser() {
		list.removeAll();
		Enumeration<User> user;
		try {
			user = DataProcessing.getAllUser();
		    int i=0;
		    while(user.hasMoreElements()) {
			    User temp=user.nextElement();
			    delName[i++]=temp.getName();
		    	list.add("                "+temp.getName()+"                          "+
			    temp.getPassword()+"                   "+temp.getRole());
		    }
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	private void flushUserName() {
		userList.removeAll();
		Enumeration<User> user;
		try {
			user = DataProcessing.getAllUser();
		    while(user.hasMoreElements()) {
			    User temp=user.nextElement();
			    userList.add(temp.getName());
		    }
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==addUser) {
			downPanel.setBackground(Color.CYAN);
			addPanel.setLayout(new GridLayout(3,2,60,60));
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
			add("Center",addPanel);
			downPanel.add(addBut);
			downPanel.add(backBut);
			add("South",downPanel);
			setVisible(true);
		}
		else if(e.getSource()==modUser) {
			flushUserName();
			downPanel.setBackground(Color.lightGray);
			modPanel.setLayout(new GridLayout(3,2,60,60));
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
			add("Center",modPanel);
			downPanel.add(modBut);
			downPanel.add(backBut);
			add("South",downPanel);
			setVisible(true);
		}
		else if(e.getSource()==delUser) {
			downPanel.setBackground(Color.ORANGE);
			addPanel.setVisible(false);
			modPanel.setVisible(false);
			delPanel.setVisible(true);
			addBut.setVisible(false);
			modBut.setVisible(false);
			delBut.setVisible(true);
			add("Center",delPanel);

			flushUser();
			delPanel.setLayout(new BorderLayout());
			JPanel p=new JPanel();
			p.setBackground(Color.ORANGE);
			p.setOpaque(false);
			p.setLayout(new FlowLayout(FlowLayout.LEFT,50,5));
			p.add(new JLabel("用户名 "));
			p.add(new JLabel(" 密码"));
			p.add(new JLabel(" 角色"));
			delPanel.add("North",p);
			delPanel.add("Center",list);
			downPanel.add(delBut);
			downPanel.add(backBut);
			add("South",downPanel);
			setVisible(true);
		}
		else if(e.getSource()==addBut){
			Object[] option= {"确定","取消"};
			if(JOptionPane.showOptionDialog(null, "确认要添加用户吗？","提示！" , JOptionPane.YES_NO_OPTION,
					JOptionPane.PLAIN_MESSAGE, new ImageIcon("D:\\OOP\\pictrue.jpg"),option , option[1])==0) {
				Client.sendData("User_Add");
				Client.sendData(userText.getText());
				Client.sendData(pwsText.getText());
				Client.sendData(roleList.getSelectedItem());
			}
		}
		else if(e.getSource()==modBut) {
			Object[] option= {"确定","取消"};
			if(JOptionPane.showOptionDialog(null, "确认要修改用户吗？","提示！" , JOptionPane.YES_NO_OPTION,
					JOptionPane.PLAIN_MESSAGE, new ImageIcon("D:\\OOP\\pictrue.jpg"),option , option[1])==0) {
				Client.sendData("User_Mod");
				Client.sendData(userList.getSelectedItem());
				Client.sendData(pwsText.getText());
				Client.sendData(roleList.getSelectedItem());
			}
		}
		else if(e.getSource()==delBut) {
			int[] delIndex=list.getSelectedIndexes();
			if(list.getSelectedIndexes().length==0) {
				JOptionPane.showMessageDialog(new JButton("确定"), "请至少选择一项");
			}
			else {
				Object[] option= {"确定","取消"};
				if(JOptionPane.showOptionDialog(null, "确认要删除用户吗？","提示！" , JOptionPane.YES_NO_OPTION,
						JOptionPane.PLAIN_MESSAGE, new ImageIcon("D:\\OOP\\pictrue.jpg"),option , option[1])==0)
				{
					int s;
					Client.sendData("User_Del");
						for(s=0;s<delIndex.length-1;s++) {
							Client.sendData(delName[delIndex[s]]+"Del_Name");
						}
						Client.sendData(delName[delIndex[s]]+"Del_Name_Last");
				}
				else {

				}
			}

		}
		else if(e.getSource()==backBut) {
			this.dispose();
		}
		else {

		}

	}
	public static void main(String[] args) {
		new userFrame(new Administrator("a","b","c"));
	}


}