package data;


import domain.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 连接数据库
 * 处理User和Doc数据
 * 增删改查
 */
public class DataProcessing {

    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement preparedStatement;
    private static ResultSet resultSet;
    private static boolean connectedToDatabase = false;

    /**
     * 模拟数据库错误
     */
    public static void Init() {
        if (Math.random() > 0.2)
            connectedToDatabase = true;
        else
            connectedToDatabase = false;
    }

    /**
     * 连接到数据库
     */
    public static void connectToDatabase() {
        String driverName = "com.mysql.jdbc.Driver";               // 加载数据库驱动类
        String url = "jdbc:mysql://localhost:3306/document?useSSL=false";       // 声明数据库的URL
        String user = "root";                                      // 数据库用户
        String password = "123456";
        try {
            Class.forName(driverName);
            connection = DriverManager.getConnection(url, user, password);   // 建立数据库连接
            connectedToDatabase = true;
        } catch (ClassNotFoundException e) {
            System.out.println("数据驱动错误");
        } catch (SQLException e) {
            System.out.println("数据库错误");
            e.printStackTrace();
        }

    }

    /**
     * 断开连接
     */
    public static void disconnectFromData() {
        if (connectedToDatabase) {
            try {
                resultSet.close();
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                connectedToDatabase = false;
            }
        }
    }

    // User用户数据库处理=======================================start
    public static User search(String Username) throws SQLException {
        User temp = null;
        if (!connectedToDatabase) {
            throw new SQLException("Not connected to Database");
        }
        statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        String sql = "select * from user_info where username='" + Username + "'";
        resultSet = statement.executeQuery(sql);
        if (resultSet.next()) {
            String name = resultSet.getString(1);
            String password = resultSet.getString(2);
            String role = resultSet.getString(3);
            if (role.equals("Administator")) {
                temp = new Administrator(name, password, role);
            } else if (role.equals("Operator")) {
                temp = new Operator(name, password, role);
            } else {
                temp = new Browser(name, password, role);
            }
        }
        return temp;
    }

    public static User searchUser(String name, String password) throws SQLException {
        User temp = null;
        if (!connectedToDatabase) {
            throw new SQLException("Not connected to Database");
        }
        String sql = "select * from user_info where username='" + name + "' and password='" + password + "'";
        statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        resultSet = statement.executeQuery(sql);
        if (resultSet.next()) {
            String userName = resultSet.getString("username");
            String userPassword = resultSet.getString("password");
            String userRole = resultSet.getString("role");
            if (userRole.equals("administrator")) {
                return new Administrator(userName, userPassword, userRole);
            } else if (userRole.equals("operator")) {
                return new Operator(userName, userPassword, userRole);
            } else {
                return new Browser(userName, userPassword, userRole);
            }
        }
        return null;
    }

    public static boolean insertUser(String name, String password, String role) throws SQLException {
        if (!connectedToDatabase) {
            throw new SQLException("Not connected to Database");
        }
        String sql = "insert into user_info "
                + "values('" + name + "','" + password + "','" + role + "')";
        preparedStatement = connection.prepareStatement(sql);
        int temp = preparedStatement.executeUpdate(sql);
        if (temp > 0) {
            System.out.println("添加用户成功！");
            return true;
        }
        return false;
    }

    public static boolean updateUser(String name, String password, String role) throws SQLException {
        if (!connectedToDatabase) {
            throw new SQLException("Not connected to Database");
        }
        String sql = "update user_info set password='" + password + "',role='" + role +
                "' where username='" + name + "'";
        preparedStatement = connection.prepareStatement(sql);
        int temp = preparedStatement.executeUpdate();
        if (temp > 0) {
            return true;
        }
        return false;
    }

    public static boolean deleteUser(String name) throws SQLException {
        if (!connectedToDatabase) {
            throw new SQLException("Not connected to Database");
        }
        String sql = "delete from user_info where username='" + name + "'";
        preparedStatement = connection.prepareStatement(sql);
        int temp = preparedStatement.executeUpdate();
        if (temp > 0) {
            return true;
        }
        return false;
    }

    public static List<User> getAllUser() throws SQLException {
        if (!connectedToDatabase) {
            throw new SQLException("Not connected to Database");
        }
        String sql = "select * from user_info";
        statement = connection.createStatement();
        resultSet = statement.executeQuery(sql);
        List<User> users = new ArrayList<>();
        while (resultSet.next()) {
            String temp = resultSet.getString(3);
            switch (temp.compareTo("browser")) {
                case 0:
                    users.add(new Browser(resultSet.getString(1),
                            resultSet.getString(2), resultSet.getString(3)));
                    break;
                case -1:
                    users.add(new Administrator(resultSet.getString(1),
                            resultSet.getString(2), resultSet.getString(3)));
                    break;
                default:
                    users.add(new Operator(resultSet.getString(1),
                            resultSet.getString(2), resultSet.getString(3)));
                    break;
            }
        }
        return users;
    }
    // User用户数据库处理=============================================End

    // Doc文件数据库处理=============================================Start
    public static List<Doc> getAllDocs() throws SQLException {
        if (!connectedToDatabase) {
            throw new SQLException("Not connected to Database");
        }
        String sql = "select * from doc_info";
        statement = connection.createStatement();
        resultSet = statement.executeQuery(sql);
        List<Doc> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(new Doc(resultSet.getString(1), resultSet.getString(2),
                    resultSet.getTimestamp(3), resultSet.getString(4), resultSet.getString(5)));
        }
        return list;
    }

    public static Doc searchDoc(String DocID) throws SQLException {
        Doc temp = null;
        if (!connectedToDatabase) {
            throw new SQLException("Not connected to Database");
        }
        statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        String sql = "select * from doc_info where Id='" + DocID + "'";
        resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            String ID = resultSet.getString("Id");
            String creator = resultSet.getString("creator");
            Timestamp timestamp = resultSet.getTimestamp("timestamp");
            String description = resultSet.getString("description");
            String filename = resultSet.getString("filename");
            temp = new Doc(ID, creator, timestamp, description, filename);
        }
        return temp;
    }

    public static boolean insertDoc(String ID, String creator, Timestamp timestamp,
                                    String description, String filename) throws SQLException {
        if (!connectedToDatabase) {
            throw new SQLException("Not connected to Database");
        }
        timestamp = new Timestamp(System.currentTimeMillis());
        String sql = "insert into doc_info(Id,creator,timestamp,description,filename)"
                + " values('" + ID + "','" + creator + "','" + timestamp + "','" + description + "','" + filename + "')";
        preparedStatement = connection.prepareStatement(sql);
        int temp = preparedStatement.executeUpdate(sql);
        if (temp > 0) {
            return true;
        }
        return false;
    }
    // Doc文件数据库处理=================================================END
}