package domain;

import java.sql.SQLException;
import java.util.Scanner;

public class Browser extends User {
    public Browser(String name, String password, String role) {
        super(name, password, role);
    }

    public void showMenu() {
        Scanner input = new Scanner(System.in);
        int s;
        do {
            System.out.print("********欢迎进入档案浏览人员菜单********\n" + "1.下载文件\n" + "2.文件列表\n" + "3.修改密码\n" + "4.退出\n" + "***********************\n" + "请选择菜单：");
            s = input.nextInt();
            try {
                switch (s) {
                    case 1: {
                        downloadFile();
                        break;
                    }
                    case 2: {
                        showFileList();
                        break;
                    }
                    case 3: {
                        modSelfInfo();
                        break;
                    }
                    case 4: {
                        exitSystem();
                        break;
                    }
                    default:
                        System.out.println("输入错误！");
                }
            } catch (SQLException e) {
                System.out.print(e.getMessage() + "\n");
            }
        } while (s != 4);
    }
}
