package Deteccion;

/**
 * Esta clase hace la captura de datos a través de una cámara IP o una camara
 * consectada al PC.
 *
// */
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Size;
import static org.bytedeco.javacpp.opencv_core.subtract;
import org.bytedeco.javacpp.opencv_highgui;
import org.bytedeco.javacpp.opencv_imgproc;
import static org.bytedeco.javacpp.opencv_imgproc.CHAIN_APPROX_SIMPLE;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_MOP_OPEN;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_BINARY;
import static org.bytedeco.javacpp.opencv_imgproc.MORPH_RECT;
import static org.bytedeco.javacpp.opencv_imgproc.RETR_CCOMP;
import static org.bytedeco.javacpp.opencv_imgproc.boundingRect;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.getStructuringElement;
import static org.bytedeco.javacpp.opencv_imgproc.threshold;
import org.bytedeco.javacpp.opencv_videoio.VideoCapture;

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
        Mat retorno = null;
        if (!imagenAnterior.empty()) {
            Mat _kernel = getStructuringElement(MORPH_RECT, new Size(3, 3),
                    new Point(1, 1));

            Mat imagenMovimiento = new Mat();

            //Se restan las imagenes
            subtract(imagenActual, imagenAnterior, imagenMovimiento);
            //Reduce el ruido de fondo
            threshold(imagenMovimiento, imagenMovimiento, 15, 255, CV_MOP_OPEN);
            opencv_imgproc.morphologyEx(imagenMovimiento, imagenMovimiento, CV_THRESH_BINARY, _kernel);
            //Se convierte la imagen a escala de grises
            cvtColor(imagenMovimiento, imagenMovimiento, CV_BGR2GRAY);

            //Lista que guarda los puntos de los contornos
            MatVector contornos = new MatVector();
            Mat jerarquia = new Mat();

            //Detecta los contornos
            opencv_imgproc.findContours(imagenMovimiento, contornos,
                    jerarquia, RETR_CCOMP, CHAIN_APPROX_SIMPLE);

            double a;
            double areaMayor = 0;
            int pos = 0;
            for (int i = 0; i < contornos.size(); i++) {
                a = opencv_imgproc.contourArea(contornos.get(i), false);
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
