package frame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Enumeration;

import javax.swing.*;

import port.Client;
import data.DataProcessing;
import domain.Doc;
import domain.Operator;
import domain.User;


public class fileFrame extends JFrame implements ActionListener{
	private JPanel panel0=new JPanel(),
			       panel1=new JPanel(),
			       panel2=new JPanel(),
	               panel11=new JPanel(),
	               panel22=new JPanel(),
	               panel=new JPanel();
	JPanel panel3=new JPanel(new FlowLayout(FlowLayout.LEFT,40,1));
	private JButton downFileBut=new JButton("�ļ�����"),
			        upFileBut=new JButton("�ļ��ϴ�"),
			        downBut=new JButton("����"),
			        backBut=new JButton("����");
	private JLabel idLab=new JLabel("������"),
			       creLab=new JLabel("������"),
			       timeLab=new JLabel("     ʱ��                  "),
				   nameLab=new JLabel("�ļ���"),
				   desLab=new JLabel("����");

    private JLabel lab11=new JLabel("             ������"),
		           lab22=new JLabel("             ��������"),
		           lab33=new JLabel("             �����ļ���");
    private JButton openBut=new JButton("��"),
		            upBut=new JButton("�ϴ�"),
		            canBut=new JButton("ȡ��");
    private JTextField idText=new JTextField(15),
		               nameText=new JTextField(15);
    private JTextArea desText=new JTextArea(3,15);

    private List fileList=new List(6,true);
    private String[] downName=new String[20];
    private String[] downID=new String[20];
    private JFrame fr=new JFrame();

//    private JTable table;

    private JFrame f=new JFrame();

	private String dictionary;

    private User user;
	public fileFrame(User user) throws SQLException {
		super("�ļ��������");
		this.user=user;
		 ImageIcon icon=new ImageIcon("D:\\OOP\\pictrue\\file.jpg");
		 JLabel img=new JLabel(icon);
		 this.getLayeredPane().add(img, new Integer(Integer.MIN_VALUE));
		 img.setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight());
	     Container contain = this.getContentPane();
	     ((JPanel) contain).setOpaque(false);
		setSize(600,300);
		setLayout(new BorderLayout());
		panel11.setBackground(Color.PINK);
		panel22.setBackground(Color.PINK);
		panel2.setBackground(Color.LIGHT_GRAY);
//		fileList.setBackground(Color.LIGHT_GRAY);
		panel0.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
	    panel0.add(downFileBut);
        panel0.add(upFileBut);
        downFileBut.setBackground(Color.CYAN);
        upFileBut.setBackground(Color.CYAN);
        downFileBut.setForeground(Color.BLUE);
        upFileBut.setForeground(Color.BLUE);
        upFileBut.setFocusPainted(false);
        downFileBut.setFocusPainted(false);
        if(user.getRole().equals("administrator")||user.getRole().equals("browser")) {
        	upFileBut.setEnabled(false);
        }
        else {
        	upFileBut.setEnabled(true);
        }
        add("North",panel0);

        panel1.setLayout(new BorderLayout());
        flushFile();
		panel1.add("Center",fileList);
        JPanel p=new JPanel();
        p.setOpaque(false);
        p.setLayout(new FlowLayout(FlowLayout.LEFT,50,10));
        p.add(idLab);
        p.add(creLab);
        p.add(timeLab);
        p.add(nameLab);
        p.add(desLab);
        panel1.add("North",p);

		panel2.setLayout(new FlowLayout(FlowLayout.CENTER,2,0));
		panel2.add(downBut);
		panel2.add(backBut);
//		add("South",panel2);
		downFileBut.addActionListener(this);
		upFileBut.addActionListener(this);
		downBut.addActionListener(this);
		backBut.addActionListener(this);
		panel11.setLayout(new GridLayout(3,3,100,60));
		panel11.add(lab11);
		panel11.add(idText);
		panel11.add(new JLabel());
		panel11.add(lab22);
		panel11.add(desText);
		panel11.add(new JLabel());
		panel11.add(lab33);
		panel11.add(nameText);
		panel11.add(openBut);
		panel22.setLayout(new FlowLayout(FlowLayout.CENTER,2,0));
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
	public void flushFile(){
		fileList.removeAll();
		try {
			Enumeration<Doc> doc  = DataProcessing.getAllDocs();
			int i=0;
			while(doc.hasMoreElements()) {
				Doc temp1=doc.nextElement();
				downName[i]=temp1.getFilename();
				downID[i++]=temp1.getID();
				fileList.add("               "+temp1.getID()+"                    "+
				temp1.getCreator()+"           "+
				temp1.getTimestamp()+"                "+temp1.getFilename()+
				"               "+temp1.getDescription());
			}
		}catch(Exception e) {
			e.getMessage();
		}
	}
	public void actionPerformed(ActionEvent e){
		if(e.getSource()==downFileBut) {
			flushFile();
			add("Center",panel1);
			add("South",panel2);
			panel11.setVisible(false);
			panel22.setVisible(false);
    		panel1.setVisible(true);
			panel2.setVisible(true);
			setVisible(true);
		}
		else if(e.getSource()==upFileBut) {
			add("Center",panel11);
			add("South",panel22);
		    panel1.setVisible(false);
			panel2.setVisible(false);
			panel11.setVisible(true);
			panel22.setVisible(true);
			setVisible(true);
		}
		else if(e.getSource()==downBut) {
			int[] downIndex=fileList.getSelectedIndexes();
			if(downIndex.length==0) {
			JOptionPane.showMessageDialog(new JButton("ȷ��"), "������ѡ��һ��");
		    }
			else {
				Object[] option= {"ȷ��","ȡ��"};
				if(JOptionPane.showOptionDialog(null, "ȷ��Ҫ�����ļ���","��ʾ��" , JOptionPane.YES_NO_OPTION,
						JOptionPane.PLAIN_MESSAGE, new ImageIcon("D:\\OOP\\pictrue.jpg"),option , option[1])==0){
					Client.sendData("File_Down");
					int i;
					for(i=0;i<downIndex.length-1;i++) {
//						FileDialog fd=new FileDialog(fr,"�����ļ�",FileDialog.SAVE);
//						fd.show();
						Client.sendData(downName[downIndex[i]]+"File_Name");
					}
					Client.sendData(downName[downIndex[i]]+"File_Name_Last");
				}
			}
/*			if(downIndex.length==0) {
				JOptionPane.showMessageDialog(new JButton("ȷ��"), "������ѡ��һ��");
			}
			else {
				Object[] option= {"ȷ��","ȡ��"};
				if(JOptionPane.showOptionDialog(null, "ȷ��Ҫ�����ļ���","��ʾ��" , JOptionPane.YES_NO_OPTION,
						JOptionPane.PLAIN_MESSAGE, new ImageIcon("D:\\OOP\\pictrue.jpg"),option , option[1])==0)
				{
					for(int i=0;i<downIndex.length;i++) {
						FileDialog fd=new FileDialog(fr,"�����ļ�",FileDialog.SAVE);
						fd.show();
						try {
							FileInputStream fin=new FileInputStream("D:\\OOP\\uploadfile\\"+downName[downIndex[i]]);
							FileOutputStream fou=new FileOutputStream(fd.getDirectory() + fd.getFile());
							byte[] temp1=new byte[fin.available()];
							while(fin.read(temp1)>0) {
								fou.write(temp1);
							}
							fin.close();
							fou.close();
						if(DataProcessing.searchDoc(downID[downIndex[i]])==null) {
							JOptionPane.showMessageDialog(new JButton("ȷ��"), downName[downIndex[i]]+"����ʧ��");
						}
						else {
							JOptionPane.showMessageDialog(new JButton("ȷ��"), downName[downIndex[i]]+"���سɹ�");
						}
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
				else {

				}
			}*/

		}
		else if(e.getSource()==backBut) {
			this.dispose();
		}
		else if(e.getSource()==openBut) {
			FileDialog fd=new FileDialog(f,"Open file",FileDialog.LOAD);
			fd.show();
			nameText.setText(fd.getFile());
			dictionary=fd.getDirectory()+fd.getFile();
		}
		else if(e.getSource()==upBut) {
			try {
//				System.out.println("dic"+dictionary);
				Object[] option= {"ȷ��","ȡ��"};
				if(JOptionPane.showOptionDialog(null, "ȷ��Ҫ�ϴ��ļ���","��ʾ��" , JOptionPane.YES_NO_OPTION,
				JOptionPane.PLAIN_MESSAGE, new ImageIcon("D:\\OOP\\pictrue.jpg"),option , option[1])==0) {
					Timestamp timestamp = new Timestamp(System.currentTimeMillis());
					Client.sendData("File_Up");
					Client.dos.writeObject(new Doc(idText.getText(),user.getName(),timestamp,desText.getText(),
							nameText.getText()));
					Client.sendFile(dictionary);
				}
/*				if(DataProcessing.searchDoc(idText.getText())!=null) {
					JOptionPane.showMessageDialog(new JButton("ȷ��"), "�õ������Ѵ��ڣ�");
				}
				else {
					Object[] option= {"ȷ��","ȡ��"};
					if(JOptionPane.showOptionDialog(null, "ȷ��Ҫ�ϴ��ļ���","��ʾ��" , JOptionPane.YES_NO_OPTION,
					JOptionPane.PLAIN_MESSAGE, new ImageIcon("D:\\OOP\\pictrue.jpg"),option , option[1])==0) {
						Timestamp timestamp = new Timestamp(System.currentTimeMillis());
					    if(!DataProcessing.insertDoc(idText.getText(), user.getName(), timestamp,
							desText.getText(), nameText.getText())) {
						    JOptionPane.showMessageDialog(new JButton("ȷ��"), "�ϴ�ʧ��");
					    }
					    else {
					    	JOptionPane.showMessageDialog(new JButton("ȷ��"), "�ϴ��ɹ�");
					    }
					}
					else {

					}
				}*/
			} catch(Exception e2) {
				e2.printStackTrace();
			}
		}
		else if(e.getSource()==canBut) {
			this.dispose();
		}
		else {

		}
	}
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		DataProcessing.connectToDatabase();
		new fileFrame(new Operator("a","123","operator"));
	}
	/*        String[] header={"������","������","ʱ��","�ļ���","����"};
    Object[][] row = {
            {001,"����", 80, 80, 80, 240},
            {002,"John", 70, 80, 90, 240},
            {003,"Sue", 70, 70, 70, 210},
            {004,"Jane", 80, 70, 60, 210},
            {005,"Joe", 80, 70, 60, 210}
    };
    for(int i=0;i<5;i++)row[i][1]=new Object();
    table=new JTable(row,header);
    table.setForeground(Color.BLACK);                   // ������ɫ
    table.setFont(new Font(null, Font.PLAIN, 14));      // ������ʽ
    table.setSelectionForeground(Color.DARK_GRAY);      // ѡ�к�������ɫ
    table.setSelectionBackground(Color.LIGHT_GRAY);     // ѡ�к����屳��
    table.setGridColor(Color.GRAY);
    // ���ñ�ͷ
    table.getTableHeader().setFont(new Font(null, Font.BOLD, 14));  // ���ñ�ͷ����������ʽ
    table.getTableHeader().setForeground(Color.RED);                // ���ñ�ͷ����������ɫ
    table.getTableHeader().setResizingAllowed(false);               // ���ò������ֶ��ı��п�
    table.getTableHeader().setReorderingAllowed(false);             // ���ò������϶������������

    // �����и�
    table.setRowHeight(30);

    // ��һ���п�����Ϊ40
    table.getColumnModel().getColumn(0).setPreferredWidth(40);

    // ���ù�������ӿڴ�С�������ô�С�������ݣ���Ҫ�϶����������ܿ�����
    table.setPreferredScrollableViewportSize(new Dimension(400, 300));

    // �� ��� �ŵ� ������� �У���ͷ���Զ���ӵ�������嶥����
    JScrollPane scrollPane = new JScrollPane(table);

    // ��� ������� �� �������
    panel.add(scrollPane);
    add("Center",panel);*/

/*        panel1.setLayout(new GridLayout(12,5,0,0));
	panel1.add(lab1);
	panel1.add(lab2);
	panel1.add(lab3);
	panel1.add(lab4);
	panel1.add(lab5);
	for(int i=0;i<55;i++) {
		JTextField t=new JTextField(15);
		panel1.add(t);
		t.setEnabled(false);
	}*/
/*      panel.setLayout(new BorderLayout());
    panel3.add(lab1);
    panel3.add(lab2);
    panel3.add(lab3);
    panel3.add(lab4);
    panel3.add(lab5);
    panel3.setVisible(true);
    panel.add("North",panel3);
    JTextArea j=new JTextArea("",11,5);
    JScrollPane jsp=new JScrollPane(j);
    jsp.setBounds(400, 300, 30, 50);
    j.setAutoscrolls(true);
	panel.add("Center",j);*/
//	add("Center",panel1);
}
