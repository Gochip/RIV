package Controladores;

import Deteccion.Persona;
import Guardador.Guardador;
import Interfaces.InterfazNuevaPersona;

/**
 *
 * @author gastr
 */
public class CtrlInterfazNuevaPersona {

    private InterfazNuevaPersona interfazNuevaPersona;
    private Guardador guardador;

    public void setInterfaz(InterfazNuevaPersona interfazNuevaPersona) {
        this.interfazNuevaPersona = interfazNuevaPersona;
    }

    public void nuevaPersona(String legajo, String nombre, String apellido) {
        if (!"".equals(legajo)) {
            if (!"".equals(nombre)) {
                if (!"".equals(apellido)) {
                    Persona persona = new Persona(Integer.valueOf(legajo),nombre,apellido);
                    guardador = new Guardador();
                    guardador.insertarPersona(persona);
                } else {
                    this.interfazNuevaPersona.setLabelValidacion("El apellido no puede estar vacio");
                }
            } else {
                this.interfazNuevaPersona.setLabelValidacion("El nombre no puede estar vacio");
            }
        } else {
            this.interfazNuevaPersona.setLabelValidacion("El legajo no puede estar vacio");
        }
    }
}
