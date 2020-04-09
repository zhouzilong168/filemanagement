package application;

import data.DataProcessing;
import port.Server;

import java.io.IOException;

/**
 * java GUI 服务端程序入口
 **/
public class ServerStart {
    public static void main(String[] args) {
        try {
            DataProcessing.connectToDatabase();
            new Server(); // create server
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
