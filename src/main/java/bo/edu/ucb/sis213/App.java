package bo.edu.ucb.sis213;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class App {
    private static int usuarioId;
    static String nombreUsuario;
    static double saldo;
    static int pinActual;
    static String tipoOperacion;

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 3306;
    private static final String USER = "root";
    private static final String PASSWORD = "123456";
    private static final String DATABASE = "atm";

    // Método para obtener la conexión a la base de datos
    public static Connection getConnection() throws SQLException {
        String jdbcUrl = String.format("jdbc:mysql://%s:%d/%s", HOST, PORT, DATABASE);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found.", e);
        }
        return DriverManager.getConnection(jdbcUrl, USER, PASSWORD);
    }

    // Método para validar las credenciales del usuario
    public static boolean validarCredenciales(String alias, int pin) {
        String query = "SELECT id, nombre, saldo, pin FROM usuarios WHERE alias = ? AND pin = ?";
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, alias);
            preparedStatement.setInt(2, pin);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                usuarioId = resultSet.getInt("id");
                nombreUsuario = resultSet.getString("nombre");
                saldo = resultSet.getDouble("saldo");
                pinActual = resultSet.getInt("pin");
                return true;
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return false;
    }

    // Método para manejar excepciones de SQL
    private static void handleSQLException(SQLException e) {
        System.err.println("Error de SQL:");
        e.printStackTrace();
    }

    // Método para actualizar el saldo y guardar el historial
    public static void actualizarSaldo(double cantidad, String tipoOperacion) {
        String query = "UPDATE usuarios SET saldo = ? WHERE id = ?";
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setDouble(1, saldo);
            preparedStatement.setInt(2, usuarioId);
            preparedStatement.executeUpdate();
            guardarHistorico(cantidad, tipoOperacion);
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    // Método para guardar el historial de operaciones
    public static void guardarHistorico(double cantidad, String tipoOperacion) {
        // ... (similar to your original code)
    }

    // Método para actualizar el PIN del usuario
    public static void actualizarPIN(int nuevoPin) {
        String query = "UPDATE usuarios SET pin = ? WHERE id = ?";
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, nuevoPin);
            preparedStatement.setInt(2, usuarioId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }
}
