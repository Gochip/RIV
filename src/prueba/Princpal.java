/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prueba;

import capturador.Capturador;
import java.awt.Image;
import java.util.LinkedList;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import reconocedor.Reconocedor;

/**
 *
 * @author gastr
 */
public class Princpal {

    /**
     * 1324799
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Crea y muestra la ventana
        InterfazCamara VENTANA = new InterfazCamara();
        VENTANA.setVisible(true);

        //Carga las librerias
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        //Asigna la ip a reconocer
        Capturador CAP = new Capturador(0);
        Reconocedor REC = new Reconocedor();

        //     Guardador GUA = new Guardador();
        //While true para estar constantemente capturando las imagenes
        while (true) {

            //Obtengo la imagen de la camara ip
            Mat imagen = CAP.obtenerImagen();

            MatOfRect carasDetectadas = REC.reconocerCara(imagen);
            //Asigno la imagen a un label convirtiendola primero en formato Image

            VENTANA.setImage(REC.convertir(imagen));

            LinkedList<Image> lista = new LinkedList<>();
            for (Rect rect : carasDetectadas.toArray()) {
                lista.add(REC.convertir(imagen.submat(rect)));
            }
            VENTANA.agregarCarasPanel(lista);
//            
//            if (VENTANA.botonTocado) {
//                //Detecta las caras en la imagen
//                LinkedList<Image> imagenesCarasValidas = REC.reconocerCaraValida(imagen, carasDetectadas);
//                
//                System.out.println(imagenesCarasValidas.size());
//                if (!imagenesCarasValidas.isEmpty()) {
//                    VENTANA.agregarCarasPanel(imagenesCarasValidas);
//                }
//                VENTANA.botonTocado=false;
//            }

        }
    }

}
