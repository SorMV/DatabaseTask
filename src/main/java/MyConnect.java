import java.sql.*;

public class MyConnect {
    private static Connection connection=null;


    public static void setConnection(Connection connection) {
        MyConnect.connection = connection;
    }

    public static Connection getConnection() {
        return connection;
    }

}
