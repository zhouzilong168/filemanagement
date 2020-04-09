package application;

import data.DataProcessing;
import port.Client;

import java.sql.SQLException;

/**
 * JavaGUI �ͻ��˳������
 */
public class ClientStart {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        DataProcessing.connectToDatabase();
        try {
            Client mt = new Client("127.0.0.1");
            Thread t = new Thread(mt);
            t.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
