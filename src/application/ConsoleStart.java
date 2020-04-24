package application;

import data.DataProcessing;
import domain.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * ����̨��
 * ���������
 */
public class ConsoleStart {

    public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {
        DataProcessing.connectToDatabase();
        String system = "����ϵͳ*********";
        String menu = "��ѡ��˵���";
        String exit = "ϵͳ�˳���ллʹ�ã�";
        String infos = "************��ӭ����" + system + "***\n" + "1.����\n" + "2.�˳�\n" + "**********************************\n" + menu;
        System.out.print(infos);
        Scanner input = new Scanner(System.in);
        int s = input.nextInt();
        switch (s) {
            case 1: {
                try {
                    System.out.print("�������û����� ");
                    String user = input.next();
                    while (DataProcessing.search(user) == null) {
                        System.out.print("�û����������������룺");
                        user = input.next();
                    }
                    System.out.print("���������룺 ");
                    String password = input.next();
                    while (DataProcessing.searchUser(user, password) == null) {
                        System.out.print("�����������������:");
                        password = input.next();
                    }
                    User temp = DataProcessing.searchUser(user, password);
                    temp.showMenu();
                } catch (SQLException e) {
                    System.out.print("Error in excecuting Update" + e.getMessage() + "\n");
                } catch (IOException e) {
                    System.out.print(e.getMessage() + "\n");
                }
                break;
            }
            case 2:
                System.out.print(exit);
                break;
            default:
                System.out.print("�˵�ѡ�����������ϵͳ��");
        }
    }

}
