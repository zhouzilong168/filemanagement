package domain;

import data.DataProcessing;

import java.sql.SQLException;
import java.util.Scanner;
import java.io.*;

public abstract class User implements Serializable  {
	/**
	 *
	 */
	private static final long serialVersionUID = 367088993086102345L;
	private String name;
	private String password;
	private String role;

	public User(String name,String password,String role){
		this.name=name;
		this.password=password;
		this.role=role;
	}

	public boolean changeSelfInfo(String password) throws SQLException{
		//д�û���Ϣ���洢
		if (DataProcessing.updateUser(name, password, role)){
			this.password=password;
			System.out.println("�޸ĳɹ�");
			return true;
		}else
			return false;
	}

	public boolean downloadFile(){
//		double ranValue=Math.random();
//		if (ranValue>0.5)
//			throw new IOException( "Error in accessing file" );
        try {
            Scanner input=new Scanner(System.in);
            System.out.println("�����ļ�\n"+"�����뵵���ţ�");
            String id=input.next();
            if(DataProcessing.searchDoc(id)==null)System.out.print("�ļ������ڣ�");
            Doc temp=DataProcessing.searchDoc(id);
            File file=new File("D:\\OOP\\downloadfile\\"+temp.getFilename());
            FileInputStream fin=new FileInputStream("D:\\OOP\\uploadfile\\"+temp.getFilename());
            FileOutputStream fou=new FileOutputStream(file,true);
            byte[] temp1=new byte[fin.available()];
            while(fin.read(temp1)>0) {
                fou.write(temp1);
            }
            fin.close();
            fou.close();
            if(file.exists()) {
                System.out.print("���سɹ�\n");
                return true;
            }
            else {
                System.out.print("����ʧ��");
                return false;
            }
        } catch (SQLException | IOException | NullPointerException e) {
            //e.printStackTrace();
            System.out.println("�ļ�����ʧ�ܣ�");
            return false;
        }
    }

	public void showFileList() throws SQLException{
//		double ranValue=Math.random();
//		if (ranValue>0.5)
//			throw new SQLException( "Error in accessing file DB" );
		System.out.print("�ļ��б�\n");
		DataProcessing.getAllDocs();
	}

	public void modSelfInfo() throws SQLException {
		Scanner input=new Scanner(System.in);
		System.out.println("�޸ı�������\n"+"�����������룺");
		String psw=input.next();
		if(changeSelfInfo(psw)) {
			System.out.println("�޸ĳɹ�");
		}
	}

	public abstract void showMenu() throws IOException, SQLException;

	public void exitSystem(){
		System.out.println("ϵͳ�˳�, ллʹ�� ! ");
		System.exit(0);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
