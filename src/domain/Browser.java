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
            System.out.print("********��ӭ���뵵�������Ա�˵�********\n" + "1.�����ļ�\n" + "2.�ļ��б�\n" + "3.�޸�����\n" + "4.�˳�\n" + "***********************\n" + "��ѡ��˵���");
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
                        System.out.println("�������");
                }
            } catch (SQLException e) {
                System.out.print(e.getMessage() + "\n");
            }
        } while (s != 4);
    }
}
