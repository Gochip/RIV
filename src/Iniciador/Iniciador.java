package Iniciador;

import Interfaces.InterfazPrincipal;
import Interfaces.InterfazHistorial;
import Controladores.CtrlInterfazPrincipal;
import Controladores.CtrlInterfazHistorial;
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
            UIManager.setLookAndFeel(new UpperEssentialLookAndFeel("temas//RIV.Theme"));
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Iniciador.class.getName()).log(Level.SEVERE, null, ex);
        }

        CtrlInterfazPrincipal ctrlInterfazPrincipal;
        CtrlInterfazHistorial ctrlInterfazHistorial;
        InterfazPrincipal interfazPrincipal;
        InterfazHistorial interfazHistorial;
        
        ctrlInterfazPrincipal = new CtrlInterfazPrincipal();
        ctrlInterfazHistorial = new CtrlInterfazHistorial();
        interfazPrincipal = new InterfazPrincipal();
        interfazHistorial = new InterfazHistorial();

        ctrlInterfazPrincipal.setInterfaz(interfazPrincipal);
        ctrlInterfazHistorial.setInterfaz(interfazHistorial);
        
        interfazPrincipal.setControlador(ctrlInterfazPrincipal);
        interfazHistorial.setControlador(ctrlInterfazHistorial);
        
        
        interfazPrincipal.setVisible(true);
        interfazPrincipal.setLocationRelativeTo(null);
        
        ctrlInterfazPrincipal.setCtrlInterfazHistorial(ctrlInterfazHistorial);
    }
}
