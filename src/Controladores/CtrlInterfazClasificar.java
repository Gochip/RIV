/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controladores;

import Deteccion.Capturador;
import Deteccion.Cara;
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
                capturador = new Capturador(0);
                if (capturador.conectarCamara()) {

                    if (cantidad == 0) {
                        cantidad = 15;
                        this.interfazClasificar.setTxtCantidadImagenes("15");
                    }

                    Mat imagen;
                    ArrayList<Cara> imagenes = new ArrayList<>();
                    ArrayList<Cara> aux;
                    reconocedorCara = new ReconocedorCara();
                    int i = 0;

                    while (imagenes.size() <= cantidad) {
                        imagen = capturador.getImagen();

                        if (imagen != null) {

                            aux = reconocedorCara.detectarCaras(imagen);
                            if (aux.size() == 1) {
                                i++;
                                aux.get(0).setLegajo(leg);
                                imagenes.add(aux.get(0));
                                this.interfazClasificar.setLabelValidacion(Integer.toString(i));
                            }
                        }
                    }
                    this.guardador = new Guardador();
                    this.guardador.guardarCarasClasificador(imagenes);
                    this.actualizarTabla();
                    this.interfazClasificar.setLabelValidacion("Se han guardado los ejemplos correctamente");
                } else {
                    this.interfazClasificar.setLabelValidacion("No se pudo conectar con la camara");
                }
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
}
