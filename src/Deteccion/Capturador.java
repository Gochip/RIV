package Deteccion;

import java.util.LinkedList;
import static org.opencv.core.Core.subtract;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import static org.opencv.imgproc.Imgproc.MORPH_OPEN;
import static org.opencv.imgproc.Imgproc.MORPH_RECT;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.contourArea;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.findContours;
import static org.opencv.imgproc.Imgproc.getStructuringElement;
import static org.opencv.imgproc.Imgproc.morphologyEx;
import static org.opencv.imgproc.Imgproc.threshold;
import org.opencv.videoio.VideoCapture;

/**
 * Esta clase hace la captura de datos a través de una cámara IP o una camara
 * consectada al PC.
 *
// */

public class Capturador {

    private int nroCamara;
    private String ipCamara;
    private VideoCapture cap;
    private Mat imagen;


    /*
     * Crea un capturador pasandole un string con la ip de la camara
     */
    public Capturador(String ipCamara) {
        this.imagen = new Mat();
        this.ipCamara = "http://" + ipCamara + "/video";
    }

    /*
     * Crea un capturador pasandole un int con el numero de la camara
     */
    public Capturador(int nroCamara) {
        this.imagen = new Mat();
        this.nroCamara = nroCamara;
        cap = new VideoCapture(nroCamara);
    }

    /*
     * Intenta abrir una conexion con el VideoCapture retorna true si la conexion
     * fue exitosa false en caso contrario
     */
    public boolean conectarCamara() {
        boolean exito = false;
        //Pregunta si la conexion con la camara esta abierta
        if (cap.isOpened()) {
            exito = true;
        }
        return exito;
    }

    //Obtiene una imagen de la camara ip y la convierte en formato MAT
    public Mat getImagen() {
        Mat imagenActual = new Mat();
        Mat retorno = null;
        //Obtengo la imagen actual de la camara
        cap.read(imagenActual);
        //Pregunta si la imagen no esta vacia
        if (!imagenActual.empty()) {
            //Pregunto si la imagen anterior es nula
            //Preguno si hay movimiento
            retorno = getMovimiento(imagen, imagenActual);
            imagen = imagenActual;
        } else {
            //Si la imagen actual esta vacia devuelvo nulo
            imagen = null;
        }
        return retorno;
    }

    /*
        Devuelve verdadero si detecto movimiento en la imagenActual con respecto a la imagenAnterior
     */
    public Mat getMovimiento(Mat imagenAnterior, Mat imagenActual) {
        Mat retorno;
        if (!imagenAnterior.empty()) {
            Mat _kernel = getStructuringElement(MORPH_RECT, new Size(3, 3),
                    new Point(1, 1));

            Mat imagenMovimiento = new Mat();

            //Se restan las imagenes
            subtract(imagenActual, imagenAnterior, imagenMovimiento);
            //Reduce el ruido de fondo
            threshold(imagenMovimiento, imagenMovimiento, 15, 255, THRESH_BINARY);
            morphologyEx(imagenMovimiento, imagenMovimiento, MORPH_OPEN, _kernel, 
                    new Point(-1, -1), 1);
            //Se convierte la imagen a escala de grises
            cvtColor(imagenMovimiento, imagenMovimiento, Imgproc.COLOR_BGR2GRAY);

            //Lista que guarda los puntos de los contornos
            LinkedList<MatOfPoint> contornos = new LinkedList<>();
            Mat jerarquia = new Mat();
             
            //Detecta los contornos
            findContours(imagenMovimiento, contornos,
                jerarquia, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);

            double a;
            double areaMayor = 0;
            int pos = 0;
            for (int i = 0; i < contornos.size(); i++) {
                a = contourArea(contornos.get(i), false);
                if (a > areaMayor) {
                    areaMayor = a;
                    pos = i;
                }
            }
             retorno = imagenAnterior;
            if (contornos.size() > 0) {
                //Si existe algun movimiento salta la bandera
                //Y asigno a la imagen del capturador una nueva imagen 
                //en donde se encuentra el movimiento
                //retorno = new Mat(imagenActual, boundingRect(contornos.get(pos)));
                retorno = imagenActual;
            }
        }
        else{
            retorno = imagenActual; 
        }
        return retorno;
    }

    public void setNroCamara(int nroCamara) {
        this.nroCamara = nroCamara;
        cap = new VideoCapture(nroCamara);
    }

    public void setIpCamara(String ipCamara) {
        this.ipCamara = ipCamara;
        cap = new VideoCapture("\"http://" + ipCamara + "/video?x.mjpeg");
    }


}
