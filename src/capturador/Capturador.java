package capturador;

/**
 * Esta clase hace la captura de datos a través de una cámara IP.
 * @author Parisi Germán
 */
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import static org.opencv.core.Core.subtract;
import static org.opencv.core.CvType.CV_8UC3;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import static org.opencv.imgproc.Imgproc.MORPH_RECT;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.findContours;
import static org.opencv.imgproc.Imgproc.getStructuringElement;
import static org.opencv.imgproc.Imgproc.morphologyEx;
import static org.opencv.imgproc.Imgproc.threshold;
import org.opencv.videoio.VideoCapture;

public class Capturador {
	
    private int nroCamara;
    private String ipCamara;
    private VideoCapture cap;
    private Mat imagen;

    public Capturador() {
        cap= new VideoCapture();
    }

    //Crea un capturador pasandole un string con la ip de la camara
    public Capturador(String ipCamara) {
        this.ipCamara = ipCamara;
    }

    //Crea un capturador pasandole un int con el numero de la camara
    public Capturador(int nroCamara) {
        this.nroCamara = nroCamara;
        cap = new VideoCapture(nroCamara);
    }
    
    //Obtiene una imagen de la camara ip y la convierte en format0 MAT
    public Mat obtenerImagenIpCamara() {
        Mat m = null;
        try {
            //Lee la imagen de la direccion
            BufferedImage img = ImageIO.read(new URL("http://" + ipCamara + "/photo.jpg"));
           
            byte[] pixeles = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
            m = new Mat(img.getHeight(), img.getWidth(), CV_8UC3);
            m.put(0, 0, pixeles);
            
        } catch (IOException ex) {
            System.out.println("Error de lectura " + ex);
        }

        return m;
    }

    //Obtiene una imagen de la camara asignada
    public Mat obtenerImagen() {
        Mat imagenActual = new Mat();

        //Pregunto si la camara esta abierta
        if (cap.isOpened()) {

            //Obtengo la imagen actual de la camara
            cap.read(imagenActual);
            //Pregunta si la imagen no esta vacia
            if (!imagenActual.empty()) {
                //Pregunto si la imagen anterior es nula
                if (imagen != null) {
                    //Preguno si hay movimiento
                    if (hayMovimiento(imagen,imagenActual)) {
                        //Si hay se asigna la imagen actual como
                        imagen = imagenActual;
                    }
                } else {
                    //Si la imagen anterior es igual a null se asigna auotmaticamente 
                    //la capturada actualmente
                    imagen = imagenActual;
                }
            } else {
                //Si la imagen actual esta vacia devuelvo nulo
                imagen = null;
            }
        } else {
            System.out.println("Camara no reconocida");
        }
        return imagen;
    }

    //Devuelve verdadero si detecto movimiento en la imagen con respecto a la anterior
    public boolean hayMovimiento(Mat imagenAnterior, Mat imagenActual) {
        boolean movimiento = false;
        Mat _kernel = getStructuringElement(MORPH_RECT, new Size(3, 3),
                new Point(1, 1));

        Mat imagenMovimiento = new Mat();

        //Se restan las imagenes
        subtract(imagenActual, imagenAnterior, imagenMovimiento);
        //Reduce el ruido de fondo
        threshold(imagenMovimiento, imagenMovimiento, 15, 255, THRESH_BINARY);
        morphologyEx(imagenMovimiento, imagenMovimiento, Imgproc.MORPH_OPEN,
                _kernel, new Point(-1, -1), 1);
        //Se convierte la imagen a escala de grises
        cvtColor(imagenMovimiento, imagenMovimiento, Imgproc.COLOR_BGR2GRAY);

        //Lista que guarda los puntos de los contornos
        LinkedList<MatOfPoint> contornos = new LinkedList<>();
        Mat jerarquia = new Mat();

        //Detecta los contornos
        findContours(imagenMovimiento, contornos,
                jerarquia, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);

        if (contornos.size() > 0) {
            //Si existe algun movimiento salta la bandera
            movimiento = true;
        }
        return movimiento;
    }
    
    public void setNroCamara(int nroCamara){
        this.nroCamara = nroCamara;
    }

    public void setIpCamara(String ipCamara){
        this.ipCamara = ipCamara;
    }
    
     public Mat getImagen() {
        return imagen;
    }

    public void setImagen(Mat imagen) {
        this.imagen = imagen;
    }
}
