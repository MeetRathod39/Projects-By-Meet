import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;

public class ECOMMERCESYSTEM{
    public static void main(String[] args) throws ClassNotFoundException, SQLException{
        String dburl="jdbc:mysql://localhost:3306/userdata";
        String dbuser="root";
        String dbpass="";            
        String drivername="com.mysql.jdbc.Driver";
        Class.forName(drivername);
        Connection co=DriverManager.getConnection(dburl, dbuser, dbpass);
        if(co!=null){
            System.out.println("CONNECTION SUCESSFULL");
        }                  
        else{
            System.out.println("CONNECTION nottt SUCESSFULL");
        }
    }
}
