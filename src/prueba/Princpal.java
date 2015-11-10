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
import reconocedor.Reconocedor;

/**
 *
 * @author gastr
 */
public class Princpal {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Crea y muestra la ventana
        InterfazCamara VENTANA = new InterfazCamara();
        VENTANA.setVisible(true);

        //Carga las librerias
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        //Asigna la ip a reconocer
        String ipCamara = "192.168.0.17:8080";
        Capturador CAP = new Capturador(ipCamara);
        Reconocedor REC = new Reconocedor();

        //While true para estar constantemente capturando las imagenes
        while (true) {

            //Obtengo la imagen de la camara ip
            Mat imagen = CAP.obtenerImagenIpCamara();
            //Mat imagen = CAP.obtenerImagen();

            //Asigno la imagen a un label convirtiendola primero en formato Image
            VENTANA.setImage(REC.convertir(imagen));

            //Detecta las caras en la imagen
            LinkedList<Image> carasDetectadas = REC.reconocerCara(imagen);
            //Asigna la primera cara reconocida al otro label si es que encontro alguna
            if (!carasDetectadas.isEmpty()) {
                VENTANA.setImage2(carasDetectadas.getFirst());
            }
        }

    }

}
