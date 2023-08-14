package bo.edu.ucb.sis213;

import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

public class App {
    private static int usuarioId;
    private static String nombreUsuario;
    private static double saldo;
    private static int pinActual;
    private static String tipoOperacion;

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 3306;
    private static final String USER = "root";
    private static final String PASSWORD = "123456";
    private static final String DATABASE = "atm";

    public static Connection getConnection() throws SQLException {
        String jdbcUrl = String.format("jdbc:mysql://%s:%d/%s", HOST, PORT, DATABASE);
        try {
            // Asegúrate de tener el driver de MySQL agregado en tu proyecto
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("MySQL Driver not found.", e);
        }

        return DriverManager.getConnection(jdbcUrl, USER, PASSWORD);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int intentos = 3;

        System.out.println("Bienvenido al Cajero Automático.");

        Connection connection = null;
        try {
            connection = getConnection(); // Reemplaza esto con tu conexión real
        } catch (SQLException ex) {
            System.err.println("No se puede conectar a Base de Datos");
            ex.printStackTrace();
            System.exit(1);
        }
        

        while (intentos > 0) {
            System.out.print("Ingrese su PIN de 4 dígitos: ");
            int pinIngresado = scanner.nextInt();
            if (validarPIN(connection, pinIngresado)) {
                pinActual = pinIngresado;
                mostrarMenu();
                break;
            } else {
                intentos--;
                if (intentos > 0) {
                    System.out.println("PIN incorrecto. Le quedan " + intentos + " intentos.");
                } else {
                    System.out.println("PIN incorrecto. Ha excedido el número de intentos.");
                    System.exit(0);
                }
            }
        }
    }

    public static boolean validarPIN(Connection connection, int pin) {
        String query = "SELECT id,nombre, saldo FROM usuarios WHERE pin = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, pin);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                usuarioId = resultSet.getInt("id");
                nombreUsuario = resultSet.getString("nombre");
                saldo = resultSet.getDouble("saldo");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void mostrarMenu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Bienvenido: "+nombreUsuario);
            System.out.println("\nMenú Principal:");
            System.out.println("1. Consultar saldo.");
            System.out.println("2. Realizar un depósito.");
            System.out.println("3. Realizar un retiro.");
            System.out.println("4. Cambiar PIN.");
            System.out.println("5. Salir.");
            System.out.print("Seleccione una opción: ");
            int opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    consultarSaldo();
                    break;
                case 2:
                    realizarDeposito();
                    break;
                case 3:
                    realizarRetiro();
                    break;
                case 4:
                    cambiarPIN();
                    break;
                case 5:
                    System.out.println("Gracias por usar el cajero. ¡Hasta luego!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
            }
        }
    }

    public static void consultarSaldo() {
        System.out.println("Su saldo actual es: $" + saldo);
    }

    public static void realizarDeposito() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese la cantidad a depositar: $");
        double cantidad = scanner.nextDouble();
        tipoOperacion="Deposito";

        if (cantidad <= 0) {
            System.out.println("Cantidad no válida.");
        } else {

            saldo += cantidad;
            String query = "UPDATE usuarios SET saldo = ? WHERE id = ?";
            try {
                PreparedStatement preparedStatement = getConnection().prepareStatement(query);
                preparedStatement.setDouble(1, saldo);
                preparedStatement.setInt(2, usuarioId);
                preparedStatement.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
            guardarHistorico(cantidad);
            System.out.println("Depósito realizado con éxito. Su nuevo saldo es: $" + saldo);
            
        }
    }

    public static void guardarHistorico(double cantidad ){
        String insertHistoricoQuery = "INSERT INTO historico (usuario_id, tipo_operacion, cantidad, fecha) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement insertHistoricoStatement = getConnection().prepareStatement(insertHistoricoQuery);
            insertHistoricoStatement.setInt(1, usuarioId);
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

    public static void realizarRetiro() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese la cantidad a retirar: $");
        double cantidad = scanner.nextDouble();
        tipoOperacion="Retiro";

        if (cantidad <= 0) {
            System.out.println("Cantidad no válida.");
        } else if (cantidad > saldo) {
            System.out.println("Saldo insuficiente.");
        } else {
            saldo -= cantidad;
            //uptade saldo in bd
            String query = "UPDATE usuarios SET saldo = ? WHERE id = ?";
            try {
                PreparedStatement preparedStatement = getConnection().prepareStatement(query);
                preparedStatement.setDouble(1, saldo);
                preparedStatement.setInt(2, usuarioId);
                preparedStatement.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
            guardarHistorico(cantidad);
            System.out.println("Retiro realizado con éxito. Su nuevo saldo es: $" + saldo);
        }
    }

    public static void cambiarPIN() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese su PIN actual: ");
        int pinIngresado = scanner.nextInt();
        int nuevoPin=3,confirmacionPin=0;
        if (pinIngresado == pinActual) {
            do{
                System.out.println("Ingrese 0 para volver al menú");
                System.out.print("Ingrese su nuevo PIN: ");
                nuevoPin = scanner.nextInt();
                if(nuevoPin!=0){
                    System.out.print("Confirme su nuevo PIN: ");
                    confirmacionPin = scanner.nextInt();

                    if (nuevoPin == confirmacionPin) {
                        pinActual = nuevoPin;
                        //update pin in bd
                        String query = "UPDATE usuarios SET pin = ? WHERE id = ?";
                        try {
                            PreparedStatement preparedStatement = getConnection().prepareStatement(query);
                            preparedStatement.setInt(1, pinActual);
                            preparedStatement.setInt(2, usuarioId);
                            preparedStatement.executeUpdate();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.out.println("PIN actualizado con éxito.");
                    } else {
                        System.out.println("Los PINs no coinciden.");
                    }
                }
                else{
                    System.out.println("Volviendo al menú principal");
                }
            }while(nuevoPin!=confirmacionPin);
        } else {
            System.out.println("PIN incorrecto.");
        }
    }
}
