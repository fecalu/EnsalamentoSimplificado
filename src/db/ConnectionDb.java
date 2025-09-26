package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDb {

    private static Connection conn = null;

    public static Connection getConn(){
        try {
            if(conn == null) {
                // conn = DriverManager.getConnection("jdbc:postgresql://maglev.proxy.rlwy.net:46549/railway", "postgres", "AgtuiEbNAzEmCGjDoyHDNeFrjEOxthlv");
                conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/railway", "postgres", "aluno");
            }
            return conn;
        } catch (SQLException e){
            throw new RuntimeException("Erro de conexão com banco de dados");
        } catch (Exception e) {
            throw new RuntimeException("Erro desgraçado que eu não sei qual é.");
        }
    }

    public static void closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException("Erro de conexão com banco de dados");
        }
    }

}
