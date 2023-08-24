package bo.edu.ucb.sis213.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import bo.edu.ucb.sis213.BI.App;

public class usuariosDAO {
    // Método para validar las credenciales del usuario
    public static boolean validarCredenciales(String alias, int pin) {
        String query = "SELECT id, nombre, saldo, pin FROM usuarios WHERE alias = ? AND pin = ?";
        try (Connection connection = conectionDAO.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, alias);
            preparedStatement.setInt(2, pin);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                App.usuarioId = resultSet.getInt("id");
                App.nombreUsuario = resultSet.getString("nombre");
                App.saldo = resultSet.getDouble("saldo");
                App.pinActual = resultSet.getInt("pin");
                return true;
            }
        } catch (SQLException e) {
            exceptionDAO.handleSQLException(e);
        }
        return false;
    }

    // Método para actualizar el saldo y guardar el historial
    public static void actualizarSaldo(double cantidad, String tipoOperacion) {
        String query = "UPDATE usuarios SET saldo = ? WHERE id = ?";
        try (Connection connection = conectionDAO.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setDouble(1, App.saldo);
            preparedStatement.setInt(2, App.usuarioId);
            preparedStatement.executeUpdate();
            historicoDAO.guardarHistorico(cantidad, tipoOperacion);
        } catch (SQLException e) {
            exceptionDAO.handleSQLException(e);
        }
    }

    // Método para actualizar el PIN del usuario
    public static void actualizarPIN(int nuevoPin) {
        String query = "UPDATE usuarios SET pin = ? WHERE id = ?";
        try (Connection connection = conectionDAO.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, nuevoPin);
            preparedStatement.setInt(2, App.usuarioId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            exceptionDAO.handleSQLException(e);
        }
    }
}
