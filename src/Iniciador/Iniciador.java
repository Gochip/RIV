package Iniciador;

import Interfaces.InterfazPrincipal;
import Controladores.CtrlInterfazPrincipal;
import UpperEssential.UpperEssentialLookAndFeel;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 */
public class Iniciador {

    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(new UpperEssentialLookAndFeel("temas//Moderno.Theme"));
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Iniciador.class.getName()).log(Level.SEVERE, null, ex);
        }

        CtrlInterfazPrincipal ctrlInterfazPrincipal;
        InterfazPrincipal interfazPrincipal;

        ctrlInterfazPrincipal = new CtrlInterfazPrincipal();
        interfazPrincipal = new InterfazPrincipal();

        ctrlInterfazPrincipal.setInterfaz(interfazPrincipal);
        interfazPrincipal.setControlador(ctrlInterfazPrincipal);

        interfazPrincipal.setVisible(true);
    }
}
