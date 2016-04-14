/*
 * Representa una cara detectada valida 
 */
package Deteccion;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

public class Cara {
    private Mat imagen;
    private Rect OjoDerecho;
    private Rect OjoIzquierdo;
    private int legajo;

    public Cara(Mat imagen, Rect OjoDerecho, Rect OjoIzquierdo) {
        this.imagen = imagen;
        this.OjoDerecho = OjoDerecho;
        this.OjoIzquierdo = OjoIzquierdo;
    }

    public Cara(Mat imagen, int legajo) {
        this.imagen = imagen;
        this.legajo = legajo;
    }

    public Mat getImagen() {
        return imagen;
    }

    public void setImagen(Mat imagen) {
        this.imagen = imagen;
    }

    public Rect getOjoDerecho() {
        return OjoDerecho;
    }

    public void setOjoDerecho(Rect OjoDerecho) {
        this.OjoDerecho = OjoDerecho;
    }

    public Rect getOjoIzquierdo() {
        return OjoIzquierdo;
    }

    public void setOjoIzquierdo(Rect OjoIzquierdo) {
        this.OjoIzquierdo = OjoIzquierdo;
    }

    public int getLegajo() {
        return legajo;
    }

    public void setLegajo(int legajo) {
        this.legajo = legajo;
    }
    
}
