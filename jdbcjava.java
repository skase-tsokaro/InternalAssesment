import java.sql.Connection;
import java.sql.DriverManager;

public class jdbcjava {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/InsuranceDB";
        String user = "root";
        String password = "password123";


        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected to the database successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}