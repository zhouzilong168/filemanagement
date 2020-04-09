package domain;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

import data.DataProcessing;

public class Administrator extends User {
	public Administrator(String name, String password, String role) {
		super(name, password, role);
		// TODO 自动生成的构造函数存根
	}
	public void showMenu() throws SQLException, IOException
	{
		Scanner input=new Scanner(System.in);
		int s;
		do {
			System.out.print("********欢迎进入档案录入人员菜单********\n"+"1.修改用户\n"+"2.删除用户\n"+"3.新增用户\n"+"4.列出用户\n"+"5.下载文件\n"+"6.文件列表\n"+"7.修改密码\n"+"8.退出\n"+"***********************\n"+"请选择菜单：");
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
					System.out.println("输入错误！");
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
			System.out.println("修改成功");
			return true;
		}else {
			System.out.println("修改失败");
			return false;
		}
	}
	public boolean modUser() throws SQLException {
		Scanner input=new Scanner(System.in);
		System.out.print("修改用户\n");
		System.out.println("请输入用户名：");
		String name=input.next();
		System.out.println("请输入密码：");
		String pws=input.next();
		System.out.println("请输入角色：");
		String role=input.next();
		if(changeUserInfo(name,pws,role))return true;
		return false;
	}
	public boolean delUser() throws SQLException {
		Scanner input=new Scanner(System.in);
		System.out.print("删除用户\n");
		System.out.println("请输入用户名：");
		String name=input.next();
		if(DataProcessing.deleteUser(name)) return true;
		return false;
	}
	public boolean addUser() throws SQLException {
		Scanner input=new Scanner(System.in);
		System.out.print("新增用户\n");
		System.out.println("请输入用户名：");
		String name=input.next();
		System.out.println("请输入密码：");
		String pws=input.next();
		System.out.println("请输入角色：");
		String role=input.next();
		if(DataProcessing.insertUser(name,pws,role)) return true;
		return false;
	}

	public void listUser() throws SQLException {
		System.out.print("列出用户\n");
		DataProcessing.getAllUser();
	}

}
