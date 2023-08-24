package bo.edu.ucb.sis213.BI;
import javax.swing.SwingUtilities;

import bo.edu.ucb.sis213.DAO.conectionDAO;
import bo.edu.ucb.sis213.VIEW.ATMApp;

public class App {
    public static int usuarioId;
    public static String nombreUsuario;
    public static double saldo;
    public static int pinActual;
    public static String tipoOperacion;
    static conectionDAO conectionBBDD = new conectionDAO();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ATMApp().setVisible(true);
            }
        });
    } 
}
