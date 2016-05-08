/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controladores;

import Deteccion.Capturador;
import Deteccion.Cara;
import Deteccion.Clasificador;
import Deteccion.ReconocedorCara;
import Guardador.Guardador;
import Interfaces.InterfazClasificar;
import ModeloTablas.ModeloTablaClasificador;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.SwingWorker;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

/**
 *
 * @author gastr
 */
public class CtrlInterfazClasificar {

    private InterfazClasificar interfazClasificar;
    private Capturador capturador;
    private ReconocedorCara reconocedorCara;
    private Guardador guardador;
    private ModeloTablaClasificador modeloTabla;
    private Conectar con;

    public void setInterfaz(InterfazClasificar interfazClasificar) {
        this.interfazClasificar = interfazClasificar;
    }

    void setVisibleInterfaz(boolean b) {
        this.interfazClasificar.setVisible(b);
        this.actualizarTabla();
    }

    public void nuevaPersona(int cantidad, String legajo) {

        if (!"".equals(legajo)) {
            guardador = new Guardador();
            int leg = Integer.valueOf(legajo);
            if (guardador.existeLegajo(leg)) {
                this.guardador = new Guardador();
                con = new Conectar(cantidad, leg);
                con.execute();
                this.actualizarTabla();
            } else {
                this.interfazClasificar.setLabelValidacion("Debe especificar el numero de legajo");
            }
        } else {
            this.interfazClasificar.setLabelValidacion("No existe una persona con ese legajo");
        }
    }

    public void actualizarTabla() {
        modeloTabla = new ModeloTablaClasificador();
        this.interfazClasificar.setModeloTabla(modeloTabla);
    }

    public void eliminarCarasClasificador(int legajo) {
        this.guardador = new Guardador();
        if (this.guardador.eliminarCarasClasificador(legajo)) {
            this.interfazClasificar.setLabelValidacion("Se ha eliminado correctamente");
        } else {
            this.interfazClasificar.setLabelValidacion("No se a podido eliminar el registro");
        }
        this.actualizarTabla();
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

    public void entrenar() {
        Clasificador.getSingletonInstance().entrenar();
    }

    private class Conectar extends SwingWorker<Void, Void> {

        private int cantidad;
        private final int legajo;

        public Conectar(int cantidad, int legajo) {
            this.cantidad = cantidad;
            this.legajo = legajo;
        }

        @Override
        protected Void doInBackground() throws Exception {
            ArrayList<Cara> imagenes = new ArrayList<>();
            //Conecta con la camara
            capturador = new Capturador(0);
            if (capturador.conectarCamara()) {

                if (cantidad == 0) {
                    cantidad = 15;
                    interfazClasificar.setTxtCantidadImagenes("15");
                }

                Mat imagen;
                ArrayList<Cara> aux;
                reconocedorCara = new ReconocedorCara();

                while (imagenes.size() <= cantidad) {
                    imagen = capturador.getImagen();

                    if (imagen != null) {

                        aux = reconocedorCara.detectarCaras(imagen);
                        if (aux.size() == 1) {
                            interfazClasificar.setLblImagenEncontrada(
                                    convertir(aux.get(0).getImagen()));
                            aux.get(0).setLegajo(legajo);
                            imagenes.add(aux.get(0));
                        }
                    }
                    interfazClasificar.setLblImagenCamara(convertir(imagen));
                    interfazClasificar.setLabelValidacion("Se almacenaron : " + imagenes.size());
                }
            } else {
                interfazClasificar.setLabelValidacion("No se pudo conectar con la camara");
            }
            interfazClasificar.setLabelValidacion("Guardando imagenes...");
            guardador.guardarCarasClasificador(imagenes);
            interfazClasificar.setLabelValidacion("Se han guardado los ejemplos correctamente");

            return null;
        }

    }
}
