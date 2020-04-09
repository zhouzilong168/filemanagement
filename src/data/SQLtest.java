package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 数据库连接测试类
 */
public class SQLtest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Connection connection;
		Statement statement;
		ResultSet resultSet;
		String driverName="com.mysql.jdbc.Driver";               // 加载数据库驱动类
	    String url="jdbc:mysql://localhost:3306/document?useSSL=false";       // 声明数据库的URL
	    String user="root";                                      // 数据库用户
	    String password="123456";
	    try{
	    	Class.forName(driverName);
			connection=DriverManager.getConnection(url, user, password);   // 建立数据库连接
			statement = connection.createStatement(
			         ResultSet.TYPE_SCROLL_INSENSITIVE,
			         ResultSet.CONCUR_READ_ONLY );
			String Username="rose",Userpassword="123";
			String sql="select * from user_info where username='"+Username+"'and password='"+Userpassword+"'";
			resultSet = statement.executeQuery(sql);
			while (resultSet.next()){
				String username=resultSet.getString(1);
				String pwd=resultSet.getString(2);
				String role=resultSet.getString(3);
				System.out.println(username+";"+pwd+";"+role);
			}
			resultSet.close();
            statement.close();
            connection.close();
	    }catch(ClassNotFoundException e ){
	    	System.out.println("数据驱动错误");
	    }catch(SQLException e){
	    	System.out.println("数据库错误");
	    	e.printStackTrace();
	    }
	}

}