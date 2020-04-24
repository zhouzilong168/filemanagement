package domain;

import data.DataProcessing;

import java.io.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public abstract class User implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 367088993086102345L;
    private String name;
    private String password;
    private String role;

    public User(String name, String password, String role) {
        this.name = name;
        this.password = password;
        this.role = role;
    }

    public boolean changeSelfInfo(String password) throws SQLException {
        //写用户信息到存储
        if (DataProcessing.updateUser(name, password, role)) {
            this.password = password;
            System.out.println("修改成功");
            return true;
        } else
            return false;
    }

    public boolean downloadFile() {
        // 模拟异常
/*		double ranValue=Math.random();
		if (ranValue>0.5)
			throw new IOException( "Error in accessing file" );*/
        try {
            Scanner input = new Scanner(System.in);
            System.out.println("下载文件\n" + "请输入档案号：");
            String id = input.next();
            if (DataProcessing.searchDoc(id) == null) System.out.print("文件不存在！");
            Doc temp = DataProcessing.searchDoc(id);
            URL upresource = getClass().getClassLoader().getResource("resources/serverfiles");
            URL downresource = getClass().getClassLoader().getResource("resources/clientfiles");
            File file = new File(downresource + temp.getFilename());
            FileInputStream fin = new FileInputStream(upresource + temp.getFilename());
            FileOutputStream fou = new FileOutputStream(file, true);
            byte[] temp1 = new byte[fin.available()];
            while (fin.read(temp1) > 0) {
                fou.write(temp1);
            }
            fin.close();
            fou.close();
            if (file.exists()) {
                System.out.print("下载成功\n");
                return true;
            } else {
                System.out.print("下载失败");
                return false;
            }
        } catch (SQLException | IOException | NullPointerException e) {
            //e.printStackTrace();
            System.out.println("文件下载失败！");
            return false;
        }
    }

    public void showFileList() throws SQLException {
/*		double ranValue=Math.random();
		if (ranValue>0.5)
			throw new SQLException( "Error in accessing file DB" );*/
        System.out.print("文件列表\n");
        List<Doc> allDocs = DataProcessing.getAllDocs();
        for (Doc doc:
                allDocs) {
            System.out.println("ID:" + doc.getID() + "\tCreator:" +
                    doc.getCreator() + "\tTime:" + doc.getTimestamp() +
                    "\tFilename:" + doc.getFilename() + "\tDiscription:" +
                    doc.getDescription() + "\n");
        }
    }

    public void modSelfInfo() throws SQLException {
        Scanner input = new Scanner(System.in);
        System.out.println("修改本人密码\n" + "请输入新密码：");
        String psw = input.next();
        if (changeSelfInfo(psw)) {
            System.out.println("修改成功");
        }
    }

    public abstract void showMenu() throws IOException, SQLException;

    public void exitSystem() {
        System.out.println("系统退出, 谢谢使用 ! ");
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
