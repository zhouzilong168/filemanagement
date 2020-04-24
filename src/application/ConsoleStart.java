package application;

import data.DataProcessing;
import domain.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * 控制台端
 * 主程序入口
 */
public class ConsoleStart {

    public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {
        DataProcessing.connectToDatabase();
        String system = "档案系统*********";
        String menu = "请选择菜单：";
        String exit = "系统退出，谢谢使用！";
        String infos = "************欢迎进入" + system + "***\n" + "1.登入\n" + "2.退出\n" + "**********************************\n" + menu;
        System.out.print(infos);
        Scanner input = new Scanner(System.in);
        int s = input.nextInt();
        switch (s) {
            case 1: {
                try {
                    System.out.print("请输入用户名： ");
                    String user = input.next();
                    while (DataProcessing.search(user) == null) {
                        System.out.print("用户名错误！请重新输入：");
                        user = input.next();
                    }
                    System.out.print("请输入密码： ");
                    String password = input.next();
                    while (DataProcessing.searchUser(user, password) == null) {
                        System.out.print("密码错误！请重新输入:");
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
                System.out.print("菜单选择错误，请重启系统！");
        }
    }

}
