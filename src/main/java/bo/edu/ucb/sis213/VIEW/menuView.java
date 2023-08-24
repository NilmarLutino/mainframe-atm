 package bo.edu.ucb.sis213.VIEW;

import javax.swing.*;

import bo.edu.ucb.sis213.BI.App;
import bo.edu.ucb.sis213.DAO.usuariosDAO;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class menuView extends JFrame {
    private JPanel loginPanel, menuPanel;
    private JTextField userField;
    private JPasswordField pinField;
    private String nombreUsuarioActual=App.nombreUsuario;
    private int intentosRestantes = 3;
    private boolean cuentaBloqueada = false;
    static String tipoOperacion;


    // Método principal para iniciar la aplicación
    
    // Constructor de la clase ATMApp
    public menuView() {
        setTitle("ATM App");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        loginPanel = new JPanel();
        menuPanel = new JPanel();

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
    // Consultar saldo
    public void consultarSaldo() {
        JOptionPane.showMessageDialog(this, "Su saldo actual es: $" + App.saldo, "Consultar Saldo", JOptionPane.INFORMATION_MESSAGE);
    }
    // Realizar depósito
    public void realizarDeposito() {
        String cantidadStr = JOptionPane.showInputDialog(this, "Ingrese la cantidad a depositar:", "Realizar Depósito", JOptionPane.PLAIN_MESSAGE);
        if (cantidadStr != null) {
            try {
                double cantidad = Double.parseDouble(cantidadStr);
                if (cantidad > 0) {
                    App.saldo += cantidad;
                    tipoOperacion = "deposito";
                    usuariosDAO.actualizarSaldo(cantidad,tipoOperacion);
                    JOptionPane.showMessageDialog(this, "Depósito realizado con éxito. Su nuevo saldo es: $" + App.saldo, "Realizar Depósito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Cantidad no válida.", "Realizar Depósito", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingrese un número válido.", "Realizar Depósito", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    // Realizar retiro
    public void realizarRetiro() {
        String cantidadStr = JOptionPane.showInputDialog(this, "Ingrese la cantidad a retirar:", "Realizar Retiro", JOptionPane.PLAIN_MESSAGE);
        if (cantidadStr != null) {
            try {
                double cantidad = Double.parseDouble(cantidadStr);
                if (cantidad > 0) {
                    if (cantidad <= App.saldo) {
                        App.saldo -= cantidad;
                        tipoOperacion = "retiro";
                        usuariosDAO.actualizarSaldo(cantidad,tipoOperacion);
                        JOptionPane.showMessageDialog(this, "Retiro realizado con éxito. Su nuevo saldo es: $" + App.saldo, "Realizar Retiro", JOptionPane.INFORMATION_MESSAGE);
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
    // Cambiar PIN
    public void cambiarPIN() {
        String pinActualStr = JOptionPane.showInputDialog(this, "Ingrese su PIN actual:", "Cambiar PIN", JOptionPane.PLAIN_MESSAGE);
        if (pinActualStr != null) {
            try {
                int pinIngresado = Integer.parseInt(pinActualStr);
                if (pinIngresado == App.pinActual) {
                    String nuevoPinStr = JOptionPane.showInputDialog(this, "Ingrese su nuevo PIN:", "Cambiar PIN", JOptionPane.PLAIN_MESSAGE);
                    String confirmacionPinStr = JOptionPane.showInputDialog(this, "Confirme su nuevo PIN:", "Cambiar PIN", JOptionPane.PLAIN_MESSAGE);

                    if (nuevoPinStr != null && confirmacionPinStr != null) {
                        try {
                            int nuevoPin = Integer.parseInt(nuevoPinStr);
                            int confirmacionPin = Integer.parseInt(confirmacionPinStr);
                            if (nuevoPin == confirmacionPin) {
                                App.pinActual = nuevoPin;
                                usuariosDAO.actualizarPIN(App.pinActual);
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
        // Agregar el panel de inicio de sesión a la ventana
        add(menuPanel);
    }
}
