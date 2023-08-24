package bo.edu.ucb.sis213.DAO;

import java.sql.SQLException;

public class exceptionDAO {
  // Método para manejar excepciones de SQL
    static void handleSQLException(SQLException e) {
        System.err.println("Error de SQL:");
        e.printStackTrace();
    }  
}
