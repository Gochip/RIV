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
import java.util.ArrayList;
import org.opencv.core.Mat;

/**
 *
 * @author gastr
 */
public class CtrlInterfazPrincipal {

    private InterfazPrincipal interfazPrincipal;
    private Capturador capturador;
    private ReconocedorCara reconocedorCara;

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
                                reconocedorCara.convertir(vectorCaras.get(0).getImagen()));
                        
                    }
                    this.interfazPrincipal.setLblImagenCamara(reconocedorCara.convertir(imagenMat));
                    
                }
            }

        } else {
            //this.interfazPrincipal.setLabelValidacion("No se pudo establecer conexion con la camara");
        }
    }

}
