package domain;

import data.DataProcessing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Scanner;

public class Operator extends User {
    public Operator(String name, String password, String role) {
        super(name, password, role);
    }

    public void showMenu() throws IOException, SQLException {
        Scanner input = new Scanner(System.in);
        int s;
        do {
            System.out.print("********欢迎进入档案录入人员菜单********\n" + "1.上传文件\n" + "2.下载文件\n" + "3.文件列表\n" + "4.修改密码\n" + "5.退出\n" + "***********************\n" + "请选择菜单：");
            s = input.nextInt();
            try {
                switch (s) {
                    case 1: {
                        uploadFile();
                        break;
                    }
                    case 2: {
                        downloadFile();
                        break;
                    }
                    case 3: {
                        showFileList();
                        break;
                    }
                    case 4: {
                        modSelfInfo();
                        break;
                    }
                    case 5: {
                        exitSystem();
                        break;
                    }
                    default:
                        System.out.println("输入错误！");
                }
            } catch (SQLException e) {
                System.out.print(e.getMessage() + "\n");
            }
        } while (s != 5);
    }

    public boolean uploadFile() throws IOException, SQLException {
        Scanner input = new Scanner(System.in);
        System.out.println("上传文件\n" + "请输入文件路径：");
        String path = input.next();
        System.out.println("请输入文件名：");
        String name = input.next();
        System.out.println("请输入档案号：");
        String num = input.next();
        System.out.println("请输入档案描述：");
        String description = input.next();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        if (!DataProcessing.insertDoc(num, getName(), timestamp, description, name)) {
            System.out.println("the ID aleady exist! 上传失败！");
            return false;
        } else {
            URL resource = getClass().getClassLoader().getResource("resources/serverfiles");
            File file = new File(resource + name);
            FileInputStream fin = new FileInputStream(path + name);
            FileOutputStream fou = new FileOutputStream(file, true);
            byte[] temp = new byte[fin.available()];
            while (fin.read(temp) > 0) {
                fou.write(temp);
            }
            fin.close();
            fou.close();

            if (file.exists()) {
                System.out.print("上传成功\n");
                return true;
            } else {
                System.out.print("上传失败\n");
                return false;
            }
        }
    }

}
