package Iniciador;

import Controladores.CtrlInterfazClasificar;
import Controladores.CtrlInterfazHistorial;
import Interfaces.InterfazPrincipal;
import Controladores.CtrlInterfazPrincipal;
import Interfaces.InterfazClasificar;
import Interfaces.InterfazHistorial;
import UpperEssential.UpperEssentialLookAndFeel;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.opencv.core.Core;

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
        
        //Carga la libreria Opencv
         System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
  
        CtrlInterfazPrincipal ctrlInterfazPrincipal;
        InterfazPrincipal interfazPrincipal;
        InterfazClasificar interfazClasificar;
        CtrlInterfazClasificar ctrlInterfazClasificar;
        InterfazHistorial interfazHistorial;
        CtrlInterfazHistorial ctrlInterfazHistorial;

        interfazPrincipal = new InterfazPrincipal();
        ctrlInterfazPrincipal = new CtrlInterfazPrincipal();
        interfazClasificar = new InterfazClasificar();
        ctrlInterfazClasificar = new CtrlInterfazClasificar();
        interfazHistorial = new InterfazHistorial();
        ctrlInterfazHistorial = new CtrlInterfazHistorial();

        interfazPrincipal.setControlador(ctrlInterfazPrincipal);
        ctrlInterfazPrincipal.setInterfaz(interfazPrincipal);
        interfazClasificar.setControlador(ctrlInterfazClasificar);
        ctrlInterfazClasificar.setInterfaz(interfazClasificar);
        interfazHistorial.setControlador(ctrlInterfazHistorial);
        ctrlInterfazClasificar.setInterfaz(interfazClasificar);

        ctrlInterfazPrincipal.setControladorInterfazHistorial(ctrlInterfazHistorial);
        ctrlInterfazPrincipal.setContrladorInterfazClasificar(ctrlInterfazClasificar);
        
        interfazPrincipal.setVisible(true);
        ctrlInterfazPrincipal.iniciarCaptura();
        
    }
}
