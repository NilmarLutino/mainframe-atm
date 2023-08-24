 package bo.edu.ucb.sis213.VIEW;

import javax.swing.*;

import bo.edu.ucb.sis213.BI.App;
import bo.edu.ucb.sis213.DAO.usuariosDAO;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ATMApp extends JFrame {
    private JPanel loginPanel, menuPanel;
    private JTextField userField;
    private JPasswordField pinField;
    private String nombreUsuarioActual;
    private int intentosRestantes = 3;
    private boolean cuentaBloqueada = false;
    static String tipoOperacion;


    // Método principal para iniciar la aplicación
    
    // Constructor de la clase ATMApp
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

        // Manejo de la acción del botón de inicio de sesión
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
                if (usuariosDAO.validarCredenciales(usuario, pin)) {
                    nombreUsuarioActual = App.nombreUsuario;
                    dispose();
                    SwingUtilities.invokeLater(() -> {
                        menuView menuView = new menuView();
                        menuView.setVisible(true); // Muestra la ventana del menú
                    });
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
        // Agregar el panel de inicio de sesión a la ventana
        add(loginPanel);
    }
    
}
