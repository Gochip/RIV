/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controladores;

import Interfaces.InterfazHistorial;

/**
 *
 * @author javie
 */
public class CtrlInterfazHistorial {

    InterfazHistorial interfazHistorial;
    
    public void setInterfaz(InterfazHistorial interfazHistorial) {
    
        this.interfazHistorial = interfazHistorial;
    }

    void abrirInterfazHistorial() {
    
        interfazHistorial = new InterfazHistorial();
        interfazHistorial.setVisible(true);
    }
    
}
