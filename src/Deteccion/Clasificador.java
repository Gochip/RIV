/*
 * 
 */
package Deteccion;

import Guardador.Guardador;
import java.util.ArrayList;
import org.opencv.core.Size;
import org.opencv.ml.SVM;


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
        imagenes = conexion.getCarasClasificador(new Size(100,100));
    }
    
    /*
        Entrena una Red para detectar las diferentes caras
    */
    public void entrenar(){
       SVM clasificador;
       
    }
    
}
