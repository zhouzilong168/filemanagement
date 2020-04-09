package frame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.	*;

import domain.Browser;
import port.Client;
import domain.User;

public class selfFrame extends JFrame implements ActionListener{
	private static final long serialVersionUID = -2274384143825653568L;
	private JPanel panel=new JPanel();
	private JLabel userLab=new JLabel("用户名"),
			       ppwsLab=new JLabel("原密码"),
			       npwsLab=new JLabel("新密码"),
			       npwsLab1=new JLabel("确认新密码"),
			       roleLab=new JLabel("角色");
	private TextField userText=new TextField(15),
			           ppwsText=new TextField(15),
			           npwsText=new TextField(15),
			           npwsText1=new TextField(15),
			           roleText=new TextField(15);
	private JButton modBut=new JButton("修改"),
			        backBut=new JButton("返回");
	private User user;
	public selfFrame(User temp) {
		this.user=temp;
		 ImageIcon icon=new ImageIcon("D:\\OOP\\pictrue\\selfMod.jpg");
		 JLabel img=new JLabel(icon);
		 this.getLayeredPane().add(img, new Integer(Integer.MIN_VALUE));
		 img.setBounds(0, 0, 300, 250);
	     Container contain = this.getContentPane();
	     ((JPanel) contain).setOpaque(false);
		setSize(300,250);
		setLayout(new FlowLayout(FlowLayout.CENTER,10,10));
		panel.setLayout(new GridLayout(5,2,10,10));
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
		userText.setText(temp.getName());
		userText.setEnabled(false);
		roleText.setText(temp.getRole());
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
		if(e.getSource()==modBut||e.getSource()==npwsText1) {
			Object[] option= {"确定","取消"};
			if(ppwsText.getText().equals(npwsText.getText())) {
				JOptionPane.showMessageDialog(new JButton("确定"), "和原密码一致！");
			}
			else if(!npwsText.getText().equals(npwsText1.getText())) {
				JOptionPane.showMessageDialog(new JButton("确定"), "确认密码错误！");
			}
			else if(JOptionPane.showOptionDialog(null, "确认要修改密码吗？","提示！" , JOptionPane.YES_NO_OPTION,
					JOptionPane.PLAIN_MESSAGE, new ImageIcon("D:\\OOP\\pictrue.jpg"),option , option[1])==0) {
				Client.sendData("Self_Mod");
				Client.sendData(ppwsText.getText());
				Client.sendData(npwsText.getText());
			}
		}
		else if(e.getSource()==backBut) {
			this.dispose();
		}

	}
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		new selfFrame(new Browser("s","123","browser"));
	}
}
