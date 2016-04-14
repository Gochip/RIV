/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controladores;

import Deteccion.Capturador;
import Deteccion.Cara;
import Deteccion.ReconocedorCara;
import Interfaces.InterfazPrincipal;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

/**
 *
 * @author gastr
 */
public class CtrlInterfazPrincipal {

    private InterfazPrincipal interfazPrincipal;
    private Capturador capturador;
    private ReconocedorCara reconocedorCara;
    private CtrlInterfazHistorial ctrlInterfazHistorial;
    private CtrlInterfazClasificar ctrlInterfazClasificar;

    public void setInterfaz(InterfazPrincipal interfazPrincipal) {
        this.interfazPrincipal = interfazPrincipal;
    }

    public void iniciarCaptura() {
        capturador = new Capturador(0);
        if (capturador.conectarCamara()) {
            reconocedorCara = new ReconocedorCara();
            ArrayList<Cara> vectorCaras;
            Mat imagenMat;
            while (true) {
                imagenMat = capturador.getImagen();
                if (imagenMat != null && !imagenMat.empty()) {
                    vectorCaras = reconocedorCara.detectarCaras(imagenMat);
                    if (vectorCaras.size() >= 1) {
                        this.interfazPrincipal.setLblImagenEncontrada(
                                convertir(vectorCaras.get(0).getImagen()));

                    }
                    this.interfazPrincipal.setLblImagenCamara(convertir(imagenMat));

                }
            }

        } else {
            //this.interfazPrincipal.setLabelValidacion("No se pudo establecer conexion con la camara");
        }
    }

    /*
        Convierte una imagen Mat(formato de opencv) a Image(formato de java)
     */
    private Image convertir(Mat imagen) {
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", imagen, matOfByte);

        byte[] byteArray = matOfByte.toArray();
        BufferedImage bufImage = null;

        try {

            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);
        } catch (Exception e) {
            System.out.println(e);
        }
        return (Image) bufImage;
    }

    public void setControladorInterfazHistorial(CtrlInterfazHistorial ctrlInterfazHistorial) {
        this.ctrlInterfazHistorial = ctrlInterfazHistorial;
    }

    public void setContrladorInterfazClasificar(CtrlInterfazClasificar ctrlInterfazClasificar) {
        this.ctrlInterfazClasificar = ctrlInterfazClasificar;
    }

    public void setVisibleInterfazHistorial(boolean b) {
        this.ctrlInterfazHistorial.setVisibleInterfaz(b);
    }

    public void setVisibleInterfazClasificar(boolean b) {
        this.ctrlInterfazClasificar.setVisibleInterfaz(b);
    }

}
