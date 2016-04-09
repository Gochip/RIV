package Deteccion;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import static java.lang.Math.atan2;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.indexer.DoubleIndexer;
import static org.bytedeco.javacpp.opencv_core.CV_64FC3;
import static org.bytedeco.javacpp.opencv_core.CV_8U;
import static org.bytedeco.javacpp.opencv_core.CV_PI;
import org.bytedeco.javacpp.opencv_core.CvMat;
import org.bytedeco.javacpp.opencv_core.CvType;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Point2f;
import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_core.RectVector;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_core.Size;
import static org.bytedeco.javacpp.opencv_core.cvRound;
import static org.bytedeco.javacpp.opencv_core.cvSet2D;
import static org.bytedeco.javacpp.opencv_highgui.imshow;
import org.bytedeco.javacpp.opencv_imgcodecs;
import static org.bytedeco.javacpp.opencv_imgcodecs.imencode;
import static org.bytedeco.javacpp.opencv_imgcodecs.imwrite;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.equalizeHist;
import static org.bytedeco.javacpp.opencv_imgproc.getRotationMatrix2D;
import static org.bytedeco.javacpp.opencv_imgproc.resize;
import static org.bytedeco.javacpp.opencv_imgproc.warpAffine;
import static org.bytedeco.javacpp.opencv_objdetect.CASCADE_DO_ROUGH_SEARCH;
import static org.bytedeco.javacpp.opencv_objdetect.CASCADE_FIND_BIGGEST_OBJECT;
import org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;
import org.bytedeco.javacv.FrameConverter;

public class ReconocedorCara {

    private final CascadeClassifier clasificadorCara;
    private final CascadeClassifier clasificadorOjoDerecho;
    private final CascadeClassifier clasificadorOjoIzquierdo;

    private final int caraAncho;
    private final int caraAlto;
    private final double DESEADO_OJO_IZQUIERDO_X;
    private final double DESEADO_OJO_IZQUIERDO_Y;
    private final double EYE_SX;
    private final double EYE_SY;
    private final double EYE_SW;
    private final double EYE_SH;

    private final Size minDeteccionCara;
    private final Size maxDeteccionCara;

    private final int reducirTamaño;

    public ReconocedorCara() {
        //Usados para estabilizar la imagen
        this.caraAncho = 100;
        this.caraAlto = 100;
        this.DESEADO_OJO_IZQUIERDO_X = 0.19;
        this.DESEADO_OJO_IZQUIERDO_Y = 0.14;

        //Usados para detectar las coordenadas de los ojos
        this.EYE_SX = 0.12;
        this.EYE_SY = 0.17;
        this.EYE_SW = 0.37;
        this.EYE_SH = 0.36;

        //Clasificadores para la cara y los ojos
        clasificadorCara = new CascadeClassifier("src/clasificadores/haarcascade_frontalface_alt_tree.xml");
        clasificadorOjoIzquierdo = new CascadeClassifier("src/clasificadores/haarcascade_eye_tree_eyeglasses.xml");
        clasificadorOjoDerecho = new CascadeClassifier("src/clasificadores/haarcascade_eye_tree_eyeglasses.xml");

        //Tamaño a reducir la imagen para normalizar
        reducirTamaño = 480;

        //Se asigna los tamaños minimos a detectar de cara
        minDeteccionCara = new Size(30, 30);
        maxDeteccionCara = new Size(300, 300);
    }

    /*
     * Recibe una imagen Mar y retorna un ArrayList con las caras validas detectadas en el,
     * una cara se considera valida si ademas de haber sido reconocido positivo como por su clasificador
     * se pueden reconocer sus dos ojos
     */
    public ArrayList<Cara> detectarCaras(Mat imagen) {
        ArrayList<Cara> carasValidas = new ArrayList<>();
        RectVector vectorCaras = new RectVector();

        Mat imagenNormalizada = this.normalizarImagen(imagen);
        //Se detectan las caras en la imagen y se guarda en un vecot de rectas con las posiciones
        clasificadorCara.detectMultiScale(imagenNormalizada, vectorCaras, 1.1, 3,
                CASCADE_FIND_BIGGEST_OBJECT | CASCADE_DO_ROUGH_SEARCH, minDeteccionCara, maxDeteccionCara);

        //Recorro todas las posibles caras detectadas
        Mat cara;
        Cara C;
        for (int i = 0; i < vectorCaras.size(); i++) {
            //Obtengo una cara clasificada
            cara = new Mat(imagen, vectorCaras.get(i));
            //Intento detectar los ojos
            C = this.detectarOjos(cara);
            if (C != null) {
                //Si se detectaron se agregan al ArrayList
                //Pero antes se estabiliza la imagen
                C.setImagen(this.getImagenEstabilizada(C.getImagen(), 
                        C.getOjoIzquierdo(), C.getOjoDerecho()));
                carasValidas.add(C);
            }
        }
        return carasValidas;
    }

    /*
     * Retorna un obteto cara si la cara pasada por parametro es valida
     */
    private Cara detectarOjos(Mat cara) {
        Cara C = null;
        //Obtiene las regiones donde detectar el ojo
        int izquierdaX = cvRound(cara.cols() * EYE_SX);
        int arribaY = cvRound(cara.rows() * EYE_SY);
        int anchoX = cvRound(cara.cols() * EYE_SW);
        int altoY = cvRound(cara.rows() * EYE_SH);
        int derechaX = cvRound(cara.cols() * (1.0 - EYE_SX - EYE_SW));

        //Obtengo las imagenes de las partes de la cara donde se ecnuentran los ojos
        Mat arribaIzquierdaCara = new Mat(cara, new Rect(izquierdaX, arribaY, anchoX, altoY));
        Mat arribaDerechaCara = new Mat(cara, new Rect(derechaX, arribaY, anchoX, altoY));

        RectVector vectorOjoIzquierdo = new RectVector();
        RectVector vectorOjoDerecho = new RectVector();

        //Detecto los ojos en cada region
        clasificadorOjoIzquierdo.detectMultiScale(arribaIzquierdaCara, vectorOjoIzquierdo,
                1.1, 3, CASCADE_DO_ROUGH_SEARCH, new Size(0, 0),
                new Size(anchoX, altoY));
        clasificadorOjoDerecho.detectMultiScale(arribaDerechaCara, vectorOjoDerecho,
                1.1, 3, CASCADE_DO_ROUGH_SEARCH, new Size(0, 0),
                new Size(anchoX, altoY));

        //Pregunto si detecto correctamente los ojos
        if (vectorOjoDerecho.size() == 1 && vectorOjoIzquierdo.size() == 1) {

            //Obtengo las coordenadas de los ojos
            Rect ojoDCoor = vectorOjoDerecho.get(0);
            Rect ojoICoor = vectorOjoIzquierdo.get(0);

            //Modifico las coordenadas conrespecto a la cara
            ojoDCoor.x(ojoDCoor.x() + izquierdaX);
            ojoDCoor.y(ojoDCoor.y() + arribaY);

            ojoICoor.x(ojoICoor.x() + derechaX);
            ojoICoor.y(ojoICoor.y() + arribaY);
            C = new Cara(cara, ojoDCoor, ojoICoor);
        }

        return C;
    }

    /*
     * Retorna una imagen mat normalizada para su posterior analisis
     */
    private Mat normalizarImagen(Mat imagen) {
        Mat imagenNormalizada = new Mat();
        //Reduce el tamaño de la imagen para mejorar el rendimiento
        float escala = imagen.cols() / this.reducirTamaño;
        //Pregunta si la imagen es mayor al tamaño minimo
        if (imagen.cols() > this.reducirTamaño) {
            //Escala la imagen
            int escalarAlto = cvRound(imagen.rows() / escala);
            resize(imagen, imagenNormalizada, new Size(this.reducirTamaño, escalarAlto));
        }

        //Convierto la imagen a escala de grises
        cvtColor(imagenNormalizada, imagenNormalizada, CV_BGR2GRAY);
        //Aplico la ecuacion de histograma a la imagen para estandaraziar el contraste y el brillo
        equalizeHist(imagenNormalizada, imagenNormalizada);

        return imagenNormalizada;
    }

    /*
    * Funcion que rota y estabiliza una imagen
     */
    private Mat getImagenEstabilizada(Mat cara, Rect ojoIzquierdo, Rect ojoDerecho) {
        Mat imagen;

        Point izquierda = new Point(ojoIzquierdo.x() + ojoIzquierdo.width() / 2,
                ojoIzquierdo.y() + ojoIzquierdo.height() / 2);
        Point derecha = new Point(ojoDerecho.x() + ojoDerecho.width() / 2,
                ojoDerecho.y() + ojoDerecho.height() / 2);
        Point2f centroOjos = new Point2f((izquierda.x() + derecha.x()) * 0.5f,
                (izquierda.y() + derecha.y()) * 0.5f);

        // Obtiene el angulo entre los dos ojos
        double dy = (derecha.y() - izquierda.y());
        double dx = (derecha.x() - izquierda.x());
        double largo = sqrt(pow(dx,2) + pow(dy,2));
        double angulo = atan2(dy, dx) * 180.0 / CV_PI;

        // Mediciones a mano muestran que el centro del ojo izquierdo debe ser idealmente en 
        //aproximadamente ( 0.19 , 0.14 ) de una imagen de la cara a escala
        final double DESADO_OJO_DERECHO_X = (1 - DESEADO_OJO_IZQUIERDO_X);

        // Obtener la cantidad que necesitamos para escalar la imagen para ser el tamaño fijo 
        //deseado que queremos
        double deseadoLargo = (DESADO_OJO_DERECHO_X - DESEADO_OJO_IZQUIERDO_X) * caraAncho;
        double escala = deseadoLargo / largo;

        // Obtener la matriz de transformación para rotar y escalar la cara al ángulo y tamaño deseado
        Mat rot_mat = getRotationMatrix2D(centroOjos, angulo, escala);

        // Desplazar el centro de los ojos para ser el centro deseado entre los ojos 
        DoubleIndexer matIdx = rot_mat.createIndexer();
        matIdx.put(0, 2, matIdx.get(0, 2)+(caraAncho * 0.5f) - centroOjos.x());
        matIdx.put(1, 2, matIdx.get(1, 2)+(caraAlto * DESEADO_OJO_IZQUIERDO_Y - centroOjos.y()));

        imagen = new Mat(caraAlto, caraAncho, CV_8U, new Scalar(128));

        warpAffine(cara, imagen, rot_mat, imagen.size());

        return imagen;
    }
    
    /*
     *Convierte una imagen Mat(formato de opencv) a Image(formato de java)
     */
    public Image convertir(Mat imagen) {
        BytePointer byteBuffer = new BytePointer();
        imencode(".jpg", imagen, byteBuffer);
        
        byte[] byteArray = byteBuffer.getStringBytes();
        BufferedImage bufImage = null;
        
        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);
        } catch (Exception e) {
            System.out.println(e);
        }
        return (Image) bufImage;
    }

}
