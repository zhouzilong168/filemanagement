package domain;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

import data.DataProcessing;

public class Administrator extends User {
	public Administrator(String name, String password, String role) {
		super(name, password, role);
		// TODO �Զ����ɵĹ��캯�����
	}
	public void showMenu() throws SQLException, IOException
	{
		Scanner input=new Scanner(System.in);
		int s;
		do {
			System.out.print("********��ӭ���뵵��¼����Ա�˵�********\n"+"1.�޸��û�\n"+"2.ɾ���û�\n"+"3.�����û�\n"+"4.�г��û�\n"+"5.�����ļ�\n"+"6.�ļ��б�\n"+"7.�޸�����\n"+"8.�˳�\n"+"***********************\n"+"��ѡ��˵���");
			s=input.nextInt();
			try {
				switch(s) {
				case 1:{
					modUser();
					break;
				}
				case 2:{
					delUser();
					break;
				}
				case 3:{
					addUser();
					break;
				}
				case 4:{
					listUser();
					break;
				}
				case 5:{
					downloadFile();
					break;
				}
				case 6:{
					showFileList();
					break;
				}
				case 7:{
					modSelfInfo();
					break;
				}
				case 8:{
					exitSystem();
					break;
				}
				default:
					System.out.println("�������");
				}
			}catch(SQLException e) {
				System.out.print(e.getMessage()+"\n");
			}

		}while(s!=9);
	}

	public boolean changeUserInfo(String name,String password,String role) throws SQLException
	{
		User temp= DataProcessing.search(name);
		if (DataProcessing.updateUser(name,password,role)){
			temp.setRole(role);
			temp.setPassword(password);
			System.out.println("�޸ĳɹ�");
			return true;
		}else {
			System.out.println("�޸�ʧ��");
			return false;
		}
	}
	public boolean modUser() throws SQLException {
		Scanner input=new Scanner(System.in);
		System.out.print("�޸��û�\n");
		System.out.println("�������û�����");
		String name=input.next();
		System.out.println("���������룺");
		String pws=input.next();
		System.out.println("�������ɫ��");
		String role=input.next();
		if(changeUserInfo(name,pws,role))return true;
		return false;
	}
	public boolean delUser() throws SQLException {
		Scanner input=new Scanner(System.in);
		System.out.print("ɾ���û�\n");
		System.out.println("�������û�����");
		String name=input.next();
		if(DataProcessing.deleteUser(name)) return true;
		return false;
	}
	public boolean addUser() throws SQLException {
		Scanner input=new Scanner(System.in);
		System.out.print("�����û�\n");
		System.out.println("�������û�����");
		String name=input.next();
		System.out.println("���������룺");
		String pws=input.next();
		System.out.println("�������ɫ��");
		String role=input.next();
		if(DataProcessing.insertUser(name,pws,role)) return true;
		return false;
	}

	public void listUser() throws SQLException {
		System.out.print("�г��û�\n");
		DataProcessing.getAllUser();
	}

}
