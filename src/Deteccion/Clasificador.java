/*
 * 
 */
package Deteccion;

import Guardador.Guardador;
import java.util.ArrayList;
import org.opencv.core.Size;

/**
 *
 * @author gastr
 */
public class Clasificador {
    private ArrayList<Cara> imagenes;

    /*
        Obtienes las imagenes para entrenar el clasificador de la base de datos
    */
    public Clasificador() {
        Guardador conexion = new Guardador();
        imagenes = conexion.getCarasClasificador(new Size(30,30));
    }
    
    /*
        Entrena una Red para detectar las diferentes caras
    */
    public void entrenar(){
        
    }
    
}
