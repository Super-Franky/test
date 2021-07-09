import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ConnMySql {
    public static void main(String[] args) throws Exception{
        Class.forName("com.mysql.cj.jdbc.Driver");
        try{
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/dbsclab2021"
                    ,"DaddyChen","chjsh001023");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from prereq");
            while (rs.next()){
                System.out.print(rs.getInt(1));
                System.out.print(" ");
                System.out.println(rs.getInt(2));
            }
            rs.close();
            stmt.close();
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
