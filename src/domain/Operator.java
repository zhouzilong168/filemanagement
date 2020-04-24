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
            System.out.print("********��ӭ���뵵��¼����Ա�˵�********\n" + "1.�ϴ��ļ�\n" + "2.�����ļ�\n" + "3.�ļ��б�\n" + "4.�޸�����\n" + "5.�˳�\n" + "***********************\n" + "��ѡ��˵���");
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
                        System.out.println("�������");
                }
            } catch (SQLException e) {
                System.out.print(e.getMessage() + "\n");
            }
        } while (s != 5);
    }

    public boolean uploadFile() throws IOException, SQLException {
        Scanner input = new Scanner(System.in);
        System.out.println("�ϴ��ļ�\n" + "�������ļ�·����");
        String path = input.next();
        System.out.println("�������ļ�����");
        String name = input.next();
        System.out.println("�����뵵���ţ�");
        String num = input.next();
        System.out.println("�����뵵��������");
        String description = input.next();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        if (!DataProcessing.insertDoc(num, getName(), timestamp, description, name)) {
            System.out.println("the ID aleady exist! �ϴ�ʧ�ܣ�");
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
                System.out.print("�ϴ��ɹ�\n");
                return true;
            } else {
                System.out.print("�ϴ�ʧ��\n");
                return false;
            }
        }
    }

}
