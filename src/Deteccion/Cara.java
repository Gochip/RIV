/*
 * Representa una cara detectada valida 
 */
package Deteccion;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import static org.opencv.imgproc.Imgproc.resize;

public class Cara {

    private Mat imagen;
    private Rect OjoDerecho;
    private Rect OjoIzquierdo;
    private int legajo;
    private Point puntoComienzo;

    public Cara(Mat imagen, Rect OjoDerecho, Rect OjoIzquierdo) {
        this.imagen = imagen;
        this.OjoDerecho = OjoDerecho;
        this.OjoIzquierdo = OjoIzquierdo;
        this.ajustarTamaño(new Size(100, 100));
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
        this.ajustarTamaño(new Size(100, 100));
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

    public void ajustarTamaño(Size tamaño) {
        resize(this.imagen, this.imagen, tamaño);
    }

    public Point getPuntoComienzo() {
        return puntoComienzo;
    }

    public void setPuntoComienzo(Point puntoComienzo) {
        this.puntoComienzo = puntoComienzo;
    }
}
