package bo.edu.ucb.sis213;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

public class ATMApp extends JFrame {
    private JPanel loginPanel, menuPanel;
    private JTextField userField;
    private JPasswordField pinField;
    private int usuarioId;
    private String nombreUsuario;
    private String nombreUsuarioActual;
    private int intentosRestantes = 3;
    private boolean cuentaBloqueada = false;
    private double saldo;
    private int pinActual;
    private String tipoOperacion;

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 3306;
    private static final String USER = "root";
    private static final String PASSWORD = "123456";
    private static final String DATABASE = "atm";

    public Connection getConnection() throws SQLException {
        String jdbcUrl = String.format("jdbc:mysql://%s:%d/%s", HOST, PORT, DATABASE);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("MySQL Driver not found.", e);
        }
        return DriverManager.getConnection(jdbcUrl, USER, PASSWORD);
    }

    public boolean validarCredenciales(String alias, int pin) {
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
            e.printStackTrace();
        }
        return false;
    }

    public void mostrarMenu() {

        JLabel welcomeLabel = new JLabel("Bienvenido, " + nombreUsuarioActual + "!");
        menuPanel.add(welcomeLabel);

        JButton consultButton = new JButton("Consultar Saldo");
        JButton depositButton = new JButton("Realizar Depósito");
        JButton withdrawButton = new JButton("Realizar Retiro");
        JButton changePinButton = new JButton("Cambiar PIN");
        JButton exitButton = new JButton("Salir");

        menuPanel.setLayout(new GridLayout(5, 1));
        menuPanel.add(consultButton);
        menuPanel.add(depositButton);
        menuPanel.add(withdrawButton);
        menuPanel.add(changePinButton);
        menuPanel.add(exitButton);

        consultButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                consultarSaldo();
            }
        });

        depositButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                realizarDeposito();
            }
        });

        withdrawButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                realizarRetiro();
            }
        });

        changePinButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cambiarPIN();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        getContentPane().removeAll();
        add(menuPanel);
        revalidate();
        repaint();
    }

    public void consultarSaldo() {
        JOptionPane.showMessageDialog(this, "Su saldo actual es: $" + saldo, "Consultar Saldo", JOptionPane.INFORMATION_MESSAGE);
    }

    public void realizarDeposito() {
        String cantidadStr = JOptionPane.showInputDialog(this, "Ingrese la cantidad a depositar:", "Realizar Depósito", JOptionPane.PLAIN_MESSAGE);
        if (cantidadStr != null) {
            try {
                double cantidad = Double.parseDouble(cantidadStr);
                if (cantidad > 0) {
                    saldo += cantidad;
                    tipoOperacion = "deposito";
                    actualizarSaldo(cantidad);
                    JOptionPane.showMessageDialog(this, "Depósito realizado con éxito. Su nuevo saldo es: $" + saldo, "Realizar Depósito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Cantidad no válida.", "Realizar Depósito", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingrese un número válido.", "Realizar Depósito", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void realizarRetiro() {
        String cantidadStr = JOptionPane.showInputDialog(this, "Ingrese la cantidad a retirar:", "Realizar Retiro", JOptionPane.PLAIN_MESSAGE);
        if (cantidadStr != null) {
            try {
                double cantidad = Double.parseDouble(cantidadStr);
                if (cantidad > 0) {
                    if (cantidad <= saldo) {
                        saldo -= cantidad;
                        tipoOperacion = "retiro";
                        actualizarSaldo(cantidad);
                        JOptionPane.showMessageDialog(this, "Retiro realizado con éxito. Su nuevo saldo es: $" + saldo, "Realizar Retiro", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Saldo insuficiente.", "Realizar Retiro", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Cantidad no válida.", "Realizar Retiro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingrese un número válido.", "Realizar Retiro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    

    public void actualizarSaldo(double cantidad) {
        String query = "UPDATE usuarios SET saldo = ? WHERE id = ?";
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setDouble(1, saldo);
            preparedStatement.setInt(2, usuarioId);
            preparedStatement.executeUpdate();
            guardarHistorico(cantidad);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void guardarHistorico(double cantidad) {
        String insertHistoricoQuery = "INSERT INTO historico (usuario_id, tipo_operacion, cantidad, fecha) VALUES (?, ?, ?, ?)";
        try (Connection connection = getConnection()) {
            PreparedStatement insertHistoricoStatement = connection.prepareStatement(insertHistoricoQuery);
            insertHistoricoStatement.setInt(1, usuarioId);
            insertHistoricoStatement.setString(2, tipoOperacion);
            insertHistoricoStatement.setDouble(3, cantidad);
            Timestamp fechaActual = new Timestamp(System.currentTimeMillis());
            insertHistoricoStatement.setTimestamp(4, fechaActual);
            insertHistoricoStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Guardado de historico fallido");
            e.printStackTrace();
        }
    }

    public void cambiarPIN() {
        String pinActualStr = JOptionPane.showInputDialog(this, "Ingrese su PIN actual:", "Cambiar PIN", JOptionPane.PLAIN_MESSAGE);
        if (pinActualStr != null) {
            try {
                int pinIngresado = Integer.parseInt(pinActualStr);
                if (pinIngresado == pinActual) {
                    String nuevoPinStr = JOptionPane.showInputDialog(this, "Ingrese su nuevo PIN:", "Cambiar PIN", JOptionPane.PLAIN_MESSAGE);
                    String confirmacionPinStr = JOptionPane.showInputDialog(this, "Confirme su nuevo PIN:", "Cambiar PIN", JOptionPane.PLAIN_MESSAGE);

                    if (nuevoPinStr != null && confirmacionPinStr != null) {
                        try {
                            int nuevoPin = Integer.parseInt(nuevoPinStr);
                            int confirmacionPin = Integer.parseInt(confirmacionPinStr);
                            if (nuevoPin == confirmacionPin) {
                                pinActual = nuevoPin;
                                actualizarPIN(pinActual);
                                JOptionPane.showMessageDialog(this, "PIN actualizado con éxito.", "Cambiar PIN", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(this, "Los PINs no coinciden.", "Cambiar PIN", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(this, "Ingrese números válidos.", "Cambiar PIN", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "PIN incorrecto.", "Cambiar PIN", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingrese un número válido.", "Cambiar PIN", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void actualizarPIN(int nuevoPin) {
        String query = "UPDATE usuarios SET pin = ? WHERE id = ?";
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, nuevoPin);
            preparedStatement.setInt(2, usuarioId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    

    public ATMApp() {
        setTitle("ATM App");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        loginPanel = new JPanel();
        menuPanel = new JPanel();

        JLabel userLabel = new JLabel("Usuario:");
        JLabel pinLabel = new JLabel("PIN:");
        userField = new JTextField(15);
        pinField = new JPasswordField(15);
        JButton loginButton = new JButton("Ingresar");

        loginPanel.setLayout(new GridLayout(3, 2));
        loginPanel.add(userLabel);
        loginPanel.add(userField);
        loginPanel.add(pinLabel);
        loginPanel.add(pinField);
        loginPanel.add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (cuentaBloqueada) {
                    JOptionPane.showMessageDialog(ATMApp.this, "La cuenta está bloqueada. Por favor, contacte al banco.", "Ingresar", JOptionPane.ERROR_MESSAGE);
                    return;
                }
        
                String usuario = userField.getText();
                char[] pinChars = pinField.getPassword();
                String pinString = new String(pinChars);
                int pin = Integer.parseInt(pinString);
        
                if (validarCredenciales(usuario, pin)) {
                    nombreUsuarioActual = nombreUsuario;
                    mostrarMenu();
                } else {
                    intentosRestantes--;
                    if (intentosRestantes > 0) {
                        JOptionPane.showMessageDialog(ATMApp.this, "Credenciales incorrectas. Le quedan " + intentosRestantes + " intentos.", "Ingresar", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(ATMApp.this, "Credenciales incorrectas. Ha excedido el número de intentos. La cuenta ha sido bloqueada.", "Ingresar", JOptionPane.ERROR_MESSAGE);
                        cuentaBloqueada = true;
                        JOptionPane.showMessageDialog(ATMApp.this, "La aplicación se cerrará.", "Cerrando", JOptionPane.INFORMATION_MESSAGE);
                        System.exit(0);
                    }
                }
            }
        });
        

        add(loginPanel);
    }

    private void showMenuPanel() {
        getContentPane().removeAll();
        add(menuPanel);
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ATMApp().setVisible(true);
            }
        });
    }
}
