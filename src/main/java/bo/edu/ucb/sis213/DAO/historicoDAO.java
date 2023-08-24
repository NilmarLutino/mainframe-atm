package bo.edu.ucb.sis213.DAO;

import java.sql.PreparedStatement;
import java.sql.Timestamp;

import bo.edu.ucb.sis213.BI.App;

public class historicoDAO {
    // Método para guardar el historial de operaciones
    public static void guardarHistorico(double cantidad, String tipoOperacion){
        String insertHistoricoQuery = "INSERT INTO historico (usuario_id, tipo_operacion, cantidad, fecha) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement insertHistoricoStatement = conectionDAO.getConnection().prepareStatement(insertHistoricoQuery);
            insertHistoricoStatement.setInt(1, App.usuarioId);
            insertHistoricoStatement.setString(2, tipoOperacion);
            insertHistoricoStatement.setDouble(3, cantidad);
            Timestamp fechaActual = new Timestamp(System.currentTimeMillis());
            insertHistoricoStatement.setTimestamp(4, fechaActual);
            insertHistoricoStatement.executeUpdate();
        } catch (Exception e) {
            System.out.println("Guardado de historico fallido");
            e.printStackTrace();
        }
    }
}
