/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controladores;

import Deteccion.Capturador;
import Deteccion.Cara;
import Deteccion.Clasificador;
import Deteccion.Persona;
import Deteccion.ReconocedorCara;
import Guardador.Guardador;
import Interfaces.InterfazPrincipal;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.SwingWorker;
import static org.opencv.core.Core.FONT_HERSHEY_PLAIN;
import static org.opencv.core.Core.FONT_HERSHEY_SCRIPT_COMPLEX;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import static org.opencv.imgproc.Imgproc.putText;

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
    private CtrlInterfazNuevaPersona ctrlInterfazNuevaPersona;
    private final Clasificador clasificador;
    private Conectar con;
    private HashMap<Integer, Persona> personas;

    public CtrlInterfazPrincipal() {
        clasificador = Clasificador.getSingletonInstance();
        clasificador.entrenar();
        this.llenarHash();
    }

    private void llenarHash() {
        personas = new HashMap<>();
        Guardador guardador = new Guardador();
        for (Persona persona : guardador.getPersonas()) {
            personas.put(persona.getLegajo(), persona);
        }
    }

    public boolean entrenarClasificador() {
        return clasificador.entrenar();
    }

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
                        this.clasificador.getLegajo(vectorCaras);
                        this.interfazPrincipal.setLblLegajo(vectorCaras.get(0).getLegajo());
                        // putText(img, "OpenCV 2", Point(180,320), FONT_HERSHEY_SCRIPT_COMPLEX, 3, CV_RGB(125,12,145), 2);

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

    public void setControladorInterfazNuevaPersona(CtrlInterfazNuevaPersona ctrlInterfazNuevaPersona) {
        this.ctrlInterfazNuevaPersona = ctrlInterfazNuevaPersona;
    }

    public void nuevaPersona() {
        this.ctrlInterfazNuevaPersona.mostrarInterfaz(true);
    }

    public void conectarCamara(String camara) {
        this.llenarHash();
        try {
            int numeroCamara = Integer.parseInt(camara);
            con = new Conectar(numeroCamara);
            try {
                this.interfazPrincipal.habilitar(false);
                con.execute();
            } catch (Exception ex) {
                this.interfazPrincipal.habilitar(true);
                this.interfazPrincipal.setLblValidacionCamara(ex.toString());
            }
        } catch (NumberFormatException e) {
            this.interfazPrincipal.setLblValidacionCamara("Operacion no soportada");
        }
    }

    public void desconectarCamara() {
        con.setEncendida(false);
        this.interfazPrincipal.habilitar(true);
    }

    private class Conectar extends SwingWorker<Void, Void> {

        private final int nroCamara;
        private boolean encendida;

        public Conectar(int nroCamara) {
            this.nroCamara = nroCamara;
            encendida = true;
        }

        @Override
        protected Void doInBackground() throws Exception {
            //Conecta con la camara
            capturador = new Capturador(nroCamara);
            if (capturador.conectarCamara()) {
                //Crea estructuras para reconocer cara
                reconocedorCara = new ReconocedorCara();
                ArrayList<Cara> vectorCaras;
                Mat imagenMat;
                Persona persona;
                //Comienza la toma de imagenes desde la camara
                while (encendida) {
                    imagenMat = capturador.getImagen();
                    //Mientras la imagen no sea null o este vacia reconoce caras
                    if (imagenMat != null && !imagenMat.empty()) {
                        vectorCaras = reconocedorCara.detectarCaras(imagenMat);
                        //Si reconocia alguna cara trata de identificarla
                        if (vectorCaras.size() >= 1) {

                            //Asigno la imagen al lbl de imagen econtrada valida
                            interfazPrincipal.setLblImagenEncontrada(
                                    convertir(vectorCaras.get(0).getImagen()));
                            //Obtengo los legajos de cada persona detectada
                            clasificador.getLegajo(vectorCaras);
                            //Obtengo la primera persona detectada
                            persona = personas.get(vectorCaras.get(0).getLegajo());
                            //Asigno la primera persona detectada a los lbl 
                            interfazPrincipal.setLblLegajo(persona.getLegajo());
                            interfazPrincipal.setLblNombreApellido(persona.getNombre() + " " + persona.getApellido());
                            for (int i = 0; i < vectorCaras.size(); i++) {
                                persona = personas.get(vectorCaras.get(i).getLegajo());
                                putText(imagenMat,persona.getNombre() + " " + persona.getApellido(),
                                        vectorCaras.get(i).getPuntoComienzo(), FONT_HERSHEY_PLAIN, 1, 
                                        new Scalar(0,255,0), 2);
                            }
                        }
                        interfazPrincipal.setLblImagenCamara(convertir(imagenMat));
                    }
                }

            } else {
                interfazPrincipal.setLblValidacionCamara("No se pudo establecer conexion con la camara");
            }
            return null;
        }

        public void setEncendida(boolean encendida) {
            this.encendida = encendida;
        }
    }
}
