package Deteccion;

import java.util.ArrayList;
import static org.opencv.core.CvType.CV_8U;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.equalizeHist;
import static org.opencv.imgproc.Imgproc.getRotationMatrix2D;
import static org.opencv.imgproc.Imgproc.rectangle;
import static org.opencv.imgproc.Imgproc.resize;
import static org.opencv.imgproc.Imgproc.warpAffine;
import org.opencv.objdetect.CascadeClassifier;

/*
    Esta clase reconoce una cara valida en una imagen
*/
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
        //haarcascade_eye_tree_eyeglasses.xml
//        this.EYE_SX = 0.12;
//        this.EYE_SY = 0.17;
//        this.EYE_SW = 0.37;
//        this.EYE_SH = 0.36;
        //haarcascade_eye.xml
        this.EYE_SX = 0.16;
        this.EYE_SY = 0.26;
        this.EYE_SW = 0.30;
        this.EYE_SH = 0.28;

        //haarcascade_mcs_lefteye.xml
//        this.EYE_SX = 0.10;
//        this.EYE_SY = 0.19;
//        this.EYE_SW = 0.40;
//        this.EYE_SH = 0.36;
        //haarcascade_lefteye_2splits.xml
//        this.EYE_SX = 0.12;
//        this.EYE_SY = 0.17;
//        this.EYE_SW = 0.37;
//        this.EYE_SH = 0.36;
        //Clasificadores para la cara y los ojos
        clasificadorCara = new CascadeClassifier("src/clasificadores/haarcascade_frontalface_alt2.xml");
        clasificadorOjoIzquierdo = new CascadeClassifier("src/clasificadores/haarcascade_eye.xml");
        clasificadorOjoDerecho = new CascadeClassifier("src/clasificadores/haarcascade_eye.xml");

        //Tamaño a reducir la imagen para normalizar
        reducirTamaño = 480;

        //Se asigna los tamaños minimos a detectar de cara
        minDeteccionCara = new Size(30, 30);
        maxDeteccionCara = new Size(480, 480);
    }

    /*
     * Recibe una imagen Mar y retorna un ArrayList con las caras validas detectadas en el,
     * una cara se considera valida si ademas de haber sido reconocido positivo como por su clasificador
     * se pueden reconocer sus dos ojos
     */
    public ArrayList<Cara> detectarCaras(Mat imagen) {
        ArrayList<Cara> carasValidas = new ArrayList<>();
        MatOfRect vectorCaras = new MatOfRect();

        Mat imagenNormalizada = this.normalizarImagen(imagen);
        //Se detectan las caras en la imagen y se guarda en un vecot de rectas con las posiciones
        clasificadorCara.detectMultiScale(imagenNormalizada, vectorCaras, 1.2, 3,
                0, minDeteccionCara, maxDeteccionCara);

        //Recorro todas las posibles caras detectadas
        Mat cara;
        Cara C;
        for (Rect rect : vectorCaras.toArray()) {
            //Obtengo una cara clasificada
            cara = new Mat(imagenNormalizada, rect);
            rectangle(imagen, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0, 255, 255));

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
        int izquierdaX = round(cara.cols() * EYE_SX);
        int arribaY = round(cara.rows() * EYE_SY);
        int anchoX = round(cara.cols() * EYE_SW);
        int altoY = round(cara.rows() * EYE_SH);
        int derechaX = round(cara.cols() * (1.0 - EYE_SX - EYE_SW));

        //Obtengo las imagenes de las partes de la cara donde se ecnuentran los ojos
        Mat arribaIzquierdaCara = new Mat(cara, new Rect(izquierdaX, arribaY, anchoX, altoY));
        Mat arribaDerechaCara = new Mat(cara, new Rect(derechaX, arribaY, anchoX, altoY));

        rectangle(cara, new Point(izquierdaX, arribaY), new Point(anchoX + izquierdaX, arribaY + altoY),
                new Scalar(0, 255, 255));
        rectangle(cara, new Point(derechaX, arribaY), new Point(anchoX + derechaX, arribaY + altoY),
                new Scalar(0, 255, 255));

        MatOfRect vectorOjoIzquierdo = new MatOfRect();
        MatOfRect vectorOjoDerecho = new MatOfRect();

        //Detecto los ojos en cada region
        clasificadorOjoIzquierdo.detectMultiScale(arribaIzquierdaCara, vectorOjoIzquierdo);
        clasificadorOjoDerecho.detectMultiScale(arribaDerechaCara, vectorOjoDerecho);

        //Pregunto si detecto correctamente los ojos
        if (vectorOjoDerecho.toArray().length == 1 && vectorOjoIzquierdo.toArray().length == 1) {

            //Obtengo las coordenadas de los ojos
            Rect ojoDCoor = vectorOjoDerecho.toArray()[0];
            Rect ojoICoor = vectorOjoIzquierdo.toArray()[0];

            //Modifico las coordenadas conrespecto a la cara
            ojoDCoor.x += izquierdaX;
            ojoDCoor.y += arribaY;

            ojoICoor.x += derechaX;
            ojoICoor.y += arribaY;

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
            int escalarAlto = round(imagen.rows() / escala);
            resize(imagen, imagenNormalizada, new Size(this.reducirTamaño, escalarAlto));
        }

        //Convierto la imagen a escala de grises
        cvtColor(imagenNormalizada, imagenNormalizada, Imgproc.COLOR_BGR2GRAY);
        //Aplico la ecuacion de histograma a la imagen para estandaraziar el contraste y el brillo
        equalizeHist(imagenNormalizada, imagenNormalizada);

        return imagenNormalizada;
    }

    /*
    * Funcion que rota y estabiliza una imagen
     */
    private Mat getImagenEstabilizada(Mat cara, Rect ojoDerecho, Rect ojoIzquierdo) {
        Mat imagen;

        Point izquierda = new Point(ojoIzquierdo.x + ojoIzquierdo.width / 2, ojoIzquierdo.y + ojoIzquierdo.height / 2);
        Point derecha = new Point(ojoDerecho.x + ojoDerecho.width / 2, ojoDerecho.y + ojoDerecho.height / 2);
        Point centroOjos = new Point((izquierda.x + derecha.x) * 0.5, (izquierda.y + derecha.y) * 0.5);

        // Obtiene el angulo entre los dos ojos
        double dy = (derecha.y - izquierda.y);
        double dx = (derecha.x - izquierda.x);
        double largo = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        double angulo = Math.atan2(dy, dx) * 180 / Math.PI;

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
        
        double[] aux = rot_mat.get(0, 2);
        double[] aux2 = rot_mat.get(1, 2);
        for (int i = 0; i < aux.length; i++) {
            aux[i] += caraAncho * 0.5 - centroOjos.x;
            aux2[i] += caraAlto * DESEADO_OJO_IZQUIERDO_Y - centroOjos.y;
        }
        rot_mat.put(0, 2,aux);
        rot_mat.put(1, 2,aux2);
        
        imagen = new Mat(caraAlto, caraAncho, CV_8U, new Scalar(128));
        warpAffine(cara, imagen, rot_mat, imagen.size());

        return imagen;
    }

    /*
     *Redondea al int mas cercano
     */
    private int round(double d) {
        double dAbs = Math.abs(d);
        int i = (int) dAbs;
        double result = dAbs - (double) i;
        if (result < 0.5) {
            return d < 0 ? -i : i;
        } else {
            return d < 0 ? -(i + 1) : i + 1;
        }
    }

}
