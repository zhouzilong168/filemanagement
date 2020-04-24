package application;

import data.DataProcessing;
import port.Client;

import java.sql.SQLException;

/**
 * JavaGUI 客户端程序入口
 */
public class ClientStart {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        DataProcessing.connectToDatabase();
        //int count = 1;
        int count = 3;
        try {
            for (int i = 0; i < count; i++) {
                new Thread(new Client("127.0.0.1",12345)).start();
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
